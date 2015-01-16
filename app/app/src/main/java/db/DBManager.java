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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.BookModel;
import models.ChapterModel;
import models.LanguageModel;
import utils.DBUtils;
import utils.JsonUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class DBManager extends SQLiteOpenHelper {

    private static String TAG = "DBManager";

    private static DBManager dbManager;
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
        InputStream inputStream = context.getAssets().open(DBUtils.DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DBUtils.DB_NAME;

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
            String myPath = DB_PATH + DBUtils.DB_NAME;;
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

        ContentValues values = model.getModelAsContentValues();

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
     * @param model $ChapterModel
     * @return
     */
    public boolean addChapters(ChapterModel model) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = model.getModelAsContentValues();
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
                LanguageModel model = getLanguageFromCursor(cursor);
                models.add(model);
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return models;
    }

    public Map<String,  LanguageModel> getAllLanguagesAsMap() {
        Map<String,  LanguageModel> models = new HashMap<String, LanguageModel>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_ALL_LANGUAGES, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LanguageModel model = getLanguageFromCursor(cursor);
                models.put(model.id, model);
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return models;
    }

    /**
     * loads a LanguageModel from a cursor
     * @param cursor
     * @return
     */
    private LanguageModel getLanguageFromCursor(Cursor cursor){

        LanguageModel model = new LanguageModel();

        model.dateModified =  cursor.getLong(cursor.getColumnIndex(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG));
        model.direction = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG));
        model.status.checkingEntity = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG));
        model.status.checkingLevel = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG));
        model.status.comments = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG));
        model.status.contributors = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG));
        model.status.publishDate = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG));
        model.status.sourceText = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG));
        model.status.sourceTextVersion = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG));
        model.status.version = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_VERSION_TABLE_LANGUAGE_CATALOG));
        model.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG));
        model.languageName = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG));

        try {
            model.books = getAllChapters(model);
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return model;
    }

    /**
     * Getting all chapter details from db
     *
     * @return ArrayList
     */
    public ArrayList<BookModel> getAllChapters(LanguageModel languageModel) throws JSONException {

        BookModel bookModel = new BookModel(languageModel);

        ArrayList<BookModel> bookModels = null;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_CHAPTER_BASED_ON_LANGUAGE, new String[]{languageModel.language});
        if (cursor != null) {
            bookModels = new ArrayList<BookModel>();
            bookModels.add(bookModel);
            while (cursor.moveToNext()) {
                ChapterModel model = getModelFromCursor(cursor, bookModel);
                bookModel.chapters.add(model);
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();


        return bookModels;
    }


    /**
     * loads a ChapterModel from a Cursor
     * @param cursor
     * @param parentBook
     * @return
     * @throws JSONException
     */
    private ChapterModel getModelFromCursor(Cursor cursor,  BookModel parentBook) throws JSONException{

        ChapterModel model = new ChapterModel(parentBook);

        model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
        model.reference = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO));
        model.title = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO));
        model.setFramesJson(cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO)));
        model.parentBook.appWords.languages = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO));
        model.parentBook.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO));
        model.parentBook.appWords.chapters = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO));
        model.parentBook.appWords.next_chapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO));
        model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
        model.auto_id = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO));
        JSONArray array = new JSONArray(model.getFramesJson());
        JSONObject object = array.getJSONObject(0);

        model.id = object.has(JsonUtils.ID) ? object.getString(JsonUtils.ID) : "";

        return model;
    }

//    /**
//     * Getting all chapter details from db
//     *
//     * @return ArrayList
//     */
//    public ArrayList<ChapterModel> getAllChapters(String languageName) throws JSONException {
//        ArrayList<ChapterModel> models = null;
//        SQLiteDatabase database = getReadableDatabase();
//        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_CHAPTER_BASED_ON_LANGUAGE, new String[]{languageName});
//        if (cursor != null) {
//            models = new ArrayList<ChapterModel>();
//            while (cursor.moveToNext()) {
//                ChapterModel model = new ChapterModel();
//
//                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
//                model.reference = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO));
//                model.title = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO));
//                model.setFramesJson(cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO)));
//                model.parentBook.appWords.languages = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO));
//                model.parentBook.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO));
//                model.parentBook.appWords.chapters = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO));
//                model.parentBook.appWords.next_chapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO));
//                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
//                model.auto_id = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO));
//                JSONArray array = new JSONArray(model.getFramesJson());
//                JSONObject object = array.getJSONObject(0);
//
//                model.id = object.has(JsonUtils.ID) ? object.getString(JsonUtils.ID) : "";
//                models.add(model);
//            }
//        }
//        if (cursor != null)
//            cursor.close();
//        database.close();
//        return models;
//    }

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

//    /**
//     * Getting next chapters
//     *
//     * @param chapter_id
//     * @return
//     * @throws JSONException
//     */
//    public ChapterModel getNextChapter(String chapter_id, String languages) throws JSONException {
//
//        SQLiteDatabase database = getReadableDatabase();
//        ChapterModel model = new ChapterModel();
//        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_NEXT_CHAPTER, new String[]{chapter_id, languages});
//        if (cursor != null) {
//            if (cursor.moveToNext()) {
//
//
//                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
//                model.reference = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO));
//                model.title = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO));
//                model.setFramesJson(cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO)));
//                model.parentBook.appWords.languages = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO));
//                model.parentBook.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO));
//                model.parentBook.appWords.chapters = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO));
//                model.parentBook.appWords.next_chapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO));
//                model.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO));
//                model.auto_id = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO));
//
//                JSONArray array = new JSONArray(model.getFramesJson());
//                model.parentBook.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO));
//                JSONObject object = array.getJSONObject(0);
//
//                model.id = object.has(JsonUtils.ID) ? object.getString(JsonUtils.ID) : "";
//            }
//        }
//        if (cursor != null)
//            cursor.close();
//        database.close();
//        return model;
//    }

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

//    /**
//     * Getting all modified date from DB
//     *
//     * @return
//     */
//    public String[] getAllDate() {
//        SQLiteDatabase database = getReadableDatabase();
//        Cursor cursor = database.rawQuery(DBUtils.QUERY_GET_MOD_DATE, null);
//        String[] list = null;
//        if (cursor != null) {
//            list = new String[cursor.getCount()];
//            int i = 0;
//            while (cursor.moveToNext()) {
//                list[i] = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG));
//                i++;
//            }
//            cursor.close();
//            database.close();
//        }
//        return list;
//    }


    /**
     * Updating language info
     *
     * @param model
     * @return
     */
    public ArrayList<ChapterModel> updateLanguage(LanguageModel model) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = model.getModelAsContentValues();

        int update = database.update(DBUtils.TABLE_LANGUAGE_CATALOG, values, DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + "=?", new String[]{model.language});

        ArrayList<ChapterModel> chapters = new ArrayList<ChapterModel>();
        for (ChapterModel chapterModel : model.books.get(0).chapters) {
            boolean valuea = dbManager.updateChapter(model.language, chapterModel);

            chapters.add(chapterModel);
        }

        if (update > 0 && !chapters.isEmpty()) {
            return chapters;
        }
        else {
            return null;
        }
    }

    /**
     * Updating chapter info
     *
     * @param language
     * @param model
     * @return
     */
    public boolean updateChapter(String language, ChapterModel model) {

        Log.i(TAG, "Updating chapter: " + model.title + " for language: " + language);

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = model.getModelAsContentValues();
        int update = database.update(DBUtils.TABLE_FRAME_INFO, values, DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO
                + "=? AND " + DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO
                + "=?", new String[]{model.number, model.parentBook.language});
        if (update > 0) {
            return true;
        }
        return false;
    }
}
