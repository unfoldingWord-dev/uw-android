package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.Language;
import model.daoModels.Project;
import services.UWUpdaterService;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateLanguagesRunnable implements Runnable{

    private static final String TAG = "UpdateLanguagesRunnable";
    public static final String VERSIONS_JSON_KEY = "vers";
    private JSONArray jsonModels;
    private UWUpdaterService updater;
    private Project parent;

    public UpdateLanguagesRunnable(JSONArray jsonModels, UWUpdaterService updater, Project parent) {
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

    private void updateModel(final JSONObject jsonModel, final boolean isLast){

        new ModelCreator(new Language(), parent, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Language) {

                    UWDatabaseModel shouldContinueUpdate = new LanguageSaveOrUpdater(updater.getApplicationContext()).start(model);

                    Log.d(TAG, "language created");

                    if(shouldContinueUpdate != null){
                        updateVersions(jsonModel, (Language) shouldContinueUpdate);
                    }
                    if(isLast){
                        updater.runnableFinished();
                    }
                }
            }
        }).execute(jsonModel);
    }

    private void updateVersions(JSONObject language, Language parent){

        try{
            JSONArray versions = language.getJSONArray(VERSIONS_JSON_KEY);
            UpdateVersionsRunnable runnable = new UpdateVersionsRunnable(versions, updater, parent);
            updater.addRunnable(runnable, 1);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class LanguageSaveOrUpdater extends ModelSaveOrUpdater{

        public LanguageSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Language.getModelForUniqueSlug(slug, session);
        }
    }

}
