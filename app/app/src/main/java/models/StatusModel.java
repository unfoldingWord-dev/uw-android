package models;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import utils.DBUtils;
import utils.JsonUtils;

/**
 * Created by Fechner on 1/9/15.
 */
public class StatusModel {

    private static final String TAG = "StatusModel";

    public String checkingEntity = "";
    public String checkingLevel = "";
    public String comments = "";
    public String contributors = "";
    public String publishDate = "";
    public String sourceText = "";
    public String sourceTextVersion = "";
    public String version = "";
    public LanguageModel parentLanguage;


    public StatusModel(){
        super();
    }
    public StatusModel(LanguageModel parent) {
        this();
        this.parentLanguage = parent;
    }

    public static StatusModel getStatusModelFromJsonObject(JSONObject jsonObj, LanguageModel language){

        StatusModel model = new StatusModel(language);

        try {
            model.checkingEntity = jsonObj.has(JsonUtils.CHECKING_ENTITY) ? jsonObj.getString(JsonUtils.CHECKING_ENTITY) : "";
            model.checkingLevel = jsonObj.has(JsonUtils.CHECKING_LEVEL) ? jsonObj.getString(JsonUtils.CHECKING_LEVEL) : "";
            model.checkingLevel = jsonObj.has(JsonUtils.CHECKING_LEVEL) ? jsonObj.getString(JsonUtils.CHECKING_LEVEL) : "";
            model.comments = jsonObj.has(JsonUtils.COMMENTS) ? jsonObj.getString(JsonUtils.COMMENTS) : "";
            model.contributors = jsonObj.has(JsonUtils.CONTRIBUTORS) ? jsonObj.getString(JsonUtils.CONTRIBUTORS) : "";
            model.publishDate = jsonObj.has(JsonUtils.PUBLISH_DATE) ? jsonObj.getString(JsonUtils.PUBLISH_DATE) : "";
            model.sourceText = jsonObj.has(JsonUtils.SOURCE_TEXT) ? jsonObj.getString(JsonUtils.SOURCE_TEXT) : "";
            model.sourceTextVersion = jsonObj.has(JsonUtils.SOURCE_TEXT_VERSION) ? jsonObj.getString(JsonUtils.SOURCE_TEXT_VERSION) : "";
            model.version = jsonObj.has(JsonUtils.VERSION) ? jsonObj.getString(JsonUtils.VERSION) : "";
        }
        catch (JSONException e){
            Log.e(TAG, "StatusModel JSON Exception: " + e.toString());
            return null;
        }
        return model;
    }

    public ContentValues getModelAsContentValues() {

        ContentValues values = new ContentValues();

        values.put(DBUtils.COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG, this.checkingEntity);
        values.put(DBUtils.COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG, this.checkingLevel);
        values.put(DBUtils.COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG, this.comments);
        values.put(DBUtils.COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG, this.contributors);
        values.put(DBUtils.COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG, this.publishDate);
        values.put(DBUtils.COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG, this.sourceText);
        values.put(DBUtils.COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG, this.sourceTextVersion);
        values.put(DBUtils.COLUMN_VERSION_TABLE_LANGUAGE_CATALOG, this.version);

        return values;
    }
    
    

    @Override
    public String toString() {
        return "StatusModel{" +
                ", checkingEntity='" + checkingEntity + '\'' +
                ", checkingLevel='" + checkingLevel + '\'' +
                ", comments='" + comments + '\'' +
                ", contributors='" + contributors + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", sourceText='" + sourceText + '\'' +
                ", sourceTextVersion='" + sourceTextVersion + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
