package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.DaoSession;
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
                updateModel(models.getJSONObject(i), i == (models.length() - 1));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(final JSONObject jsonObject, final boolean isLast){

        new ModelCreationTask(new Book(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Book) {

                    new BookSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                        @Override
                        public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {

//                            Log.d(TAG, "Book created");
                            if(shouldContinueUpdate != null){
                                updateChapters (jsonObject, (Book) shouldContinueUpdate);
                            }
                            if(isLast){
                                updater.runnableFinished();
                            }
                        }
                    }).execute( model);
                }
            }
        }).execute(jsonObject);
    }

    private void updateChapters(JSONObject book, Book parent){

        boolean isSideLoaded = (book.has("saved_content"));

        // TODO: sideloaded info
        if(isSideLoaded){

        }
        else if( (parent.getBibleChapters() != null && parent.getBibleChapters().size() > 0)
                && (parent.getStoryChapters() != null && parent.getStoryChapters().size() > 0) ) {

            updater.addRunnable(new UpdateBookContentRunnable(parent, updater), 4);
        }
    }



    private class BookSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public BookSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Book.getModelForUniqueSlug(slug, session);
        }
    }

}
