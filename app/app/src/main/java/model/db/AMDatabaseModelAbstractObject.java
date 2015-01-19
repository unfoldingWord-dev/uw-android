package model.db;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

/**
 * Created by Fechner on 1/12/15.
 */
public abstract class AMDatabaseModelAbstractObject {

    /**
     *  Should return the model's database values
     * @return ContentValues
     */
    abstract public ContentValues getModelAsContentValues();

    /**
     * Should return the SQLite Update Query
     * @return String
     */
    abstract public String getSqlUpdateWhereClause();

    /**
     * Should return the model's unique attribute for getSqlUpdateWhereClause()
     * @return
     */
    abstract public String[] getSqlUpdateWhereArgs();

    /**
     * Should return the TableName used for the DB model of this Class
     * @return
     */
    abstract public String getSqlTableName();

    /**
     * Should return an SQLite query to find it's children objects, if it has children.
     * @return
     */
    abstract public String getChildrenQuery();

    /**
     *  Should return a query to find an individual member of this class from the DB.
     * @return
     */
    abstract public String getSelectModelQuery();


    /**
     * Should set all the Object's attributes that are necessary to load from JSON
     * @return
     */
    abstract public void initModelFromJsonObject(JSONObject jsonObject);

    /**
     * Should set all the Object's attributes that originate the DB model for this class
     * @return
     */
    abstract public void initModelFromCursor(Cursor cursor);

    /**
     * Should create a child of this Object from the passed Cursor, if it has children.
     * @return
     */
    abstract public AMDatabaseModelAbstractObject getChildModelFromCursor(Cursor cursor);


}
