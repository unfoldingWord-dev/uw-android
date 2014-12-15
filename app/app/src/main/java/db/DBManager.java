package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import models.ChaptersModel;
import models.LanguageModel;
import utils.DBUtils;
import utils.JsonUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class DBManager extends SQLiteOpenHelper {

    private static DBManager dbManager;
    private static String DATABASE_NAME = "_un_folding_word";
    private Context context;
    private String DB_PATH = "";

    private DBManager(Context context) {
        super(context, DBUtils.DB_NAME, null, DBUtils.DB_VERSION);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
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
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {
        boolean value = checkDBExist();
        if (value) {
            // do nothing the data base existe.printStackTrace();
        } else {
            getReadableDatabase();
            copyDataBase();
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        // Open your local db as the input stream
        InputStream inputStream = context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        // Open the empty db as the output stream
        FileOutputStream outputStream = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        // Close the streams
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * Check if the database is exist
     *
     * @return
     */
    private boolean checkDBExist() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);


            if (checkDB != null) {

                checkDB.close();

            }
        } catch (Exception e) {
            return checkDB != null ? true : false;
        }
        return checkDB != null ? true : false;
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
        values.put(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO, model.loadedLanguage);
        values.put(DBUtils.COLUMN_CANCEL_LANGUAGE_TABLE_FRAME_INFO, model.cancel);
        values.put(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO, model.chapters);
        values.put(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO, model.languages);
        values.put(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO, model.next_chapter);
        values.put(DBUtils.COLUMN_OK_LANGUAGE_TABLE_FRAME_INFO, model.ok);
        values.put(DBUtils.COLUMN_REMOVE_LOCALLY_LANGUAGE_TABLE_FRAME_INFO, model.remove_locally);
        values.put(DBUtils.COLUMN_OK_REMOVE_THIS_STRING_TABLE_FRAME_INFO, model.remove_this_string);
        values.put(DBUtils.COLUMN_SAVE_LOCALLY_LANGUAGE_TABLE_FRAME_INFO, model.save_locally);
        values.put(DBUtils.COLUMN_SAVE_THIS_STRING_LANGUAGE_TABLE_FRAME_INFO, model.save_this_string);
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
     * Getting all languages
     *
     * @return Return list of LanguageModels
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
        if (cursor != null)
            cursor.close();
        database.close();
        return models;
    }

    /**
     * Getting all chapter details from db
     *
     * @return ArrayList
     */
    public ArrayList<ChaptersModel> getAllChapters(String languageName) throws JSONException {
        ArrayList<ChaptersModel> models = null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_CHAPTER_BASED_ON_LANGUAGE, new String[]{languageName});
        if (cursor != null) {
            models = new ArrayList<ChaptersModel>();
            while (cursor.moveToNext()) {
                ChaptersModel model = new ChaptersModel();

                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
                model.references = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO));
                model.title = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO));
                model.jsonArray = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO));
                model.loadedLanguage = languageName;
                model.languages = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO));
                model.loadedLanguage = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO));
                model.chapters = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO));
                model.next_chapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO));
                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
                model.auto_id = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO));
                JSONArray array = new JSONArray(model.jsonArray);
                JSONObject object = array.getJSONObject(0);

                model.imgUrl = object.has(JsonUtils.IMAGE_URL) ? object.getString(JsonUtils.IMAGE_URL) : "";
                model.id = object.has(JsonUtils.ID) ? object.getString(JsonUtils.ID) : "";
                models.add(model);
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return models;
    }

    /**
     * Display words depends on Languages
     *
     * @param languageName
     * @return return word
     */
    public String getLanguageDisp(String languageName) {
        String name = "";
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_DISP_LANGUAGE, new String[]{languageName});
        if (cursor != null) {
            if (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO));
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return name;
    }

    /**
     * Getting next chapters
     *
     * @param chapter_id
     * @return
     * @throws JSONException
     */
    public ChaptersModel getNextChapter(String chapter_id, String languages) throws JSONException {

        SQLiteDatabase database = getReadableDatabase();
        ChaptersModel model = new ChaptersModel();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_NEXT_CHAPTER, new String[]{chapter_id, languages});
        if (cursor != null) {
            if (cursor.moveToNext()) {


                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
                model.references = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO));
                model.title = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO));
                model.jsonArray = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO));
                model.languages = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO));
                model.chapters = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO));
                model.next_chapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO));
                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
                model.auto_id = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO));
                JSONArray array = new JSONArray(model.jsonArray);
                model.loadedLanguage = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO));
                JSONObject object = array.getJSONObject(0);
                model.imgUrl = object.has(JsonUtils.IMAGE_URL) ? object.getString(JsonUtils.IMAGE_URL) : "";
                model.id = object.has(JsonUtils.ID) ? object.getString(JsonUtils.ID) : "";
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return model;
    }

    /**
     * Getting Frame count
     *
     * @return
     */
    public int getFrameCount() {
        SQLiteDatabase database = getReadableDatabase();
        int count = 0;
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_FRAME_COUNT, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO)));
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return count;
    }

    /**
     * Getting all modified date from DB
     *
     * @return
     */
    public String[] getAllDate() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_MOD_DATE, null);
        String[] list = null;
        if (cursor != null) {
            list = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                list[i] = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG));
                i++;
            }
            cursor.close();
            database.close();
        }
        return list;
    }


    /**
     * Updating language info
     *
     * @param model
     * @return
     */
    public boolean upDateLanguage(LanguageModel model) {
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
        int update = database.update(DBUtils.TABLE_LANGUAGE_CATALOG, values, DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + "=?", new String[]{model.language});
        if (update > 0) {
            return true;
        }
        return false;
    }

    /**
     * Updating chapter info
     *
     * @param language
     * @param model
     * @return
     */
    public boolean updateChapter(String language, ChaptersModel model) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO, model.number);
        values.put(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO, model.references);
        values.put(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO, model.title);
        values.put(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO, model.jsonArray);
        values.put(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO, model.loadedLanguage);
        values.put(DBUtils.COLUMN_CANCEL_LANGUAGE_TABLE_FRAME_INFO, model.cancel);
        values.put(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO, model.chapters);
        values.put(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO, model.languages);
        values.put(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO, model.next_chapter);
        values.put(DBUtils.COLUMN_OK_LANGUAGE_TABLE_FRAME_INFO, model.ok);
        values.put(DBUtils.COLUMN_REMOVE_LOCALLY_LANGUAGE_TABLE_FRAME_INFO, model.remove_locally);
        values.put(DBUtils.COLUMN_OK_REMOVE_THIS_STRING_TABLE_FRAME_INFO, model.remove_this_string);
        values.put(DBUtils.COLUMN_SAVE_LOCALLY_LANGUAGE_TABLE_FRAME_INFO, model.save_locally);
        values.put(DBUtils.COLUMN_SAVE_THIS_STRING_LANGUAGE_TABLE_FRAME_INFO, model.save_this_string);
        int update = database.update(DBUtils.TABLE_FRAME_INFO, values, DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO + "=? AND " + DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO + "=?", new String[]{model.number, model.loadedLanguage});
        if (update > 0) {
            return true;
        }
        return false;
    }
}
