package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.LanguageDataSource;
import model.database.DBManager;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class LanguageModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface{

    private static final String TAG = "LanguageModel";

    private static final String LANGUAGE_JSON_KEY = "language";
    private static final String MODIFIED_DATE_JSON_KEY = "date_modified";
    private static final String READING_DIRECTION_JSON_KEY = "direction";
    private static final String LANGUAGE_NAME_JSON_KEY = "name";
    private static final String SLUG_JSON_KEY = "slug";

    private static final String PROJECT_JSON_KEY = "project";
    private static final String DESCRIPTION_JSON_KEY = "desc";
    private static final String META_JSON_KEY = "meta";
    private static final String PROJECT_NAME_JSON_KEY = "name";
    private static final String SORT_JSON_KEY = "sort";
    private static final String RESOURCE_URL_JSON_KEY = "res_catalog";


    public long dateModified;
    public String readingDirection;
    public String languageName;
    public String resourceUrl;

    public String description;
    public String meta;
    public String projectName;
    public int sortOrder;

    private ProjectModel parent;
    public ProjectModel getParent(Context context){

        if(parent == null){
            parent = (ProjectModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }
        return parent;
    }
    public void setParent(ProjectModel parent){
        this.parent = parent;
    }

    private ArrayList<VersionModel> resources = null;
    public ArrayList<VersionModel> getChildModels(Context context){

        if(resources == null){
            resources = this.getDataSource(context).getChildModels(this);
        }

        return resources;
    }

    public LanguageModel() {
    }

    public LanguageModel(JSONObject jsonObject) {
        super(jsonObject);
    }

    public LanguageModel(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        super(jsonObject, parent);
    }

    @Override
    public LanguageDataSource getDataSource(Context context) {
        return new LanguageDataSource(context);
    }

    public void initModelFromJsonObject(JSONObject jsonObj){

        try {
            resourceUrl = jsonObj.has(RESOURCE_URL_JSON_KEY) ? jsonObj.getString(RESOURCE_URL_JSON_KEY) : "";

            JSONObject languageObject = jsonObj.getJSONObject(LANGUAGE_JSON_KEY);

            slug = languageObject.has(SLUG_JSON_KEY) ? languageObject.getString(SLUG_JSON_KEY) : "";
            languageName = languageObject.has(LANGUAGE_NAME_JSON_KEY) ? languageObject.getString(LANGUAGE_NAME_JSON_KEY) : "";
            readingDirection = languageObject.has(READING_DIRECTION_JSON_KEY) ? languageObject.getString(READING_DIRECTION_JSON_KEY) : "";

            if (languageObject.has(MODIFIED_DATE_JSON_KEY)) {
                dateModified = getDateFromString(languageObject.getString(MODIFIED_DATE_JSON_KEY));
            }
            else{
                dateModified = -1;
            }

            JSONObject projectObject = jsonObj.getJSONObject(PROJECT_JSON_KEY);

            projectName = projectObject.has(PROJECT_NAME_JSON_KEY) ? projectObject.getString(PROJECT_NAME_JSON_KEY) : "";
            description = projectObject.has(DESCRIPTION_JSON_KEY) ? projectObject.getString(DESCRIPTION_JSON_KEY) : "";
            meta = projectObject.has(META_JSON_KEY) ? projectObject.getString(META_JSON_KEY) : "";
            sortOrder = projectObject.has(SORT_JSON_KEY) ? Integer.parseInt(projectObject.getString(SORT_JSON_KEY)) : 0;
        }
        catch (JSONException e){
            Log.e(TAG, "LanguageModel JSON Exception: " + e.toString());
        }
    }

    public ArrayList<String> getAvailableLanguages(Context context) {

        ArrayList<String> availLanguages = DBManager.getAvailableLanguages(context);
        ArrayList<String> languages = new ArrayList<String>();

        for(String language : availLanguages){
            if(getParent(context).containsLanguage(language, context) && !languages.contains(language)){
                languages.add(language.toLowerCase());
            }
        }
        return languages;
    }

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        this.initModelFromJsonObject(jsonObject);

        this.parentId = parent.uid;
        this.slug += ((ProjectModel) parent).slug;
    }

    @Override
    public String getTitle() {
        return this.projectName;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }

    @Override
    public String toString() {
        return "LanguageModel{" +
                "dateModified=" + dateModified +
                ", readingDirection='" + readingDirection + '\'' +
                ", languageName='" + languageName + '\'' +
                ", slug='" + slug + '\'' +
                ", resourceUrl='" + resourceUrl + '\'' +
                ", description='" + description + '\'' +
                ", meta='" + meta + '\'' +
                ", projectName='" + projectName + '\'' +
                ", sortOrder=" + sortOrder +
                ", parent=" + parent +
                ", resources=" + resources +
                "} " + super.toString();
    }
}
