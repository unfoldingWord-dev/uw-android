package tasks;

import android.content.Context;
import android.util.Log;

import org.apache.http.util.LangUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.Language;
import model.daoModels.Project;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateLanguagesRunnable implements Runnable{

    private static final String TAG = "UpdateLanguagesRunnable";
    public static final String VERSIONS_JSON_KEY = "vers";
    private JSONArray jsonModels;
    private UWUpdater updater;
    private Project parent;

    public UpdateLanguagesRunnable(JSONArray jsonModels, UWUpdater updater, Project parent) {
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

    private void updateModel(final JSONObject jsonModel){

        new ModelCreationTask(new Language(), parent, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Language) {

                    new LanguageSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener() {
                        @Override
                        public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {
                            if(shouldContinueUpdate != null){
                                updateVersions(jsonModel, (Language) shouldContinueUpdate);
                            }
                        }
                    }).execute(model);
                }
            }
        }).execute(jsonModel);
    }

    private void updateVersions(JSONObject language, Language parent){

        Log.d(TAG, "Language created or updated: " + parent.toString());
        try{
            JSONArray versions = language.getJSONArray(VERSIONS_JSON_KEY);
            UpdateVersionsRunnable runnable = new UpdateVersionsRunnable(versions, updater, parent);
            updater.mServiceHandler.post(runnable);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class LanguageSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public LanguageSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Language.getModelForSlug(slug, session);
        }
    }

}
