package model.modelClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.db.AMDatabaseModelAbstractObject;
import model.db.DBManager;
import parser.JsonParser;
import utils.DBUtils;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class LanguageModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "LanguageModel";

    private static final String DIRECTION = "direction";
    private static final String LANGUAGE = "language";
    private static final String LANGUAGE_NAME = "string";
    private static final String DATE_MODIFIED = "date_modified";
    private static final String STATUS = "status";


    public String id = "";
    public String auto_id = "";
    public long dateModified;
    public String direction = "";
    public String languageName = "";
    public String language = "";
    public StatusModel status = new StatusModel();

    private ArrayList<BookModel> books = null;
    public ArrayList<BookModel> getChildModels(Context context){

        if(books == null){
            books = DBManager.getInstance(context).getAllBooksForLanguage(this);
        }

        return books;
    }

    public LanguageModel(){
        super();
    };

//    public ArrayList<BookModel> getBooks(Context context){
//
//        try {
//            ArrayList<BookModel> books = DBManager.getInstance(context).getAllBooksForLanguage(this);
//            return books;
//        }
//        catch(JSONException e){
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public ChapterModel findChapterForNumber(Context context, String chapterNumber){
//
//        ChapterModel chapter = this.getBooks(context).get(0).getChapterForNumber(chapterNumber);
//
//        return chapter;
//    }
//
//    public Map<String,  PageModel> getAllPagesAsDictionary(Context context){
//
//        Map<String,  PageModel> chapterMap = new HashMap<String, PageModel>();
//
//        for(BookModel book : getBooks(context)){
//
//            chapterMap.putAll(book.getAllPagesAsDictionary());
//        }
//
//        if(chapterMap.size() > 0){
//            return chapterMap;
//        }
//        else{
//            return null;
//        }
//    }

    //region DatabaseInterface

    public void initModelFromJsonObject(JSONObject jsonObj){

        try {
            long date = -1;

            if (jsonObj.has(DATE_MODIFIED)) {
                String dateString = jsonObj.getString(DATE_MODIFIED);
                date = JsonParser.getSecondsFromDateString(dateString);
            }

            dateModified = date > 0 ? date : -1;
            direction = jsonObj.has(DIRECTION) ? jsonObj.getString(DIRECTION) : "";
            language = jsonObj.has(LANGUAGE) ? jsonObj.getString(LANGUAGE) : "";
            languageName = jsonObj.has(LANGUAGE_NAME) ? jsonObj.getString(LANGUAGE_NAME) : "";


            JSONObject statusJsonObject = jsonObj.getJSONObject(STATUS);
            StatusModel statMod = StatusModel.getStatusModelFromJsonObject(statusJsonObject, this);

            if(statMod != null){
                status = statMod;
            }
        }
        catch (JSONException e){
            Log.e(TAG, "LanguageModel JSON Exception: " + e.toString());
        }
    }

    public ContentValues getModelAsContentValues(){

        ContentValues values = status.getModelAsContentValues();

        values.put(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG, this.dateModified);
        values.put(DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG, this.direction);
        values.put(DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG, this.language);
        values.put(DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG, this.languageName);

        return values;
    }

    public void initModelFromCursor(Cursor cursor){

        this.dateModified =  cursor.getLong(cursor.getColumnIndex(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG));
        this.direction = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG));
        this.status.checkingEntity = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG));
        this.status.checkingLevel = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG));
        this.status.comments = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG));
        this.status.contributors = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG));
        this.status.publishDate = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG));
        this.status.sourceText = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG));
        this.status.sourceTextVersion = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG));
        this.status.version = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_VERSION_TABLE_LANGUAGE_CATALOG));
        this.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG));
        this.languageName = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG));
    }

    public String getSqlTableName() {
        return DBUtils.TABLE_LANGUAGE;
    }

    public String getSqlUpdateWhereClause() {
        return DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + "=?";
    }

    public String[] getSqlUpdateWhereArgs() {
        return new String[]{language};
    }

    public String getChildrenQuery(){
        return DBUtils.QUERY_SELECT_BOOK_BASED_ON_LANGUAGE;
    }
    //endregion


    @Override
    public String toString() {
        return "LanguageModel{" +
                "id='" + id + '\'' +
                ", auto_id='" + auto_id + '\'' +
                ", dateModified=" + dateModified +
                ", direction='" + direction + '\'' +
                ", languageName='" + languageName + '\'' +
                '}';
    }
}
