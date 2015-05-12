package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.LanguageDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class LanguageModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface{


    private class LanguageJsonModel{

        long mod;
        String slug;
        String lc;
    }

    private static final String TAG = "LanguageModel";

    public long dateModified;
    public String languageAbbreviation;

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

    private ArrayList<VersionModel> versions = null;
    public ArrayList<VersionModel> getChildModels(Context context){

        if(versions == null){
            versions = this.getDataSource(context).getChildModels(this);
        }

        return versions;
    }

    public LanguageModel() {
    }

    public LanguageModel(JSONObject jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public LanguageModel(JSONObject jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }

    @Override
    public LanguageDataSource getDataSource(Context context) {
        return new LanguageDataSource(context);
    }

    @Override
    public void initModelFromJson(JSONObject json, boolean sideLoaded){

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
            return;
        }

        try{
            dateModified = json.getLong("mod");
            languageAbbreviation = json.getString("lc");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        LanguageJsonModel model = new Gson().fromJson(json.toString(), LanguageJsonModel.class);
//
//        dateModified = model.mod;
//        languageAbbreviation = model.lc;
//        this.slug = model.slug;
        uid = -1;
    }

    @Override
    public void initModelFromJson(JSONObject json, long parentId, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
        }
        else {
            this.initModelFromJson(json, sideLoaded);
            this.parentId = parentId;
            this.slug += this.languageAbbreviation + parentId;
        }
    }

    @Override
    public String getTitle() {
        return this.languageAbbreviation;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }

    @Override
    public String toString() {
        return "LanguageModel{" +
                "dateModified=" + dateModified +
                ", languageAbbreviation='" + languageAbbreviation + '\'' +
                ", parent=" + parent +
                "} " + super.toString();
    }

    protected class LanguageSideLoadedModel{

        long date_modified;
        String slug;
        String lang_abbrev;
        VersionModel.VersionSideLoadedModel[] versions;

        private LanguageSideLoadedModel(LanguageModel model, Context context) {

            this.date_modified = model.dateModified;
            this.lang_abbrev = model.languageAbbreviation;
            this.slug = model.slug;

            ArrayList<VersionModel> bookModels = model.getChildModels(context);
            this.versions = new VersionModel.VersionSideLoadedModel[bookModels.size()];

            for(int i = 0; i < bookModels.size(); i++){
                this.versions[i] = bookModels.get(i).getAsSideLoadedModel(context);
            }
        }
    }

    protected LanguageSideLoadedModel getAsSideLoadedModel(Context context){

        return new LanguageSideLoadedModel(this, context);
    }

    public void initModelFromSideLoadedJson(JSONObject json){

        LanguageSideLoadedModel model = new Gson().fromJson(json.toString(), LanguageSideLoadedModel.class);

        dateModified = model.date_modified;
        languageAbbreviation = model.lang_abbrev;
        slug = model.slug;
        uid = -1;
    }
}
