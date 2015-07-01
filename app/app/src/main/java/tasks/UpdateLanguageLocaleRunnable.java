package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.LanguageLocale;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateLanguageLocaleRunnable implements Runnable{

    private static final String TAG = "UpdateLangLocaleRunnable";

    private JSONArray jsonModels;
    private UWUpdater updater;

    public UpdateLanguageLocaleRunnable(JSONArray jsonModels, UWUpdater updater) {
        this.jsonModels = jsonModels;
        this.updater = updater;
    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }
    private void parseModels(JSONArray models){

        Log.i(TAG, "Started Locales");
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

        new ModelCreationTask(new LanguageLocale(), null, new ModelCreationTask.ModelCreationTaskListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof LanguageLocale) {

                new LanguageLocaleSaveOrUpdateTask(updater.getApplicationContext(), new ModelSaveOrUpdateTask.ModelCreationTaskListener(){
                    @Override
                    public void modelWasUpdated(UWDatabaseModel shouldContinueUpdate) {


                        if(isLast){
                            Log.i(TAG, "Finished Locales");
                            updater.runnableFinished();
                        }
                    }
                }
                ).execute(model);
            }
            }
        }).execute(jsonObject);
    }

    private class LanguageLocaleSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public LanguageLocaleSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return LanguageLocale.getLocalForKey(slug, session);
        }
    }
}
