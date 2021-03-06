/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ListIterator;

import model.daoModels.Book;
import model.daoModels.DaoMaster;
import model.daoModels.DaoSession;
import model.daoModels.Version;
import model.parsers.MediaType;
import unfoldingword.DaoHelperMethods;
import unfoldingword.ModelNames;
import utils.FileNameHelper;
import utils.FileUtil;
import utils.UWPreferenceDataManager;

public class DatabaseOpenHelper extends DaoMaster.OpenHelper {

    private static final String TAG = "DatabaseOpenHelper";

    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private static boolean needsUpgrade;

    private static String DB_PATH;
    private static String DB_NAME;

    private static DatabaseOpenHelper sharedInstance;

    public static DatabaseOpenHelper getSharedInstance(Context context, String name, CursorFactory factory) {
        if(sharedInstance == null) {
            sharedInstance = new DatabaseOpenHelper(context, name, factory);
        }
        return sharedInstance;
    }

    public DatabaseOpenHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory);
        this.context = context;
        DB_NAME = name;

        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }

        try {
            createDataBase();
        } catch (Exception ioe) {
            ioe.printStackTrace();
            throw new Error("Unable to create database");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion < ModelNames.DB_VERSION_ID){
            needsUpgrade = true;
        }
        DaoSession session = new DaoMaster(db).newSession();
        Version version = Version.getModelForUniqueSlug("bibleasulb", session);
        if (version != null) {
            version.deleteContent(context);
        }
        UWPreferenceDataManager.resetChapterSelections(context, false);
        Log.i(TAG, "Upgraded DB From Version " + oldVersion + " To Version " + newVersion);
    }

    public void deleteDatabase() {
        close();
        String databasePath = DB_PATH + DB_NAME;
        boolean result = new File(databasePath).delete();
        Log.d(TAG, "Database Deleted Result: " + result);
    }

    /** Open Database for Use */
    public void openDatabase() {
        String databasePath = DB_PATH + DB_NAME;
        sqliteDatabase = SQLiteDatabase.openDatabase(databasePath, null,
        (SQLiteDatabase.OPEN_READWRITE));
    }

    public void openDatabaseReadable() {
        String databasePath = DB_PATH + DB_NAME;
        sqliteDatabase = SQLiteDatabase.openDatabase(databasePath, null,
        (SQLiteDatabase.OPEN_READONLY));
    }

    /** Close Database after use */
    @Override
    public synchronized void close() {
        if ((sqliteDatabase != null) && sqliteDatabase.isOpen()) {
            sqliteDatabase.close();
        }
        super.close();
    }

    /** Get database instance for use */
    public SQLiteDatabase getSqliteDatabase() {
        return sqliteDatabase;
    }

    /** Create new database if not present */
    synchronized public void createDataBase() {

        if (!databaseExists()) {

            SQLiteDatabase sqliteDatabase = this.getReadableDatabase();
            /* Database does not exists create blank database */
            sqliteDatabase.close();
            populateWithPreload();
        }
//        else if(needsUpgrade()) {
//            populateWithPreload();
//            UWPreferenceDataManager.resetChapterSelections(context);
//            SQLiteDatabase sqliteDatabase = this.getReadableDatabase();
//            Log.i(TAG, "Got here");
//        }
    }

    public void upgradeIfNeeded() {
        if(needsUpgrade) {
//            populateWithPreload();
//            UWPreferenceDataManager.resetChapterSelections(context);
            needsUpgrade = false;
        }
    }

    private void populateWithPreload(){
        copyDataBase();
        saveSourceFiles();
    }

    public void saveDatabase(){

        if(databaseExists()){
            String databasePath = DB_PATH + DB_NAME;

            File databaseInputFile = new File(databasePath);
            byte[] bytes = FileUtil.getBytesFromFile(databaseInputFile);
            FileUtil.saveFileToSdCard(context, bytes, DB_NAME);
        }
    }

    /** Check Database if it exists */
    private boolean databaseExists() {
        SQLiteDatabase sqliteDatabase = null;
        try {
            String databasePath = DB_PATH + DB_NAME;
            sqliteDatabase = SQLiteDatabase.openDatabase(databasePath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }
        return sqliteDatabase != null ? true : false;
    }

    private boolean needsUpgrade() {
        SQLiteDatabase sqliteDatabase = null;
        try {
            String databasePath = DB_PATH + DB_NAME;
            sqliteDatabase = SQLiteDatabase.openDatabase(databasePath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (sqliteDatabase != null) {
            if(sqliteDatabase.getVersion() < ModelNames.DB_VERSION_ID) {
                sqliteDatabase.close();
                return true;
            }
            sqliteDatabase.close();
        }
        return false;
    }

    /**
     * Copy existing database file in system
     */
    public void copyDataBase() {

        int length;
        byte[] buffer = new byte[1024];
        String databasePath = DB_PATH + DB_NAME;

        try {
            InputStream databaseInputFile = this.context.getAssets().open(DB_NAME);
            OutputStream databaseOutputFile = new FileOutputStream(databasePath);

            while ((length = databaseInputFile.read(buffer)) > 0) {
                databaseOutputFile.write(buffer, 0, length);
                databaseOutputFile.flush();
            }
            databaseInputFile.close();
            databaseOutputFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSourceFiles(){

//        openDatabaseReadable();
        List<Book> books = null;

        while(books == null) {
            try {
                books = DaoDBHelper.getDaoSession(context)
                        .getBookDao().queryBuilder().list();
            }
            catch (SQLiteDatabaseLockedException e){
                e.printStackTrace();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException newE) {
                    newE.printStackTrace();
                }
//                sqliteDatabase.close();
//                openDatabaseReadable();
            }
        }

        ListIterator li = books.listIterator(books.size());

        // Iterate in reverse to start with the stories.
        while(li.hasPrevious()) {

            Book book = (Book) li.previous();
            try {
                String signature = loadDbFile(FileNameHelper.getSaveFileNameFromUrl(book.getSignatureUrl()));
                byte[] text = loadDbFileBytes(FileNameHelper.getSaveFileNameFromUrl(book.getSourceUrl()));
                DataFileManager.saveDataForBook(context, book, text, MediaType.MEDIA_TYPE_TEXT);
                DataFileManager.saveSignatureForBook(context, book, signature.getBytes(), MediaType.MEDIA_TYPE_TEXT);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void saveFile(byte[] bytes, String url){

        try{
            FileOutputStream fos = context.openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.close();
            Log.i(TAG, "File Saved");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving file");
        }
    }

    private byte[] loadDbFileBytes(String fileName) throws IOException{

        // Open your local model.db as the input stream
        InputStream inputStream = context.getAssets().open("preloaded_content/" + fileName);

        return ByteStreams.toByteArray(inputStream);
    }

    private String loadDbFile(String fileName) throws IOException{

        // Open your local model.db as the input stream
        InputStream inputStream = context.getAssets().open("preloaded_content/" + fileName);
        return new String(ByteStreams.toByteArray(inputStream));
    }
}