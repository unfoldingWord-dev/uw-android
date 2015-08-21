package tasks;


import android.content.Context;

import model.DaoDBHelper;
import model.UWDatabaseModel;
import model.daoModels.DaoSession;

/**
 * Created by Fechner on 6/17/15.
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
        UWDatabaseModel newModel = model;
        UWDatabaseModel existingModel = getExistingModel(newModel.getUniqueSlug(), session);

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
}
