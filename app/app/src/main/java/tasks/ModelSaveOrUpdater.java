/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;


import android.content.Context;

import model.DaoDBHelper;
import model.UWDatabaseModel;
import model.daoModels.DaoSession;

/**
 * Created by PJ Fechner on 6/17/15.
 * abstract Class for saving or updating a UWDatabaseModel
 */
public abstract class ModelSaveOrUpdater {

    private static final String TAG = "ModelSaveOrUpdater";

    private final Context context;
    protected abstract UWDatabaseModel getExistingModel(String slug, DaoSession session);

    public ModelSaveOrUpdater(Context context) {
        this.context = context;
    }

    public UWDatabaseModel start(UWDatabaseModel model) {

        DaoSession session = DaoDBHelper.getDaoSession(context);
        UWDatabaseModel existingModel = getExistingModel(model.getUniqueSlug(), session);

        if(existingModel != null){
            if(existingModel.updateWithModel(model)){
//                Log.d(TAG, "Model updated and will update");
                return existingModel;
            }
            else{
//                Log.d(TAG, "Model updated and won't update");
                return null;
            }
        }
        else{
            model.insertModel(session);
//            Log.d(TAG, "Model created");
            return model;
        }
    }
}
