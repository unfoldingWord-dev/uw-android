package model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import model.modelClasses.BookModel;
import model.modelClasses.ChapterModel;
import model.modelClasses.PageModel;
import utils.DBUtils;

/**
 * Created by Fechner on 1/13/15.
 */
public class AMDatabaseManager {

    private static final String TAG = "AMDatabaseManager";

    /**
     *  Updates the passed model into the database, or goes to addModel() creates if it does not exist
     * @param model
     * @param database
     * @return
     */
    public static boolean updateModel(AMDatabaseModelAbstractObject model, SQLiteDatabase database) {

//        Log.i(TAG, "Updating model: " + model.toString());

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

    /**
     * adds the passed model to the database
     * @param model
     * @param database
     * @return
     */
    public static boolean addModel(AMDatabaseModelAbstractObject model,  SQLiteDatabase database) {

        ContentValues values = model.getModelAsContentValues();
        long insert = database.insert(model.getSqlTableName(), null, values);
        database.close();
        if (insert > 0) {
//            Log.d("DB", "" + true);
            return true;
        }
//        Log.d("DB", "" + false);
        return false;
    }

    /**
     * Loads the children models of the passed model.
     * @param database
     * @param model
     * @return
     */
    public static ArrayList<AMDatabaseModelAbstractObject> loadChildrenModelsForModel(SQLiteDatabase database,
                                                                                      AMDatabaseModelAbstractObject model){
        ArrayList<AMDatabaseModelAbstractObject> models = null;

        String query = model.getChildrenQuery();
        String[] args = model.getSqlUpdateWhereArgs();
        Cursor cursor = database.rawQuery(query, args);

        if (cursor != null) {
            models = new ArrayList<AMDatabaseModelAbstractObject>();

            while (cursor.moveToNext()) {
                AMDatabaseModelAbstractObject childModel = model.getChildModelFromCursor(cursor);
                models.add(childModel);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return models;
    }

    /**
     * finds the model in the database, using the passed key as the search param
     * @param database
     * @param model
     * @param key
     * @return
     */
    public static AMDatabaseModelAbstractObject getModelForKey(SQLiteDatabase database, AMDatabaseModelAbstractObject model, String key){

        String query = model.getSelectModelQuery();
        String[] args = new String[]{key};

        Cursor cursor = database.rawQuery(query, args);

        if (cursor != null) {

            while (cursor.moveToNext()) {
                model.initModelFromCursor(cursor);
            }
        }
        else{
            model = null;
        }

        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return model;
    }



}































