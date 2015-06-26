package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.StoriesChapter;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateStoriesChaptersRunnable implements Runnable{

    private static final String TAG = "UpdateVersionsRunnable";
    public static final String FRAMES_JSON_KEY = "frames";

    private JSONArray jsonModels;
    private UWUpdater updater;
    private Book parent;

    public UpdateStoriesChaptersRunnable(JSONArray jsonModels, UWUpdater updater, Book parent) {
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

        new ModelCreationTask(new StoriesChapter(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof StoriesChapter) {

                    new StoriesChapterSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                        @Override
                        public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {

                            if(shouldContinueUpdate != null){
                                updatePages(jsonObject, (StoriesChapter) shouldContinueUpdate);
                            }
                        }
                    }
                    ).execute(model);
                }
            }
        }).execute(jsonObject);
    }

    private void updatePages(JSONObject project, StoriesChapter pageParent){

        Log.d(TAG, "StoriesChapter created or updated: " + parent.toString());
        try{
            JSONArray pages = project.getJSONArray(FRAMES_JSON_KEY);
            UpdateStoryPagesRunnable runnable = new UpdateStoryPagesRunnable(pages, updater, pageParent);
            updater.mServiceHandler.post(runnable);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class StoriesChapterSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public StoriesChapterSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return StoriesChapter.getModelForSlug(slug, session);
        }
    }
}
