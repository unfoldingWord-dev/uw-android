package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.datasource.VersionDataSource;
import model.database.DBManager;
import model.modelClasses.StatusModel;

/**
 * Created by Fechner on 1/22/15.
 */
public class VersionModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface {

    private static final String TAG = "ResourceModel";

    private static final String DATE_MODIFIED_JSON_KEY = "date_modified";
    private static final String NAME_JSON_KEY = "name";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String SOURCE_URL_JSON_KEY = "source";
    private static final String TERMS_URL_JSON_KEY = "terms";
    private static final String NOTES_URL_JSON_KEY = "notes";
    private static final String USFM_URL_JSON_KEY = "usfm";
    private static final String STATUS_JSON_KEY = "status";

    public String name;
    public long dateModified;
    public String sourceUrl;
    public String termsUrl;
    public String notesUrl;
    public String usfmUrl;

    public StatusModel status;

    private LanguageModel parent;
    public LanguageModel getParent(Context context){

        if(parent == null){
            parent = (LanguageModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }
        return parent;
    }
    public void setParent(LanguageModel parent){
        this.parent = parent;
    }

    private ArrayList<BookModel> books = null;
    public ArrayList<BookModel> getStoriesChildModels(Context context){

        if(books == null){
            books = this.getDataSource(context).getChildModels(this);
        }
        return books;
    }

    private ArrayList<BibleChapterModel> chapters = null;
    public ArrayList<BibleChapterModel> getBibleChildModels(Context context){

        if(chapters == null){
            chapters = new BibleChapterDataSource(context).getChaptersForParentId(Long.toString(this.uid));
        }

        return chapters;
    }

    public VersionModel() {
        super();
        this.status = new StatusModel();
    }

    public VersionModel(JSONObject jsonObject) {
        super(jsonObject);
    }

    public VersionModel(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        super(jsonObject, parent);
    }
    
    @Override
    public VersionDataSource getDataSource(Context context) {
        return new VersionDataSource(context);
    }

    public void initModelFromJsonObject(JSONObject jsonObj){

        try {

            if (jsonObj.has(DATE_MODIFIED_JSON_KEY)) {
                dateModified = getDateFromString(jsonObj.getString(DATE_MODIFIED_JSON_KEY));
            }
            else{
                dateModified = -1;
            }

            slug = jsonObj.has(SLUG_JSON_KEY) ? jsonObj.getString(SLUG_JSON_KEY) : "";
            name = jsonObj.has(NAME_JSON_KEY) ? jsonObj.getString(NAME_JSON_KEY) : "";
            sourceUrl = jsonObj.has(SOURCE_URL_JSON_KEY) ? jsonObj.getString(SOURCE_URL_JSON_KEY) : "";
            termsUrl = jsonObj.has(TERMS_URL_JSON_KEY) ? jsonObj.getString(TERMS_URL_JSON_KEY) : "";
            notesUrl = jsonObj.has(NOTES_URL_JSON_KEY) ? jsonObj.getString(NOTES_URL_JSON_KEY) : "";
            usfmUrl = jsonObj.has(USFM_URL_JSON_KEY) ? jsonObj.getString(USFM_URL_JSON_KEY) : "";

            JSONObject statusJsonObject = jsonObj.getJSONObject(STATUS_JSON_KEY);
            StatusModel statMod = StatusModel.getStatusModelFromJsonObject(statusJsonObject, this);

            status = (statMod != null)? statMod : null;
        }
        catch (JSONException e){
            Log.e(TAG, "LanguageModel JSON Exception: " + e.toString());
        }
    }

    public ArrayList<String> getAvailableLanguages(Context context) {

        ArrayList<String> availLanguages = DBManager.getAvailableLanguages(context);
        ArrayList<String> languages = new ArrayList<String>();

        for(String language : availLanguages){
            if(getParent(context).getParent(context).containsLanguage(language, context) && !languages.contains(language)){
                languages.add(language.toLowerCase());
            }
        }
        return languages;
    }

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        this.initModelFromJsonObject(jsonObject);

        this.parentId = parent.uid;
        this.slug += ((LanguageModel) parent).slug;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }

    @Override
    public String toString() {
        return "VersionModel{" +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", dateModified=" + dateModified +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", termsUrl='" + termsUrl + '\'' +
                ", notesUrl='" + notesUrl + '\'' +
                ", usfmUrl='" + usfmUrl + '\'' +
                ", status=" + status.toString() +
                ", parent=" + parent +
                ", books=" + books +
                "} " + super.toString();
    }
}
