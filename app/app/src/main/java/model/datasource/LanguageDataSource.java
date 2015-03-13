package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class LanguageDataSource extends AMDatabaseDataSourceAbstract {


    static String TABLE_LANGUAGE = "_table_language";

    //Table columns of TABLE_PROJECT
    static String TABLE_LANGUAGE_UID = "_column_language_uid";
    static String TABLE_LANGUAGE_PARENT_ID = "_column_parent_id";
    static String TABLE_LANGUAGE_DATE_MODIFIED = "_column_date_modified";
    static String TABLE_LANGUAGE_DIRECTION = "_column_direction";
    static String TABLE_LANGUAGE_NAME = "_column_name";
    static String TABLE_LANGUAGE_SLUG = "_column_slug";
    static String TABLE_LANGUAGE_RESOURCES_URL = "_column_resources_url";
    static String TABLE_LANGUAGE_PROJECT_DESCRIPTION = "_column_project_description";
    static String TABLE_LANGUAGE_PROJECT_META = "_column_project_meta";
    static String TABLE_LANGUAGE_PROJECT_NAME = "_column_project_name";
    static String TABLE_LANGUAGE_PROJECT_SORT = "_column_project_sort_order";

    public ArrayList<String> getAvailableLanguages(){

        return this.getUniqueValuesForColumn(TABLE_LANGUAGE_NAME);
    }

    public LanguageDataSource(Context context) {
        super(context);
    }

    public ArrayList<VersionModel> getChildModels(LanguageModel parentModel) {

        ArrayList<VersionModel> modelList = new ArrayList<VersionModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = this.loadChildrenModelsFromDatabase(parentModel);

        for(AMDatabaseModelAbstractObject mod : models){
            VersionModel model = (VersionModel) mod;
            model.setParent(parentModel);
            modelList.add( model);
        }
        return modelList;
    }


    @Override
    protected String getParentIdColumnName() {
        return TABLE_LANGUAGE_PARENT_ID;
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_LANGUAGE_SLUG;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return new VersionDataSource(this.context);
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new ProjectDataSource(this.context);
    }

    @Override
    public String getTableName() {
        return TABLE_LANGUAGE;
    }

    @Override
    public LanguageModel getModel(String uid) {
        return (LanguageModel) this.getModelForKey(uid);
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_LANGUAGE_UID;
    }

    @Override
    public LanguageModel getModelForSlug(String slug){

        return (LanguageModel) this.getModelFromDatabaseForSlug(slug);
    }


    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        LanguageModel model = new LanguageModel();
        model.uid =  cursor.getLong(cursor.getColumnIndex(TABLE_LANGUAGE_UID));
        model.parentId =  cursor.getLong(cursor.getColumnIndex(TABLE_LANGUAGE_PARENT_ID));
        model.dateModified = cursor.getLong(cursor.getColumnIndex(TABLE_LANGUAGE_DATE_MODIFIED));
        model.readingDirection = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_DIRECTION));
        model.languageName = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_NAME));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_SLUG));

        model.resourceUrl = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_RESOURCES_URL));
        model.description = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_PROJECT_DESCRIPTION));
        model.meta = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_PROJECT_META));
        model.projectName = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_PROJECT_NAME));
        model.sortOrder = cursor.getInt(cursor.getColumnIndex(TABLE_LANGUAGE_PROJECT_SORT));


        return model;
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model){

        LanguageModel languageModel = (LanguageModel) model;
        ContentValues values = new ContentValues();

        if(languageModel.uid > 0) {
            values.put(TABLE_LANGUAGE_UID, languageModel.uid);
        }
        values.put(TABLE_LANGUAGE_PARENT_ID, languageModel.parentId);
        values.put(TABLE_LANGUAGE_DATE_MODIFIED, languageModel.dateModified);
        values.put(TABLE_LANGUAGE_DIRECTION, languageModel.readingDirection);
        values.put(TABLE_LANGUAGE_NAME, languageModel.languageName);
        values.put(TABLE_LANGUAGE_SLUG, languageModel.slug);

        values.put(TABLE_LANGUAGE_RESOURCES_URL, languageModel.resourceUrl);
        values.put(TABLE_LANGUAGE_PROJECT_DESCRIPTION, languageModel.description);
        values.put(TABLE_LANGUAGE_PROJECT_META, languageModel.meta);
        values.put(TABLE_LANGUAGE_PROJECT_NAME, languageModel.projectName);
        values.put(TABLE_LANGUAGE_PROJECT_SORT, languageModel.sortOrder);

        return values;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                LanguageDataSource.TABLE_LANGUAGE_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LanguageDataSource.TABLE_LANGUAGE_PARENT_ID + " INTEGER," +
                LanguageDataSource.TABLE_LANGUAGE_DATE_MODIFIED + " INTEGER," +
                LanguageDataSource.TABLE_LANGUAGE_DIRECTION + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_NAME + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_SLUG + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_RESOURCES_URL + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_PROJECT_DESCRIPTION + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_PROJECT_META + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_PROJECT_NAME + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_PROJECT_SORT + " VARCHAR)";

        return creationString;
    }




}
