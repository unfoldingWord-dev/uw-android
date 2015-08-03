package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.Language;
import model.daoModels.Version;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateVersionsRunnable implements Runnable{

    private static final String TAG = "UpdateVersionsRunnable";
    public static final String BOOKS_JSON_KEY = "toc";

    private JSONArray jsonModels;
    private UWUpdater updater;
    private Language parent;

    public UpdateVersionsRunnable(JSONArray jsonModels, UWUpdater updater, Language parent) {
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

        new ModelCreationTask(new Version(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Version) {

                    new VersionSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                        @Override
                        public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {

                            Log.d(TAG, "version created");
                            if(shouldContinueUpdate != null){
                                updateBooks(jsonObject, (Version) shouldContinueUpdate);
                            }
                            if(isLast){
                                updater.runnableFinished();
                            }
                        }
                    }
                    ).execute(model);
                }
            }
        }).execute(jsonObject);
    }

    private void updateBooks(JSONObject project, Version parent){

        try{
            JSONArray languages = project.getJSONArray(BOOKS_JSON_KEY);
            UpdateBooksRunnable runnable = new UpdateBooksRunnable(languages, updater, parent);
            updater.addRunnable(runnable, 2);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class VersionSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public VersionSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Version.getModelForUniqueSlug(slug, session);
        }
    }
}
