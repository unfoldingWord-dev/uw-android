package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateStoryPagesRunnable implements Runnable{

    private static final String TAG = "UpdateStoryPgsRunnable";

    private JSONArray jsonModels;
    private UWUpdater updater;
    private StoriesChapter parent;

    public UpdateStoryPagesRunnable(JSONArray jsonModels, UWUpdater updater, StoriesChapter parent) {
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

        UWDatabaseModel model = new ModelCreator(new StoryPage(), parent).start(jsonObject);

        if(model instanceof StoryPage) {
            new StoriesPageSaveOrUpdater(updater.getApplicationContext()).start(model);
        }

        if(isLast) {
            updater.runnableFinished();
        }
    }

    private class StoriesPageSaveOrUpdater extends ModelSaveOrUpdater{

        public StoriesPageSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return StoryPage.getModelForSlug(slug, session);
        }
    }
}
