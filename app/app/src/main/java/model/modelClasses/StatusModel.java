package model.modelClasses;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import model.modelClasses.mainData.VersionModel;

/**
 * Created by Fechner on 1/9/15.
 */
public class StatusModel {

    static String RESOURCES_COLUMN_STATUS_CHECKING_ENTITY = "_column_table_resources_checking_entity";
    static String RESOURCES_COLUMN_STATUS_CHECKING_LEVEL = "_column_table_resources_checking_level";
    static String RESOURCES_COLUMN_STATUS_COMMENTS = "_column_table_resources_comments";
    static String RESOURCES_COLUMN_STATUS_CONTRIBUTORS = "_column_table_resources_contributors";
    static String RESOURCES_COLUMN_STATUS_PUBLISH_DATE = "_column_table_resources_public_date";
    static String RESOURCES_COLUMN_STATUS_SOURCE_TEXT = "_column_table_resources_source_text";
    static String RESOURCES_COLUMN_STATUS_SOURCE_TEXT_VERSION = "_column_table_resources_source_text_version";
    static String RESOURCES_COLUMN_STATUS_VERSION = "_column_table_resources_status_version";


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
    public VersionModel parentResource;


    public StatusModel(){
        super();
    }
    public StatusModel(VersionModel parent) {
        this();
        this.parentResource = parent;
    }

    public static StatusModel getStatusModelFromJsonObject(JSONObject jsonObj, VersionModel resource){

        StatusModel model = new StatusModel(resource);

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

        values.put(StatusModel.RESOURCES_COLUMN_STATUS_CHECKING_ENTITY, this.checkingEntity);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_CHECKING_LEVEL, this.checkingLevel);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_COMMENTS, this.comments);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_CONTRIBUTORS, this.contributors);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_PUBLISH_DATE, this.publishDate);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_SOURCE_TEXT, this.sourceText);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_SOURCE_TEXT_VERSION, this.sourceTextVersion);
        values.put(StatusModel.RESOURCES_COLUMN_STATUS_VERSION, this.version);

        return values;
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "checkingEntity='" + checkingEntity + '\'' +
                ", checkingLevel='" + checkingLevel + '\'' +
                ", comments='" + comments + '\'' +
                ", contributors='" + contributors + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", sourceText='" + sourceText + '\'' +
                ", sourceTextVersion='" + sourceTextVersion + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getModelAsJson(Context context) {
        String json = "checkingEntity: \"" + checkingEntity + "\",\n" +
                "checkingLevel: \"" + checkingLevel + "\",\n" +
                "comments: \"" + comments + "\",\n" +
                "contributors: \"" + contributors + "\",\n" +
                "publishDate: \"" + publishDate + "\",\n" +
                "sourceText: \"" + sourceText + "\",\n" +
                "sourceTextVersion: \"" + sourceTextVersion + "\",\n" +
                "version: \"" + version + "\"";
        return json;
    }
}
