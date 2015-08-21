package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.StoriesChapter;
import services.UWUpdaterService;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateStoriesChaptersRunnable implements Runnable{

    private static final String TAG = "UpdateStrysChapsRunbl";
    public static final String FRAMES_JSON_KEY = "frames";

    private JSONArray jsonModels;
    private UWUpdaterService updater;
    private Book parent;

    public UpdateStoriesChaptersRunnable(JSONArray jsonModels, UWUpdaterService updater, Book parent) {
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

    private void updateModel(final JSONObject jsonObject, final boolean lastModel){

        new ModelCreator(new StoriesChapter(), parent, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof StoriesChapter) {

                    UWDatabaseModel shouldContinueUpdate =  new StoriesChapterSaveOrUpdater(updater.getApplicationContext()).start(model);

                        if(shouldContinueUpdate != null){
                            updatePages(jsonObject, (StoriesChapter) shouldContinueUpdate);
                        }
                        if(lastModel){
                            updater.runnableFinished();
                        }
                    }
                }
        }).execute(jsonObject);
    }

    private void updatePages(JSONObject project, StoriesChapter pageParent){

        try{
            JSONArray pages = project.getJSONArray(FRAMES_JSON_KEY);
            UpdateStoryPagesRunnable runnable = new UpdateStoryPagesRunnable(pages, updater, pageParent);
            updater.addRunnable(runnable, 6);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class StoriesChapterSaveOrUpdater extends ModelSaveOrUpdater{

        public StoriesChapterSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return StoriesChapter.getModelForUniqueSlug(slug, session);
        }
    }
}
