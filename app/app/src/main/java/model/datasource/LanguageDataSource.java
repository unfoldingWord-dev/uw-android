package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class LanguageDataSource extends AMDatabaseDataSourceAbstract {


    static final String TABLE_LANGUAGE = "_table_language";

    //Table columns of TABLE_PROJECT
    static final String TABLE_LANGUAGE_UID = "_column_language_uid";
    static final String TABLE_LANGUAGE_PARENT_ID = "_column_parent_id";
    static final String TABLE_LANGUAGE_DATE_MODIFIED = "_column_date_modified";
    static final String TABLE_LANGUAGE_LANGUAGE_ABBREVIATION = "_column_lang_abbrev";
    static final String TABLE_LANGUAGE_SLUG = "_column_slug";

    public ArrayList<String> getAvailableLanguages(){

        return this.getUniqueValuesForColumn(TABLE_LANGUAGE_LANGUAGE_ABBREVIATION);
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
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentId, boolean sideLoaded) {
        LanguageModel newModel = new LanguageModel(json, parentId, sideLoaded);
        LanguageModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
        }

        if (currentModel == null || (currentModel.dateModified < newModel.dateModified)) {

            saveModel(newModel);
            return getModelForSlug(newModel.slug);
        }
        else{
            return null;
        }
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
        model.languageAbbreviation = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_LANGUAGE_ABBREVIATION));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_LANGUAGE_SLUG));

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
        values.put(TABLE_LANGUAGE_LANGUAGE_ABBREVIATION, languageModel.languageAbbreviation);
        values.put(TABLE_LANGUAGE_SLUG, languageModel.slug);

        return values;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                LanguageDataSource.TABLE_LANGUAGE_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LanguageDataSource.TABLE_LANGUAGE_PARENT_ID + " INTEGER," +
                LanguageDataSource.TABLE_LANGUAGE_DATE_MODIFIED + " INTEGER," +
                LanguageDataSource.TABLE_LANGUAGE_LANGUAGE_ABBREVIATION + " VARCHAR," +
                LanguageDataSource.TABLE_LANGUAGE_SLUG + " VARCHAR)";

        return creationString;
    }




}
