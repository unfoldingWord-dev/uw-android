package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import model.db.DBManager;
import model.modelClasses.mainData.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 2/23/15.
 */
public abstract class AMDatabaseDataSourceAbstract {

    private static final String TAG = "AMDBDataSrcAbstract";
    protected Context context;


    public AMDatabaseDataSourceAbstract(Context context){
        this.context = context;
    }

    private SQLiteDatabase getDatabase() {

        SQLiteDatabase database = DBManager.getInstance(this.context).getReadableDatabase();
        return database;
    }

    abstract public String getTableCreationString();
    abstract public AMDatabaseModelAbstractObject getModel(String uid);

    abstract protected String getTableName();
    abstract protected String getUIDColumnName();
    abstract protected String getParentIdColumnName();
    abstract protected String getSlugColumnName();

    abstract protected AMDatabaseDataSourceAbstract getChildDataSource();
    abstract protected AMDatabaseDataSourceAbstract getParentDataSource();

    abstract protected ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model);
    abstract protected AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor);
    abstract public AMDatabaseModelAbstractObject getModelForSlug(String slug);


    public boolean saveModel(AMDatabaseModelAbstractObject model) {

        createOrUpdateDatabaseModel(model);
        return false;
    }

    public void deleteModel(AMDatabaseModelAbstractObject model){

        ArrayList<AMDatabaseModelAbstractObject> children = this.loadChildrenModelsFromDatabase(model);

        for(AMDatabaseModelAbstractObject childModel : children){
            childModel.getDataSource(this.context).deleteModel(childModel);
        }

        SQLiteDatabase database = getDatabase();

        database.execSQL(this.getDeleteQuery(Long.toString(model.uid)));
        database.close();
    }

    public AMDatabaseModelAbstractObject getModelFromDatabaseForSlug(String slug){

        ArrayList<AMDatabaseModelAbstractObject> models =  this.getModelFromDatabase(this.getSlugColumnName(), slug);

        if(models.size() != 1){
            return null;
        }
        else{
            return models.get(0);
        }
    }

    protected ArrayList<AMDatabaseModelAbstractObject> getModelFromDatabase(String desiredColumn, String key){

        SQLiteDatabase database = getDatabase();

        ArrayList<AMDatabaseModelAbstractObject> models = new ArrayList<AMDatabaseModelAbstractObject>();
        Cursor cursor = database.rawQuery(this.getQueryForColumn(desiredColumn), new String[]{key});
        if (cursor != null) {
            int i = 0;
            while (cursor.moveToNext()) {
                models.add(this.getObjectFromCursor(cursor));
                i++;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return models;
    }

    public ArrayList<AMDatabaseModelAbstractObject> getAllModels(){

        try {
            SQLiteDatabase database = getDatabase();

            ArrayList<AMDatabaseModelAbstractObject> models = new ArrayList<AMDatabaseModelAbstractObject>();
            Cursor cursor = database.rawQuery(this.getAllQuery(), null);
            if (cursor != null) {
                int i = 0;
                while (cursor.moveToNext()) {
                    models.add(this.getObjectFromCursor(cursor));
                    i++;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            database.close();
            return models;
        }
        catch (SQLiteException e){
            e.printStackTrace();
            return null;
        }
    }

    protected ArrayList<String> getUniqueValuesForColumn(String column){

        try {
            ArrayList<String> values = new ArrayList<String>();
            SQLiteDatabase database = getDatabase();
            Cursor cursor = database.query(true, this.getTableName(), new String[]{column}, null, null, null, null, null, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {
                    String value = cursor.getString(cursor.getColumnIndex(column));
                    values.add(value);
                }
                cursor.close();
            }

            database.close();
            return values;
        }
        catch (SQLiteException e){
            e.printStackTrace();
            return null;
        }
    }

    public AMDatabaseModelAbstractObject getModelForKey(String key){

        SQLiteDatabase database = getDatabase();
        AMDatabaseModelAbstractObject model = null;

        Cursor cursor = database.rawQuery(this.getSelectQuery(), new String[]{key});

        if (cursor != null) {

            while (cursor.moveToNext()) {
                model = getObjectFromCursor(cursor);
            }
            cursor.close();
        }
        else{
            model = null;
        }

        database.close();

        return model;
    }

    public boolean createOrUpdateDatabaseModel(AMDatabaseModelAbstractObject model){

//        Log.i(TAG, "Updating model: " + model.toString());
        SQLiteDatabase database = getDatabase();

        ContentValues values = this.getModelAsContentValues(model);
        int update = database.update(this.getTableName(), values,
                this.getCreateWhereClause(), new String[]{Long.toString(model.uid)});
        if (update > 0) {
            return true;
        }
        else{
            boolean didAdd = addModelToDatabase(database, values);
            return didAdd;
        }
    }

    protected boolean addModelToDatabase(SQLiteDatabase database, ContentValues values) {

        long insert = database.insert(this.getTableName(), null, values);
        database.close();
        if (insert > 0) {
//            Log.d("DB", "add: " + true);
            return true;
        }
//        Log.d("DB", "add: " + false);
        return false;
    }

    protected ArrayList<AMDatabaseModelAbstractObject> loadChildrenModelsFromDatabase(AMDatabaseModelAbstractObject model){

        SQLiteDatabase database = getDatabase();

        ArrayList<AMDatabaseModelAbstractObject> models = new ArrayList<AMDatabaseModelAbstractObject>();
        AMDatabaseDataSourceAbstract childDataSource = this.getChildDataSource();

        Cursor cursor = database.rawQuery(getChildrenQuery(childDataSource),new String[]{Long.toString(model.uid)});

        if (cursor != null) {

            int i = 0;
            while (cursor.moveToNext()) {
                AMDatabaseModelAbstractObject childModel = childDataSource.getObjectFromCursor(cursor);
                models.add(childModel);
                i++;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return models;
    }

    public AMDatabaseModelAbstractObject loadParentModelFromDatabase(AMDatabaseModelAbstractObject model){

        AMDatabaseDataSourceAbstract parentDataSource = this.getParentDataSource();
        AMDatabaseModelAbstractObject parentModel = parentDataSource.getModelForKey(Long.toString(model.parentId));
        return parentModel;
    }

    private String getCreateWhereClause(){
        return this.getUIDColumnName() + "=?";
    }

    private String getSelectQuery(){
        String query = "SELECT * FROM " + this.getTableName() + " WHERE " + this.getCreateWhereClause();
        return query;
    }

    private String getChildrenQuery(AMDatabaseDataSourceAbstract childDataSource){
        String query = "SELECT * FROM " +
                childDataSource.getTableName() + " WHERE " +
                childDataSource.getParentIdColumnName() + "=?";
        return query;
    }

    private String getAllQuery(){
        String query = "SELECT * FROM " + this.getTableName();
        return query;
    }

    private String getQueryForColumn(String column){
        String query = "SELECT * FROM " +
                this.getTableName() + " WHERE " +
                column + "=?";
        return query;
    }

    private String getDeleteQuery(String id){

        String query = "DELETE FROM " + this.getTableName() + " WHERE " + this.getUIDColumnName() + " = " + id;
        return query;
    }







}
