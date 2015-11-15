/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;


import org.json.JSONObject;

import model.UWDatabaseModel;

/**
 * Created by PJ Fechner on 6/17/15.
 * Class for creating a UWDatabaseModel
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

    public UWDatabaseModel run(JSONObject obj){

        return (parentOrNull != null)? dbModel.setupModelFromJson(obj, parentOrNull) : dbModel.setupModelFromJson(obj);
    }

    interface ModelCreationListener {
        /**
         * Called when the model is finished being created
         * @param model model that was created
         */
        void modelWasCreated(UWDatabaseModel model);
    }
}
