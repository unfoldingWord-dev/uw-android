/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.support.annotation.Nullable;

import org.unfoldingword.mobile.R;

import model.daoModels.DaoMaster;
import model.daoModels.DaoSession;

/**
 * Created by Fechner on 4/28/15.
 */
public class DaoDBHelper {

    static private DaoMaster daoMaster;

    static private DaoMaster getDaoMaster(Context context) {
        if(daoMaster == null) {
            DatabaseOpenHelper helper = DatabaseOpenHelper.getSharedInstance(context,
                    context.getResources().getString(R.string.database_name), null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
            helper.upgradeIfNeeded();
        }
        return daoMaster;
    }

    /**
     * @param context context of the application
     * @return a new DaoSession attached to the passed context
     */
    static public DaoSession getDaoSession(Context context){
        return getDaoMaster(context).newSession();
    }

    static public void getDaoSession(Context context, AsynchronousDatabaseAccessorCompletion completion) {

        DatabaseOpenHelper helper = DatabaseOpenHelper.getSharedInstance(context,
                context.getResources().getString(R.string.database_name), null);
        int tries = 0;
        while (tries < 10) {
            try {
                DaoSession session = getDaoMaster(context).newSession();
                completion.loadedSession(session);
                return;
            } catch (SQLiteDatabaseLockedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tries += 1;
        }
        completion.loadedSession(null);
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

    public interface AsynchronousDatabaseAccessorCompletion {
        void loadedSession(@Nullable DaoSession session);
    }
}
