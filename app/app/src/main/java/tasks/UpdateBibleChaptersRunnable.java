package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateBibleChaptersRunnable implements Runnable{

    private static final String TAG = "UpdateVersionsRunnable";

    private String usfm;
    private UWUpdater updater;
    private StoriesChapter parent;

    public UpdateBibleChaptersRunnable(String usfm, UWUpdater updater, StoriesChapter parent) {
        this.usfm = usfm;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        parse(usfm);

    }
    private void parse(String usfm){

//        for(int i = 0; i < models.length(); i++){
//
//            try {
//                updateModel(models.getJSONObject(i));
//            }
//            catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
    }

    private void updateModel(final JSONObject jsonObject){

        new ModelCreationTask(new StoryPage(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                    if(model instanceof StoryPage) {

                    new StoriesPageSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                        @Override
                        public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {

                        }
                    }
                    ).execute(model);
                }
            }
        }).execute(jsonObject);
    }

    private class StoriesPageSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public StoriesPageSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return StoryPage.getModelForSlug(slug, session);
        }
    }
}
