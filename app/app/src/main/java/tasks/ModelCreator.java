package tasks;


import org.json.JSONObject;

import model.UWDatabaseModel;

/**
 * Created by Fechner on 6/17/15.
 */
public class ModelCreator{

    private static final String TAG = "ModelCreationTask";

    private ModelCreationListener listener;
    private final UWDatabaseModel dbModel;
    private final UWDatabaseModel parentOrNull;

    public ModelCreator(UWDatabaseModel dbModel, UWDatabaseModel parentSlugOrNull, ModelCreationListener listener) {

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

    interface ModelCreationListener {
        void modelWasCreated(UWDatabaseModel model);
    }
}
