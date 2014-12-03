package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import models.ChaptersModel;
import models.LanguageModel;
import utils.DBUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class DBManager extends SQLiteOpenHelper {

    private static DBManager dbManager;

    private DBManager(Context context) {
        super(context, DBUtils.DB_NAME, null, DBUtils.DB_VERSION);
    }

    /**
     * This is a single ton class
     * <p/>
     * return the instance of $DBManager
     *
     * @param context
     * @return instance of DBManager
     */
    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        // creating tables
        database.execSQL(DBUtils.CREATE_TABLE_LANGUAGE_CATALOG);
        database.execSQL(DBUtils.CREATE_TABLE_FRAME_INFO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    /**
     * Dropping table
     *
     * @param database
     */
    private void dropAllTables(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS "
                + DBUtils.TABLE_LANGUAGE_CATALOG);
        database.execSQL("DROP TABLE IF EXISTS "
                + DBUtils.TABLE_FRAME_INFO);
    }

    /**
     * It will be return the count of the row inside the
     *
     * @return int
     */
    public int getDataCount() {

        int count = 0;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_COUNT, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        cursor.close();
        database.close();
        return count;
    }

    /**
     * Downloaded data adding to db
     *
     * @param model
     * @return
     */
    public boolean addLanguage(LanguageModel model) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG, model.dateModified);
        values.put(DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG, model.direction);
        values.put(DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG, model.language);
        values.put(DBUtils.COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG, model.checkingEntity);
        values.put(DBUtils.COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG, model.checkingLevel);
        values.put(DBUtils.COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG, model.comments);
        values.put(DBUtils.COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG, model.contributors);
        values.put(DBUtils.COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG, model.publishDate);
        values.put(DBUtils.COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG, model.sourceText);
        values.put(DBUtils.COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG, model.sourceTextVersion);
        values.put(DBUtils.COLUMN_VERSION_TABLE_LANGUAGE_CATALOG, model.version);
        values.put(DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG, model.languageName);
        long insert = database.insert(DBUtils.TABLE_LANGUAGE_CATALOG, null, values);
        database.close();
        if (insert > 0) {
            Log.d("DB", "" + true);
            return true;
        }
        Log.d("DB", "" + false);
        return false;
    }

    /**
     * Adding chapters to db
     *
     * @param model $ChaptersModel
     * @return
     */
    public boolean addChapters(ChaptersModel model) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO, model.number);
        values.put(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO, model.references);
        values.put(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO, model.title);
        values.put(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO, model.jsonArray);
        long insert = database.insert(DBUtils.TABLE_FRAME_INFO, null, values);
        database.close();
        if (insert > 0) {
            Log.d("DB", "" + true);
            return true;
        }
        Log.d("DB", "" + false);
        return false;
    }

    /**
     * @return
     */
    public List<LanguageModel> getAllLanguages() {
        List<LanguageModel> models = new ArrayList<LanguageModel>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_ALL_LANGUAGES, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LanguageModel model = new LanguageModel();
                model.dateModified = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG));
                model.direction = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG));
                model.checkingEntity = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG));
                model.checkingLevel = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG));
                model.comments = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG));
                model.contributors = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG));
                model.publishDate = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG));
                model.sourceText = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG));
                model.sourceTextVersion = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG));
                model.version = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_VERSION_TABLE_LANGUAGE_CATALOG));
                model.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG));
                model.languageName = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG));
                models.add(model);
            }
        }
        return models;
    }
}
