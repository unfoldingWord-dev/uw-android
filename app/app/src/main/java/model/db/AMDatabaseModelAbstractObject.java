package model.db;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

/**
 * Created by Fechner on 1/12/15.
 */
public abstract class AMDatabaseModelAbstractObject {

    abstract public ContentValues getModelAsContentValues();
    abstract public String getSqlUpdateWhereClause();
    abstract public String[] getSqlUpdateWhereArgs();
    abstract public String getSqlTableName();
    abstract public String getChildrenQuery();

    abstract public void initModelFromJsonObject(JSONObject jsonObject);

    abstract public void initModelFromCursor(Cursor cursor);


}
