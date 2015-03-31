package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.ProjectDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/22/15.
 */
public class ProjectModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface, Comparable<ProjectModel>{

    private static final String TAG = "AMDBModelAbstractObj";

    private class ProjectJsonModel{
        String title;
        String slug;
    }

    public String title;

    private ArrayList<LanguageModel> languages = null;
    public ArrayList<LanguageModel> getChildModels(Context context){

        if(languages == null){
            languages = this.getDataSource(context).getChildModels(this);
        }
        return languages;
    }

    public ProjectModel() {
    }

    public ProjectModel(String jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    @Override
    public ProjectDataSource getDataSource(Context context) {
        return new ProjectDataSource(context);
    }

    public boolean containsLanguage(String languageName, Context context){

        ArrayList<LanguageModel> childLanguages = this.getChildModels(context);

        for(LanguageModel model : childLanguages){
            if(model.languageAbbreviation.equalsIgnoreCase(languageName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void initModelFromJson(String json, boolean sideLoaded){
        if(sideLoaded){
            initModelFromSideLoadedJson(json);
            return;
        }

        ProjectJsonModel model = new Gson().fromJson(json, ProjectJsonModel.class);

        title = model.title;
        slug = model.slug;
        uid = -1;
    }

    @Override
    public void initModelFromJson(String json, long parentId, boolean sideLoaded) {
        return;
    }

    @Override
    public int compareTo(ProjectModel another) {
        return this.title.substring(0, 1).compareTo(another.title.substring(0, 1));
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }


    @Override
    public String toString() {
        return "ProjectModel{" +
                "title='" + title + '\'' +
                "} " + super.toString();
    }

    protected class ProjectSideLoadedModel {

        String title;
        String slug;
        long date_modified;

        LanguageModel.LanguageSideLoadedModel[] languages;

        private ProjectSideLoadedModel(ProjectModel model, Context context) {

            this.title = model.title;
            this.slug = model.slug;

            ArrayList<LanguageModel> bookModels = model.getChildModels(context);
            this.languages = new LanguageModel.LanguageSideLoadedModel[bookModels.size()];

            for(int i = 0; i < bookModels.size(); i++){
                this.languages[i] = bookModels.get(i).getAsSideLoadedModel(context);
            }
        }
    }

    public ProjectSideLoadedModel getAsSideLoadedModel(Context context){

        return new ProjectSideLoadedModel(this, context);
    }

    public void initModelFromSideLoadedJson(String json){

        ProjectSideLoadedModel model = new Gson().fromJson(json, ProjectSideLoadedModel.class);

        title = model.title;
        slug = model.slug;
        uid = -1;
    }


}
