package tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import model.DaoDBHelper;
import model.UWDatabaseModel;
import model.daoModels.DaoSession;

/**
 * Created by Fechner on 6/17/15.
 */
public abstract class ModelSaveOrUpdateTask extends AsyncTask<UWDatabaseModel, Void, UWDatabaseModel> {

    private static final String TAG = "ModelSaveOrUpdateTask";

    private ModelCreationTaskListener listener;
    private final Context context;
    protected abstract UWDatabaseModel getExistingModel(String slug, DaoSession session);

    public ModelSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected UWDatabaseModel doInBackground(UWDatabaseModel... params) {

        DaoSession session = DaoDBHelper.getDaoSession(context);
        UWDatabaseModel newModel = params[0];
        UWDatabaseModel existingModel = getExistingModel(newModel.getSlug(), session);

        if(existingModel != null){
            if(existingModel.updateWithModel(newModel)){
//                Log.d(TAG, "Model updated and will update");
                return existingModel;
            }
            else{
//                Log.d(TAG, "Model updated and won't update");
                return null;
            }
        }
        else{
            newModel.insertModel(session);
//            Log.d(TAG, "Model created");
            return newModel;
        }
    }

    @Override
    protected void onPostExecute(UWDatabaseModel shouldContinueUpdate) {
        super.onPostExecute(shouldContinueUpdate);
        listener.modelWasUpdated(shouldContinueUpdate);
    }

    interface ModelCreationTaskListener {
        void modelWasUpdated(UWDatabaseModel shouldContinueUpdate);
    }
}
