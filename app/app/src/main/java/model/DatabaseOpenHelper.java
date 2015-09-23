package model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.apache.commons.io.IOUtils;

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
import model.daoModels.Version;
import tasks.UpdateAndVerifyBookRunnable;
import unfoldingword.ModelNames;
import utils.FileNameHelper;
import utils.FileUtil;

public class DatabaseOpenHelper extends DaoMaster.OpenHelper {

    private static final String TAG = "DatabaseOpenHelper";

    private Context context;
    private SQLiteDatabase sqliteDatabase;

    private static String DB_PATH;
    private static String DB_NAME;

    public DatabaseOpenHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory);
        this.context = context;
        this.DB_NAME = name;

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

        if(oldVersion == 100 && newVersion == 101){
            db.execSQL("ALTER TABLE '" + ModelNames.VERIFICATION + "' ADD '" + "AUDIO_CHAPTER_ID" + "' INTEGER");
            db.setVersion(101);
        }
        Log.i(TAG, "Upgraded DB From Version " + oldVersion + " To Version " + newVersion);
    }

    /** Open Database for Use */
    public void openDatabase() {
        String databasePath = DB_PATH + DB_NAME;
        sqliteDatabase = SQLiteDatabase.openDatabase(databasePath, null,
                (SQLiteDatabase.OPEN_READWRITE));
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
    public void createDataBase() {

        if (!databaseExists()) {

            SQLiteDatabase sqliteDatabase = this.getReadableDatabase();
            /* Database does not exists create blank database */
            sqliteDatabase.close();
            populateWithPreload();
        }
        else {
            checkForUpgrade();
        }
    }

    private void checkForUpgrade(){

        SQLiteDatabase db = this.getWritableDatabase();
        if(db.getVersion() < ModelNames.DB_VERSION_ID){
            onUpgrade(db, db.getVersion(), ModelNames.DB_VERSION_ID);
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
            byte[] bytes = FileUtil.getbytesFromFile(databaseInputFile);
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

        List<Book> books = DaoDBHelper.getDaoSession(context)
                .getBookDao().queryBuilder().list();

        ListIterator li = books.listIterator(books.size());

        // Iterate in reverse to start with the stories.
        while(li.hasPrevious()) {

            Book book = (Book) li.previous();
            try {
                String signature = loadDbFile(FileNameHelper.getSaveFileNameFromUrl(book.getSignatureUrl()));
                byte[] text = loadDbFileBytes(FileNameHelper.getSaveFileNameFromUrl(book.getSourceUrl()));
                saveFile(text, book.getSourceUrl());
                saveFile(signature.getBytes("UTF-8"), book.getSignatureUrl());
//                Log.i(TAG, "Saved book: " + book.getTitle());
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

        return IOUtils.toByteArray(inputStream);
    }

    private String loadDbFile(String fileName) throws IOException{

        // Open your local model.db as the input stream
        InputStream inputStream = context.getAssets().open("preloaded_content/" + fileName);
        return IOUtils.toString(inputStream);
    }
}