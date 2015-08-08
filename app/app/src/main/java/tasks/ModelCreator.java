package tasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import model.UWDatabaseModel;

/**
 * Created by Fechner on 6/17/15.
 */
public class ModelCreator{

    private static final String TAG = "ModelCreationTask";

    private ModelCreationTaskListener listener;
    private final UWDatabaseModel dbModel;
    private final UWDatabaseModel parentOrNull;

    public ModelCreator(UWDatabaseModel dbModel, UWDatabaseModel parentSlugOrNull, ModelCreationTaskListener listener) {

        this.listener = listener;

        this.dbModel = (dbModel != null)? dbModel : null;
        this.parentOrNull = (parentSlugOrNull != null)? parentSlugOrNull : null;
    }

    public void execute(JSONObject obj){

        if(parentOrNull != null){
            UWDatabaseModel finalModel = dbModel.setupModelFromJson(obj, parentOrNull);
            listener.modelWasCreated(finalModel);
        }
        else {
            UWDatabaseModel finalModel = dbModel.setupModelFromJson(obj);
            listener.modelWasCreated(finalModel);
        }
    }

    interface ModelCreationTaskListener {
        void modelWasCreated(UWDatabaseModel model);
    }
}
