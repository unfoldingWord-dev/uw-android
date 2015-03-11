package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.ProjectDataSource;

/**
 * Created by Fechner on 1/22/15.
 */
public class ProjectModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface, Comparable<ProjectModel>{

    private static final String TAG = "AMDBeModelAbstractObj";

    private static final String DATE_MODIFIED_JSON_KEY = "date_modified";
    private static final String LANGUAGES_URL_JSON_KEY = "lang_catalog";
    private static final String META_JSON_KEY = "meta";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String SORT_JSON_KEY = "sort";


    public long dateModified;
    public String languageUrl;
    public String meta;
    public int sort;

    private ArrayList<LanguageModel> languages = null;
    public ArrayList<LanguageModel> getChildModels(Context context){

        if(languages == null){
            languages = this.getDataSource(context).getChildModels(this);
        }
        return languages;
    }

    public ProjectModel() {
    }

    public ProjectModel(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public ProjectDataSource getDataSource(Context context) {
        return new ProjectDataSource(context);
    }

    public boolean containsLanguage(String languageName, Context context){

        ArrayList<LanguageModel> childLanguages = this.getChildModels(context);

        for(LanguageModel model : childLanguages){
            if(model.languageName.equalsIgnoreCase(languageName)){
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getAvailableLanguageNames(Context context){

        ArrayList<LanguageModel> childLanguages = this.getChildModels(context);
        ArrayList<String> languages = new ArrayList<String>();

        for(LanguageModel model : childLanguages){
            languages.add(model.languageName);
        }

        return languages;
    }

    public LanguageModel getLanguageModel(Context context, String languageName){

        ArrayList<LanguageModel> childLanguages = this.getChildModels(context);

        for(LanguageModel model : childLanguages){
            if(model.languageName.equalsIgnoreCase(languageName)){
                return model;
            }
        }
        return null;
    }

    public String getLanguageProjectNameForLanguage(String languageName, Context context){

        ArrayList<LanguageModel> childLanguages = this.getChildModels(context);

        for(LanguageModel model : childLanguages){
            if(model.languageName.equalsIgnoreCase(languageName)){
                return model.projectName;
            }
        }
        return null;
    }

    //region DatabaseInterface

    public void initModelFromJsonObject(JSONObject jsonObj){

        try {

            if (jsonObj.has(DATE_MODIFIED_JSON_KEY)) {
                dateModified = getDateFromString(jsonObj.getString(DATE_MODIFIED_JSON_KEY));
            }
            else{
                dateModified = -1;
            }

            slug = jsonObj.has(SLUG_JSON_KEY) ? jsonObj.getString(SLUG_JSON_KEY) : "";
            languageUrl = jsonObj.has(LANGUAGES_URL_JSON_KEY) ? jsonObj.getString(LANGUAGES_URL_JSON_KEY) : "";
            String metaString = jsonObj.has(META_JSON_KEY) ? jsonObj.getString(META_JSON_KEY) : "";
            metaString = metaString.replace("[\"", "");
            metaString = metaString.replace("\"]", "");

            this.meta = (metaString.length() > 2)? metaString : "Bible Stories";
            sort = Integer.parseInt(jsonObj.has(SORT_JSON_KEY) ? jsonObj.getString(SORT_JSON_KEY) : "");
            uid = 0;

        }
        catch (JSONException e){
            Log.e(TAG, "LanguageModel JSON Exception: " + e.toString());
        }
    }

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        return;
    }

    //endregion


    @Override
    public int compareTo(ProjectModel another) {
        return this.sort - another.sort;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "dateModified=" + dateModified +
                ", languageUrl='" + languageUrl + '\'' +
                ", meta='" + meta + '\'' +
                ", slug='" + slug + '\'' +
                ", sort=" + sort +
                ", languages=" + languages +
                "} " + super.toString();
    }

    //region GeneralRowInterface

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getChildIdentifier() {
        return null;
    }


    //endregion
}
