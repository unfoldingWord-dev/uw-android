package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.Language;
import model.daoModels.Project;
import model.daoModels.Version;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateBooksRunnable implements Runnable{

    private static final String TAG = "UpdateBooksRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    private JSONArray jsonModels;
    private UWUpdater updater;
    private Version parent;

    public UpdateBooksRunnable(JSONArray jsonModels, UWUpdater updater, Version parent) {
        this.jsonModels = jsonModels;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        parseModels(jsonModels);
    }

    private void parseModels(JSONArray models){

        for(int i = 0; i < models.length(); i++){

            try {
                updateModel(models.getJSONObject(i));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(final JSONObject jsonObject){

        new ModelCreationTask(new Book(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Book) {

                    new BookSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                        @Override
                        public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {

                            if(shouldContinueUpdate != null){
                                updateChapters (jsonObject, (Book) shouldContinueUpdate);
                            }
                        }
                    }).execute( model);
                }
            }
        }).execute(jsonObject);
    }

    private void updateChapters(JSONObject book, Book parent){

        boolean isSideLoaded = (book.has("saved_content"));
        boolean isUsfm = parent.getSourceUrl().contains("usfm");

        if(isSideLoaded){

        }
        else if (isUsfm){
            updateUsfm(parent);
        }
        else{
            updateStories(parent);
        }
    }

    private void updateUsfm(Book parent){

        if(parent.getBibleChapters() != null && parent.getBibleChapters().size() > 0){

            new DownloadTask(new DownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                }
            }).execute(parent.getSourceUrl());
        }
    }

    private void updateStories(final Book parent){

        if(parent.getStoryChapters() != null && parent.getStoryChapters().size() > 0){

            new DownloadTask(new DownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try {
                        UpdateStoriesChaptersRunnable runnable = new UpdateStoriesChaptersRunnable(new JSONObject(jsonString).getJSONArray(CHAPTERS_JSON_KEY), updater, parent);
                        updater.mServiceHandler.post(runnable);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }).execute(parent.getSourceUrl());
        }
    }

    private class BookSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public BookSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Book.getModelForSlug(slug, session);
        }
    }

}
