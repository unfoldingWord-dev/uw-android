package model.modelClasses;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import utils.DBUtils;

/**
 * Created by Fechner on 1/9/15.
 */
public class StatusModel {

    private static final String TAG = "StatusModel";

    private static final String CHECKING_ENTITY = "checking_entity";
    private static final String CHECKING_LEVEL = "checking_level";
    private static final String COMMENTS = "comments";
    private static final String CONTRIBUTORS = "contributors";
    private static final String PUBLISH_DATE = "publish_date";
    private static final String SOURCE_TEXT = "source_text";
    private static final String SOURCE_TEXT_VERSION = "source_text_version";
    private static final String VERSION = "version";


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
            model.checkingEntity = jsonObj.has(CHECKING_ENTITY) ? jsonObj.getString(CHECKING_ENTITY) : "";
            model.checkingLevel = jsonObj.has(CHECKING_LEVEL) ? jsonObj.getString(CHECKING_LEVEL) : "";
            model.checkingLevel = jsonObj.has(CHECKING_LEVEL) ? jsonObj.getString(CHECKING_LEVEL) : "";
            model.comments = jsonObj.has(COMMENTS) ? jsonObj.getString(COMMENTS) : "";
            model.contributors = jsonObj.has(CONTRIBUTORS) ? jsonObj.getString(CONTRIBUTORS) : "";
            model.publishDate = jsonObj.has(PUBLISH_DATE) ? jsonObj.getString(PUBLISH_DATE) : "";
            model.sourceText = jsonObj.has(SOURCE_TEXT) ? jsonObj.getString(SOURCE_TEXT) : "";
            model.sourceTextVersion = jsonObj.has(SOURCE_TEXT_VERSION) ? jsonObj.getString(SOURCE_TEXT_VERSION) : "";
            model.version = jsonObj.has(VERSION) ? jsonObj.getString(VERSION) : "";
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
