package model.datasource.AMDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import model.database.DBManager;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 2/23/15.
 */
public abstract class AMDatabaseDataSourceAbstract {

    private static final String TAG = "AMDBDataSrcAbstract";
    protected Context context;


    public AMDatabaseDataSourceAbstract(Context context){
        this.context = context;
    }

    private static synchronized DBManager getDBManager(Context context) {

        DBManager manager = DBManager.getInstance(context);
        return manager;
    }

    abstract public AMDatabaseModelAbstractObject saveOrUpdateModel(String json, long parentId, boolean sideLoaded);

    /**
     * should return a sql table creation string
     * @return
     */
    abstract public String getTableCreationString();

    /**
     * should get the corresponding model from the passed uid
     * @param uid
     * @return
     */
    abstract public AMDatabaseModelAbstractObject getModel(String uid);

    /**
     * should get the corresponding model from the passed slug
     * @param slug
     * @return
     */
    abstract public AMDatabaseModelAbstractObject getModelForSlug(String slug);


    /**
     * should return the table name
     * @return
     */
    abstract protected String getTableName();
    /**
     * should return the column name for the entity's uid.
     * @return
     */
    abstract protected String getUIDColumnName();

    /**
     * should return the column name for the parent entity's column name, or null.
     * @return
     */
    abstract protected String getParentIdColumnName();

    /**
     * should return the column name for the entity's slug.
     * @return
     */
    abstract protected String getSlugColumnName();

    /**
     * Should return the DataSource of the child entity
     * @return
     */
    abstract protected AMDatabaseDataSourceAbstract getChildDataSource();

    /**
     * should return the DataSource of the parent entity
     * @return
     */
    abstract protected AMDatabaseDataSourceAbstract getParentDataSource();

    /**
     * should return the passed model as content values
     * @param model
     * @return
     */
    abstract protected ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model);

    /**
     * should return a new model instance from the passed cursor
     * @param cursor
     * @return
     */
    abstract protected AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor);

    public boolean saveModel(AMDatabaseModelAbstractObject model) {

        createOrUpdateDatabaseModel(model);
        return false;
    }

    public void deleteModel(AMDatabaseModelAbstractObject model){

        ArrayList<AMDatabaseModelAbstractObject> children = this.loadChildrenModelsFromDatabase(model);

        if(children != null) {
            for (AMDatabaseModelAbstractObject childModel : children) {
                childModel.getDataSource(this.context).deleteModel(childModel);
            }
        }

        SQLiteDatabase database = getDBManager(context).getDatabase();

        database.execSQL(this.getDeleteQuery(Long.toString(model.uid)));
        getDBManager(context).closeDatabase();
    }

    public void deleteChildrenOfParent(AMDatabaseModelAbstractObject parent){

        ArrayList<AMDatabaseModelAbstractObject> children = this.loadChildrenModelsFromDatabase(parent);

        if(children != null) {
            for (AMDatabaseModelAbstractObject childModel : children) {
                childModel.getDataSource(this.context).deleteChildrenOfParent(childModel);
            }
        }

        SQLiteDatabase database = getDBManager(context).getDatabase();

        database.execSQL(this.getDeleteChildrenQuery(Long.toString(parent.uid)));
        getDBManager(context).closeDatabase();
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

        SQLiteDatabase database = getDBManager(context).getDatabase();

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
        getDBManager(context).closeDatabase();

        return models;
    }

    public ArrayList<AMDatabaseModelAbstractObject> getAllModels(){

        ArrayList<AMDatabaseModelAbstractObject> models = new ArrayList<AMDatabaseModelAbstractObject>();
        SQLiteDatabase database = getDBManager(context).getDatabase();
        try {

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
        }
        catch (SQLiteException e){
            models = null;
            e.printStackTrace();
        }

        getDBManager(context).closeDatabase();

        return models;
    }

    protected ArrayList<String> getUniqueValuesForColumn(String column){

        ArrayList<String> values = new ArrayList<String>();
        SQLiteDatabase database = getDBManager(context).getDatabase();
        try {

            Cursor cursor = database.query(true, this.getTableName(), new String[]{column}, null, null, null, null, null, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {
                    String value = cursor.getString(cursor.getColumnIndex(column));
                    values.add(value);
                }
            }
            if (cursor != null) {
                cursor.close();
            }


        }
        catch (SQLiteException e){
            e.printStackTrace();
            values = null;
        }

        getDBManager(context).closeDatabase();
        return values;
    }

    public synchronized AMDatabaseModelAbstractObject getModelForKey(String key){

        AMDatabaseModelAbstractObject model = null;
        SQLiteDatabase database = getDBManager(context).getDatabase();
        Cursor cursor = database.rawQuery(this.getSelectQuery(), new String[]{key});

        if (cursor != null) {

            while (cursor.moveToNext()) {
                model = getObjectFromCursor(cursor);
            }
            if (cursor != null) {
                cursor.close();
            }
        } else {
            model = null;
        }
        getDBManager(context).closeDatabase();
        return model;
    }

    public boolean createOrUpdateDatabaseModel(AMDatabaseModelAbstractObject model){

//        Log.i(TAG, "Updating model: " + model.toString());
        SQLiteDatabase database = getDBManager(context).getDatabase();
        synchronized(database) {
            ContentValues values = this.getModelAsContentValues(model);
            int update = database.update(this.getTableName(), values,
                    this.getCreateWhereClause(), new String[]{Long.toString(model.uid)});
            if (update > 0) {
                getDBManager(context).closeDatabase();
                return true;
            } else {
                boolean didAdd = addModelToDatabase(database, values);
                getDBManager(context).closeDatabase();
                return didAdd;
            }
        }


    }

    protected boolean addModelToDatabase(SQLiteDatabase database, ContentValues values) {

        long insert = database.insert(this.getTableName(), null, values);
        if (insert > 0) {
//            Log.d("DB", "add: " + true);
            return true;
        }
//        Log.d("DB", "add: " + false);
        return false;
    }

    protected ArrayList<AMDatabaseModelAbstractObject> loadChildrenModelsFromDatabase(AMDatabaseModelAbstractObject model){

        ArrayList<AMDatabaseModelAbstractObject> models = new ArrayList<AMDatabaseModelAbstractObject>();
        AMDatabaseDataSourceAbstract childDataSource = this.getChildDataSource();

        if(childDataSource == null){
            return null;
        }

        SQLiteDatabase database = getDBManager(context).getDatabase();
        Cursor cursor = database.rawQuery(getChildrenQuery(childDataSource), new String[]{Long.toString(model.uid)});

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
        getDBManager(context).closeDatabase();

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

    private String getDeleteChildrenQuery(String parentId){

        String query = "DELETE FROM " + this.getTableName() + " WHERE " + this.getParentIdColumnName() + " = " + parentId;
        return query;
    }







}
