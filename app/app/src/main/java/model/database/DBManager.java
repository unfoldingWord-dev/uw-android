package model.database;

import android.content.Context;
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

import model.database.AMDatabaseIndex;
import model.datasource.ProjectDataSource;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class DBManager extends SQLiteOpenHelper {

    /**
     * This needs to match up with the most recently updated Language model
     */


    private static String TAG = "DBManager";

    /// Current DB model version should be put here
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
    public synchronized static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    private DBManager(Context context) {
        super(context, AMDatabaseIndex.DB_NAME, null, AMDatabaseIndex.DB_VERSION);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
    }

    private static int openingsCounter = 0;
    private SQLiteDatabase database = null;
    public synchronized SQLiteDatabase getDatabase(){

        if(database == null){
            openingsCounter = 0;
        }

        if(openingsCounter == 0){
            database = this.getWritableDatabase();
        }
        openingsCounter++;

        return database;
    }

    public synchronized void closeDatabase(){

        openingsCounter--;
        if(openingsCounter < 1){
            this.close();
        }
    }

    //region Overrides

    @Override
    public void onCreate(SQLiteDatabase database) {
        AMDatabaseIndex.createTables(database, this.context);
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
//            copyDataBase();
            getReadableDatabase();

        }
        else {
            getReadableDatabase();
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
            String myPath = DB_PATH + AMDatabaseIndex.DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

            int version = checkDB.getVersion();
            if(version < AMDatabaseIndex.DB_VERSION){
                return true;
            }

            if (checkDB != null) {
                checkDB.close();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "DB exception in shouldLoadSavedDb");
            return (checkDB == null)? true : false;
        }

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
        InputStream inputStream = context.getAssets().open(AMDatabaseIndex.DB_NAME);

        // Path to the just created empty model.db
        String outFileName = DB_PATH + AMDatabaseIndex.DB_NAME;

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


    //region Other

    private boolean dbIsUpdated(){

        ArrayList<ProjectModel> models =  new ProjectDataSource(this.context).getAllProjects();

        for(ProjectModel model : models ){
            ArrayList<LanguageModel> languageModels = model.getChildModels(context);
            for(LanguageModel langMod : languageModels) {
                if (langMod.dateModified >= AMDatabaseIndex.LAST_UPDATED) {
                    Log.i(TAG, "DB is up to date");
                    return true;
                }
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

//        if(shouldLoadSavedDb()){
//            return;
//        }
        try {
            Log.i(TAG, "is backing up database");
            // Open your local model.db as the input stream
            File dbFile = new File(this.getReadableDatabase().getPath());
            InputStream inputStream = new FileInputStream(dbFile);

            File sdCard = Environment.getExternalStorageDirectory();
//
            String backupDBPath = "unfoldingword/backupWords.sqlite";

            File backupDB = new File(sdCard, backupDBPath);

            if(!backupDB.exists()){
                backupDB.mkdir();
            }
            // Path to the just created empty model.db
            String outFileName =  backupDB.getPath();

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
