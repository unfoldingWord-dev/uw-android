package model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
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

    /**
     * This needs to match up with the most recently updated Language model
     */
    private static final int LAST_UPDATED = 20150210;

    private static String TAG = "DBManager";

    /// Current DB model version should be put here
    private static final int desiredDBVersionNumber = 2;
    private Context context;
    private String DB_PATH = "";



    private static DBManager dbManager;
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

    //region Initialization / loading




    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     * @param forceCreate
     * @throws IOException
     */
    public void createDataBase(boolean forceCreate) throws IOException {

        if(forceCreate || shouldLoadSavedDb()){
            getReadableDatabase();
            copyDataBase();
        }
//        backupDatabase();
    }

    /**
     * Check if the database is exist
     *
     * @return
     */
    private boolean shouldLoadSavedDb() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DBUtils.DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

            int version = checkDB.getVersion();
            if(version < desiredDBVersionNumber){
                return false;
            }

            if (checkDB != null) {

                checkDB.close();
            }
        }
        catch (Exception e) {
            Log.i(TAG, "DB exception in shouldLoadSavedDb");
            return (checkDB == null)? true : false;
        }



//        backupDatabase();
        boolean exists = (checkDB != null);

        if(exists){
            exists = this.dbIsUpdated();
        }
        boolean shouldUpdate = ! exists;
        return shouldUpdate;
    }

    //endregion

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database (or overrides/deletes it if it exists) in the system folder, from where it can be accessed and
     * handled. This is done by transferring ByteStream.
     *
     * @throws IOException
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

    /**
     * Gets all languages from the DB
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

    //region Getters using parent Model

    /**
     * gets the children of the passed AMDatabaseModelAbstractObject
     * @param model
     * @return
     */
    private ArrayList<AMDatabaseModelAbstractObject> getChildModelsForModel(AMDatabaseModelAbstractObject model){

        ArrayList<AMDatabaseModelAbstractObject> models = AMDatabaseManager.loadChildrenModelsForModel(getReadableDatabase(), model);

        return models;
    }

    /**
     * gets the children of the passed LanguageModel
     * @param model
     * @return
     */
    public ArrayList<BookModel> getChildModelsForLanguage(LanguageModel model){

        ArrayList<BookModel> books = new ArrayList<BookModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = getChildModelsForModel(model);

        for(AMDatabaseModelAbstractObject abstModel : models){
            books.add((BookModel) abstModel);
        }

        return books;
    }

    /**
     * gets the children of the passed BookModel
     * @param model
     * @return
     */
    public ArrayList<ChapterModel> getChildModelsForBook(BookModel model){

        ArrayList<ChapterModel> books = new ArrayList<ChapterModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = getChildModelsForModel(model);

        for(AMDatabaseModelAbstractObject abstModel : models){
            books.add((ChapterModel) abstModel);
        }

        return books;
    }

    /**
     * gets the children of the passed ChapterModel
     * @param model
     * @return
     */
    public ArrayList<PageModel> getChildModelsForChapter(ChapterModel model){

        ArrayList<PageModel> books = new ArrayList<PageModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = getChildModelsForModel(model);

        for(AMDatabaseModelAbstractObject abstModel : models){
            books.add((PageModel) abstModel);
        }

        return books;
    }

    //endregion

    //region getUsingKey

    /**
     * Gets a LanguageModel from the DB using the passed key
     * @param key
     * @return
     */
    public LanguageModel getLanguageModelForKey(String key){
        LanguageModel newModel = new LanguageModel();
        newModel = (LanguageModel) AMDatabaseManager.getModelForKey(getReadableDatabase(), newModel, key);

        return newModel;
    }

    /**
     * Gets a BookModel from the DB using the passed key
     * @param key
     * @return
     */
    public BookModel getBookModelForKey(String key){

        BookModel newModel = new BookModel();
        newModel = (BookModel) AMDatabaseManager.getModelForKey(getReadableDatabase(), newModel, key);

        return newModel;
    }

    /**
     * Gets a ChapterModel from the DB using the passed keys
     * @param language
     * @param number
     * @return
     */
    public ChapterModel getChapterModelForKey(String language, String number ){

        return getChapterModelForKey(language + number);
    }
    /**
     * Gets a ChapterModel from the DB using the passed key
     * @param key
     * @return
     */
    public ChapterModel getChapterModelForKey(String key){

        ChapterModel newModel = new ChapterModel();
        newModel = (ChapterModel) AMDatabaseManager.getModelForKey(getReadableDatabase(), newModel, key);

        return newModel;
    }

    /**
     * Gets a PageModel from the DB using the passed key
     * @param key
     * @return
     */
    public PageModel getPageModelForKey(String key){

        PageModel newModel = new PageModel();
        newModel = (PageModel) AMDatabaseManager.getModelForKey(getReadableDatabase(), newModel, key);

        return newModel;
    }


    //endRegion
    //endregion

    //region Updating


    /**
     * updates the passed model in the database
     * @param model
     * @return
     */
    public boolean updateModel(AMDatabaseModelAbstractObject model){

        SQLiteDatabase database = getReadableDatabase();
        boolean success = AMDatabaseManager.updateModel(model, database);

        database.close();
        return success;
    }

    //endregion

    //region Other

    private boolean dbIsUpdated(){

        List<LanguageModel> languages = this.getAllLanguages();

        for(LanguageModel model : languages ){
            if(model.dateModified >= LAST_UPDATED){
                Log.i(TAG, "DB is up to date");
                return true;
            }
        }
        Log.i(TAG, "DB is not up to date");
        return false;
    }
    /**
     * Copies database to the external SD card
     * @throws IOException
     */
    private void backupDatabase() {

        try {
            Log.i(TAG, "is backing up database");
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
            Log.i(TAG, "finished backup");
        }
        catch (IOException e){
            Log.e(TAG, "backup error");
            e.printStackTrace();
        }
    }

    //endregion
}
