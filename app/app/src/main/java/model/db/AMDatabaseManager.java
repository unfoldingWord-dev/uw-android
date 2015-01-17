package model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import model.modelClasses.BookModel;
import model.modelClasses.ChapterModel;
import utils.DBUtils;

/**
 * Created by Fechner on 1/13/15.
 */
public class AMDatabaseManager {

    private static final String TAG = "AMDatabaseManager";

    public static boolean updateModel(AMDatabaseModelAbstractObject model, SQLiteDatabase database) {

        Log.i(TAG, "Updating model: " + model.toString());

        ContentValues values = model.getModelAsContentValues();
        int update = database.update(model.getSqlTableName(), values,
                model.getSqlUpdateWhereClause(), model.getSqlUpdateWhereArgs());
        if (update > 0) {
            return true;
        }
        else{
            boolean didAdd = addModel(model, database);
            return didAdd;
        }
    }

    public static boolean addModel(AMDatabaseModelAbstractObject model,  SQLiteDatabase database) {

        ContentValues values = model.getModelAsContentValues();
        long insert = database.insert(model.getSqlTableName(), null, values);
        database.close();
        if (insert > 0) {
            Log.d("DB", "" + true);
            return true;
        }
        Log.d("DB", "" + false);
        return false;
    }

    public static Cursor getCursorForChildren(SQLiteDatabase database, AMDatabaseModelAbstractObject model){

        String query = model.getChildrenQuery();
        String[] args = model.getSqlUpdateWhereArgs();
        Cursor cursor = database.rawQuery(query, args);
        return cursor;
    }

}































