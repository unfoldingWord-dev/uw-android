package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.LanguageLocaleModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class LanguageLocaleDataSource extends AMDatabaseDataSourceAbstract {

    static final String TABLE_LOCALE = "_table_locale";

    // Table columns of TABLE_PAGE
    static final String TABLE_LOCALE_COLUMN_UID = "_column_uid";
    static final String TABLE_LOCALE_COLUMN_SLUG = "_column_slug";
    static final String TABLE_LOCALE_COLUMN_GW = "_column_gw";
    static final String TABLE_LOCALE_COLUMN_LANGUAGE_DIRECTION = "_column_language_direction";
    static final String TABLE_LOCALE_COLUMN_LANGUAGE_KEY = "_column_language_key";
    static final String TABLE_LOCALE_COLUMN_LANGUAGE_NAME = "_column_language_name";
    static final String TABLE_LOCALE_COLUMN_CC = "_column_cc";
    static final String TABLE_LOCALE_COLUMN_LANGUAGE_REGION = "_column_language_region";

    public LanguageLocaleDataSource(Context context) {
        super(context);
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_LOCALE_COLUMN_SLUG;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return null;
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new StoriesChapterDataSource(this.context);
    }

    @Override
    protected String getParentIdColumnName() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_LOCALE;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_LOCALE_COLUMN_UID;
    }

    @Override
    public LanguageLocaleModel getModel(String uid) {
        return (LanguageLocaleModel) this.getModelForKey(uid);
    }

    @Override
    public LanguageLocaleModel getModelForSlug(String slug){
        return (LanguageLocaleModel) this.getModelFromDatabaseForSlug(slug);
    }

    @Override
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentID, boolean sideLoaded) {
        LanguageLocaleModel newModel = new LanguageLocaleModel(json, parentID, sideLoaded);
        LanguageLocaleModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
        }

        saveModel(newModel);
        return getModelForSlug(newModel.slug);
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model) {

        LanguageLocaleModel languageLocale = (LanguageLocaleModel) model;
        ContentValues values = new ContentValues();

        if(languageLocale.uid > 0) {
            values.put(TABLE_LOCALE_COLUMN_UID, languageLocale.uid);
        }
        values.put(TABLE_LOCALE_COLUMN_GW, languageLocale.gw);
        values.put(TABLE_LOCALE_COLUMN_SLUG, languageLocale.slug);

        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_DIRECTION, languageLocale.languageDirection);
        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_KEY, languageLocale.languageKey);
        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_NAME, languageLocale.languageName);
        values.put(TABLE_LOCALE_COLUMN_CC, languageLocale.cc);
        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_REGION, languageLocale.languageRegion);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        LanguageLocaleModel model = new LanguageLocaleModel();
        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_UID));
        model.gw = (cursor.getLong(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_GW)) > 0);
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_SLUG));

        model.languageDirection = cursor.getString(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_LANGUAGE_DIRECTION));
        model.languageKey = cursor.getString(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_LANGUAGE_KEY));
        model.languageName = cursor.getString(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_LANGUAGE_NAME));
        model.cc = cursor.getString(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_CC));
        model.languageRegion = cursor.getString(cursor.getColumnIndex(TABLE_LOCALE_COLUMN_LANGUAGE_REGION));

        return model;
    }

//    private class LanguageLocaleJsonModel {
//
//        boolean gw;
//        String ld;
//        String lc;
//        String ln;
//        String[] cc;
//        String lr;
//    }

    public void fastLoadJson(String json) throws JSONException{

//        LanguageLocaleJsonModel[] models = new Gson().fromJson(json, LanguageLocaleJsonModel[].class);
//        ArrayList<ContentValues> values = new ArrayList<ContentValues>();
//
//        for(LanguageLocaleJsonModel model : models){
//            values.add(getFastContentValues(model));
//        }
        ArrayList<ContentValues> fastValues = getContentValuesFromJson(json);
        this.fastAddModelsToDatabase(fastValues);
    }

    public ArrayList<ContentValues> getContentValuesFromJson(String json) throws JSONException{

        ArrayList<ContentValues> valuesList = new ArrayList<ContentValues>();
        JSONArray array = new JSONArray(json);

        for(int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);

            ContentValues values = new ContentValues();

            values.put(TABLE_LOCALE_COLUMN_GW, obj.getBoolean("gw"));
            values.put(TABLE_LOCALE_COLUMN_SLUG, obj.getString("lc"));

            values.put(TABLE_LOCALE_COLUMN_LANGUAGE_DIRECTION, obj.getString("ld"));
            values.put(TABLE_LOCALE_COLUMN_LANGUAGE_KEY, obj.getString("lc"));
            values.put(TABLE_LOCALE_COLUMN_LANGUAGE_NAME, obj.getString("ln"));
            values.put(TABLE_LOCALE_COLUMN_CC, obj.getJSONArray("cc").getString(0));
            values.put(TABLE_LOCALE_COLUMN_LANGUAGE_REGION, obj.getString("lr"));

            valuesList.add(values);
        }
        return valuesList;
    }

//    public ContentValues getFastContentValues(LanguageLocaleJsonModel model) {
//
//        ContentValues values = new ContentValues();
//
//        values.put(TABLE_LOCALE_COLUMN_GW, model.gw);
//        values.put(TABLE_LOCALE_COLUMN_SLUG, model.lc);
//
//        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_DIRECTION, model.ld);
//        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_KEY, model.lc);
//        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_NAME, model.ln);
//        values.put(TABLE_LOCALE_COLUMN_CC, model.cc[0]);
//        values.put(TABLE_LOCALE_COLUMN_LANGUAGE_REGION, model.lr);
//
//        return values;
//    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_GW + " INTEGER," +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_SLUG + " VARCHAR," +

                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_LANGUAGE_DIRECTION + " VARCHAR," +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_LANGUAGE_KEY + " VARCHAR," +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_LANGUAGE_NAME + " VARCHAR, " +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_CC + " VARCHAR, " +
                LanguageLocaleDataSource.TABLE_LOCALE_COLUMN_LANGUAGE_REGION + " VARCHAR)";
        return creationString;
    }
}

