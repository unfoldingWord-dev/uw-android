package model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.modelClasses.BookModel;
import model.modelClasses.ChapterModel;
import model.modelClasses.LanguageModel;
import model.modelClasses.PageModel;
import utils.DBUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final int desiredDBVersionNumber = 2;

    private static String TAG = "DBManager";

    private static DBManager dbManager;
    private Context context;
    private String DB_PATH = "";

    /**
     * This is a singleton class
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

    private DBManager(Context context) {
        super(context, DBUtils.DB_NAME, null, DBUtils.DB_VERSION);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }

    //region Overrides


    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(DBUtils.CREATE_TABLE_PAGE_INFO);
        database.execSQL(DBUtils.CREATE_TABLE_LANGUAGE_CATALOG);
        database.execSQL(DBUtils.CREATE_TABLE_BOOK_INFO);
        database.execSQL(DBUtils.CREATE_TABLE_CHAPTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        Log.i(TAG, "Will update database from version: " + i + " To version: " + i2);
    }

    //region Initialization




    //endregion
    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase(boolean forceCreate) throws IOException {
        boolean value = checkDBExist();
        if (value) {
            // do nothing the data base existe.printStackTrace();
        } else if (forceCreate || !value) {
            getReadableDatabase();
            copyDataBase();
        }
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

            int version = checkDB.getVersion();
            if(version < desiredDBVersionNumber){
                return false;
            }

            if (checkDB != null) {

                checkDB.close();
            }
        } catch (Exception e) {
            return checkDB != null ? true : false;
        }

        boolean exists = (checkDB != null);
        return exists;
    }


    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        // Open your local model.db as the input stream
        InputStream inputStream = context.getAssets().open(DBUtils.DB_NAME);

        // Path to the just created empty model.db
        String outFileName = DB_PATH + DBUtils.DB_NAME;

        File currentFile = new File(outFileName);

        //make sure the file doesn't already exist.
        if(currentFile.exists()){
            Log.i(TAG, "had to delete already existing DB");
            currentFile.delete();
        }

        // Open the empty model.db as the output stream
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

    //endregion


    //region GetterQueries

    public LanguageModel getLanguageModelForLanguage(String language){

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_LANGUAGE_FROM_LANGUAGE_KEY, new String[]{language});

        LanguageModel model = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                model = new LanguageModel();
                model.initModelFromCursor(cursor);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return model;
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
                model.initModelFromCursor(cursor);
                models.add(model);
            }
        }
        if (cursor != null)
            cursor.close();
        database.close();
        return models;
    }

    public BookModel getBookModelForLanguage(String language){

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_BOOK_BASED_ON_LANGUAGE, new String[]{language});

        BookModel model = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                model = new BookModel();
                model.initModelFromCursor(cursor);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return model;
    }

    public ArrayList<BookModel> getAllBooksForLanguage(LanguageModel languageModel) {

        ArrayList<BookModel> bookModels = null;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = AMDatabaseManager.getCursorForChildren(database, languageModel);
        if (cursor != null) {
            bookModels = new ArrayList<BookModel>();
            while (cursor.moveToNext()) {
                BookModel model = new BookModel();
                model.initModelFromCursor(cursor);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return bookModels;
    }

    public ChapterModel getChapterForLanguageNumberKey(String langNumKey ){

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_CHAPTER_WITH_LANG_NUMB, new String[]{langNumKey});

        ChapterModel model = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                model = new ChapterModel();
                model.initModelFromCursor(cursor);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return model;
    }

    public ChapterModel getChapterForLanguageAndNumber(String language, String number ){


        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(DBUtils.QUERY_SELECT_CHAPTER_WITH_LANGUAGE_AND_NUMBER, new String[]{language, number});

        ChapterModel model = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                model = new ChapterModel();
                model.initModelFromCursor(cursor);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return model;
    }


    public ArrayList<ChapterModel> getAllChaptersForBook(BookModel bookModel){


        ArrayList<ChapterModel> chapterModels = null;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = AMDatabaseManager.getCursorForChildren(database, bookModel);
        if (cursor != null) {
            chapterModels = new ArrayList<ChapterModel>();
            while (cursor.moveToNext()) {
                ChapterModel model = new ChapterModel();
                model.initModelFromCursor(cursor);
                chapterModels.add(model);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return chapterModels;
    }

    public ArrayList<PageModel> getAllPagesForChapter(ChapterModel chapterModel){

        ArrayList<PageModel> chapterModels = null;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = AMDatabaseManager.getCursorForChildren(database, chapterModel);
        if (cursor != null) {
            chapterModels = new ArrayList<PageModel>();
            while (cursor.moveToNext()) {
                PageModel model = new PageModel();
                model.initModelFromCursor(cursor);
                chapterModels.add(model);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return chapterModels;
    }

    public PageModel getPageForKey(String key){


        PageModel pageModel = null;

        SQLiteDatabase database = getReadableDatabase();

        String query = DBUtils.QUERY_SELECT_PAGE_FROM_KEY;
        String[] args = new String[]{key};
        Cursor cursor = database.rawQuery(query, args);
        if (cursor != null) {

            while (cursor.moveToNext()) {
                PageModel model = new PageModel();
                model.initModelFromCursor(cursor);
                pageModel = model;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return pageModel;

    }

    //endregion


    //region Updating

    public boolean updateModel(AMDatabaseModelAbstractObject model){

        SQLiteDatabase database = getReadableDatabase();
        boolean success = AMDatabaseManager.updateModel(model, database);

        database.close();
        return success;
    }

    //endregion


    //region Other

    /**
     * Copies database to the external SD card
     */
    private void backupDatabase() throws IOException {

        // Open your local model.db as the input stream
        File dbFile = new File(this.getReadableDatabase().getPath());
        InputStream inputStream = new FileInputStream(dbFile);

        File sd = Environment.getExternalStorageDirectory();

        String backupDBPath = "/unfoldingword/backupWords.sqlite";
        File backupDB = new File(sd, backupDBPath);

        // Path to the just created empty model.db
//            String outFileName =  Environment.getExternalStorageDirectory();

        // Open the empty model.db as the output stream
        FileOutputStream outputStream = new FileOutputStream(backupDB);

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

    //endregion

}
