package model;

import android.content.Context;

import org.unfoldingword.mobile.R;

import model.daoModels.DaoMaster;
import model.daoModels.DaoSession;
import model.daoModels.Project;

/**
 * Created by Fechner on 4/28/15.
 */
public class DaoDBHelper {

    static private DaoMaster daoMaster;

    /**
     * @param context context of the application
     * @return a new DaoSession attached to the passed context
     */
    static public DaoSession getDaoSession(Context context){

        if(daoMaster == null) {
            DatabaseOpenHelper helper = new DatabaseOpenHelper(context,
                    context.getResources().getString(R.string.database_name), null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster.newSession();
    }

    /**
     * Will save the database to external Storage
     * @param context context of the application
     */
    static public void saveDatabase(Context context){

        DatabaseOpenHelper helper = new DatabaseOpenHelper(context,
                context.getResources().getString(R.string.database_name), null);
        helper.saveDatabase();
    }
}
