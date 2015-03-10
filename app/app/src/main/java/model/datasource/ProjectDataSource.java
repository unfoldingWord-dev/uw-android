package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import model.modelClasses.mainData.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;

/**
 * Created by Fechner on 2/23/15.
 */
public class ProjectDataSource extends AMDatabaseDataSourceAbstract {

    static String TABLE_PROJECT = "_table_project";

    //Table columns of TABLE_PROJECT
    static String TABLE_PROJECT_COLUMN_UID = "_column_project_uid";
    static String TABLE_PROJECT_COLUMN_DATE_MODIFIED_ = "_column_date_modified";
    static String TABLE_PROJECT_COLUMN_LANGUAGE_URL = "_column_language_url";
    static String TABLE_PROJECT_COLUMN_META = "_column_meta";
    static String TABLE_PROJECT_COLUMN_SLUG = "_column_slug";
    static String TABLE_PROJECT_COLUMN_SORT = "_column_sort";

    public ProjectDataSource(Context context) {
        super(context);
    }

    public ArrayList<LanguageModel> getChildModels(ProjectModel model) {

        ArrayList<LanguageModel> modelList = new ArrayList<LanguageModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = this.loadChildrenModelsFromDatabase(model);

        for(AMDatabaseModelAbstractObject mod : models){
            modelList.add((LanguageModel) mod);
        }
        return modelList;
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
        model.dateModified = cursor.getLong(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_DATE_MODIFIED_));
        model.languageUrl = cursor.getString(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_LANGUAGE_URL));
        model.meta = cursor.getString(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_META));
        model.slug =  cursor.getString(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_SLUG));
        model.sort = cursor.getInt(cursor.getColumnIndex(TABLE_PROJECT_COLUMN_SORT));

        return model;
    }

    @Override
    protected ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model){

        ProjectModel projectModel = (ProjectModel) model;
        ContentValues values = new ContentValues();

        if(projectModel.uid > 0) {
            values.put(TABLE_PROJECT_COLUMN_UID, projectModel.uid);
        }
        values.put(TABLE_PROJECT_COLUMN_DATE_MODIFIED_, projectModel.dateModified);
        values.put(TABLE_PROJECT_COLUMN_LANGUAGE_URL, projectModel.languageUrl);
        values.put(TABLE_PROJECT_COLUMN_META, projectModel.meta);
        values.put(TABLE_PROJECT_COLUMN_SLUG, projectModel.slug);
        values.put(TABLE_PROJECT_COLUMN_SORT, projectModel.sort);

        return values;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                ProjectDataSource.TABLE_PROJECT_COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_DATE_MODIFIED_ + " INTEGER," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_LANGUAGE_URL + " VARCHAR," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_META + " VARCHAR," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_SLUG + " VARCHAR," +
                ProjectDataSource.TABLE_PROJECT_COLUMN_SORT + " INTEGER)";
        return creationString;
    }
}
