package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;

/**
 * Created by Fechner on 2/23/15.
 */
public class ProjectDataSource extends AMDatabaseDataSourceAbstract {

    static String TABLE_PROJECT = "_table_project";

    //Table columns of TABLE_PROJECT
    static final String TABLE_PROJECT_COLUMN_UID = "_column_project_uid";
    static final String TABLE_PROJECT_COLUMN_TITLE = "_column_title";
    static final String TABLE_PROJECT_COLUMN_SLUG = "_column_slug";

    public ProjectDataSource(Context context) {
        super(context);
    }

    public ArrayList<LanguageModel> getChildModels(ProjectModel parentModel) {

        ArrayList<LanguageModel> modelList = new ArrayList<LanguageModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = this.loadChildrenModelsFromDatabase(parentModel);

        for(AMDatabaseModelAbstractObject mod : models){
            LanguageModel model = (LanguageModel) mod;
            model.setParent(parentModel);
            modelList.add( model);
        }
        return modelList;
    }

    @Override
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentId, boolean sideLoaded)  {
        ProjectModel newModel = new ProjectModel(json, sideLoaded);
        ProjectModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
        }

        if (currentModel == null) {
            saveModel(newModel);
            return getModelForSlug(newModel.slug);
        }
        else{
            return null;
        }
    }

    @Override
    protected String getParentIdColumnName() {
        return null;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return new LanguageDataSource(this.context);
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_PROJECT_COLUMN_SLUG;
    }

    @Override
    protected AMDatabaseDataSourceAbstract getParentDataSource() {
        return null;
    }

    @Override
    protected String getTableName() {
        return TABLE_PROJECT;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_PROJECT_COLUMN_UID;
    }

    @Override
    public ProjectModel getModel(String uid) {
        return (ProjectModel) this.getModelForKey(uid);
    }

    public ArrayList<ProjectModel> getAllProjects(){
        ArrayList<AMDatabaseModelAbstractObject> models = this.getAllModels();

        if(models != null) {
            ArrayList<ProjectModel> projects = new ArrayList<ProjectModel>();

            for (AMDatabaseModelAbstractObject model : models) {
                projects.add((ProjectModel) model);
            }

            return projects;
        }
        return null;
    }

    @Override
    public ProjectModel getModelForSlug(String slug){

        return (ProjectModel) this.getModelFromDatabaseForSlug(slug);
    }

    @Override
    protected AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {
        ProjectModel model = new ProjectModel();

        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_UID));
        model.title = cursor.getString(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_TITLE));
        model.slug =  cursor.getString(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_SLUG));

        return model;
    }

    @Override
    protected ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model){

        ProjectModel projectModel = (ProjectModel) model;
        ContentValues values = new ContentValues();

        if(projectModel.uid > 0) {
            values.put(TABLE_PROJECT_COLUMN_UID, projectModel.uid);
        }
        values.put(TABLE_PROJECT_COLUMN_TITLE, projectModel.title);
        values.put(TABLE_PROJECT_COLUMN_SLUG, projectModel.slug);

        return values;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                ProjectDataSource.TABLE_PROJECT_COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_TITLE + " VARCHAR," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_SLUG + " VARCHAR)";
        return creationString;
    }
}
