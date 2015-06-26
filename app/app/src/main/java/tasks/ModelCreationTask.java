package tasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import model.UWDatabaseModel;

/**
 * Created by Fechner on 6/17/15.
 */
public class ModelCreationTask extends AsyncTask<JSONObject, Void, UWDatabaseModel> {

    private static final String TAG = "ModelCreationTask";

    private ModelCreationTaskListener listener;
    private final UWDatabaseModel dbModel;
    private final UWDatabaseModel parentOrNull;

    public ModelCreationTask(UWDatabaseModel dbModel, UWDatabaseModel parentSlugOrNull, ModelCreationTaskListener listener) {

        this.listener = listener;

        this.dbModel = (dbModel != null)? dbModel : null;
        this.parentOrNull = (parentSlugOrNull != null)? parentSlugOrNull : null;
    }

    @Override
    protected UWDatabaseModel doInBackground(JSONObject... params) {

        if(parentOrNull != null){
            UWDatabaseModel finalModel = dbModel.setupModelFromJson(params[0], parentOrNull);
            return finalModel;
        }
        else {
            UWDatabaseModel finalModel = dbModel.setupModelFromJson(params[0]);
            return finalModel;
        }
    }

    @Override
    protected void onPostExecute(UWDatabaseModel uwDatabaseModel) {
        super.onPostExecute(uwDatabaseModel);
        listener.modelWasCreated(uwDatabaseModel);
    }

    interface ModelCreationTaskListener {
        void modelWasCreated(UWDatabaseModel model);
    }
}
