package model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import model.datasource.BibleChapterDataSource;
import model.datasource.BookDataSource;
import model.datasource.LanguageDataSource;
import model.datasource.PageDataSource;
import model.datasource.ProjectDataSource;
import model.datasource.StoriesChapterDataSource;
import model.datasource.VersionDataSource;

/**
 * Created by Fechner on 2/25/15.
 */
public class AMDatabaseIndex {

    public static final String DB_NAME = "_un_folding_word";
    public static final int DB_VERSION = 6;
    public static final int LAST_UPDATED = 20150310;
    private static AMDatabaseIndex ourInstance = new AMDatabaseIndex();

    public static AMDatabaseIndex getInstance() {
        return ourInstance;
    }



    private AMDatabaseIndex() {

    }

    static public void createTables(SQLiteDatabase database, Context context){

        database.execSQL(new ProjectDataSource(context).getTableCreationString());
        database.execSQL(new LanguageDataSource(context).getTableCreationString());
        database.execSQL(new VersionDataSource(context).getTableCreationString());
        database.execSQL(new BookDataSource(context).getTableCreationString());
        database.execSQL(new BibleChapterDataSource(context).getTableCreationString());
        database.execSQL(new StoriesChapterDataSource(context).getTableCreationString());
        database.execSQL(new PageDataSource(context).getTableCreationString());
    }
}
