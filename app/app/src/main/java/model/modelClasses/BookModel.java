package model.modelClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.db.AMDatabaseModelAbstractObject;
import model.db.DBManager;
import parser.JsonParser;
import utils.DBUtils;

/**
 * Created by Fechner on 1/9/15.
 */
public class BookModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "BookModel";

    static final String DATE_MODIFIED = "date_modified";
    static final String DIRECTION = "direction";
    static final String LANGUAGE = "language";
    static final String APP_WORDS = "app_words";
    static final String CHAPTERS = "chapters";

    public long dateModified;
    public String direction;
    public String language;
    public AppWordsModel appWords = new AppWordsModel();

    private LanguageModel parent = null;
    public LanguageModel getParent(Context context){

        if(parent == null){
            parent = DBManager.getInstance(context).getLanguageModelForLanguage(language);
        }

        return parent;
    }
    public void setParent(LanguageModel parent){
        this.parent = parent;
    }

    private ArrayList<ChapterModel> chapters = null;
    public ArrayList<ChapterModel> getChildModels(Context context){

        if(chapters == null){
            chapters = DBManager.getInstance(context).getAllChaptersForBook(this);
        }

        return chapters;
    }
    public BookModel(){
        super();
        appWords = new AppWordsModel(this);
    }

//    public ChapterModel getNextChapter(ChapterModel chapter){
//
//        int index = chapters.indexOf(chapter);
//
//        ChapterModel nextChapter = chapters.get(index + 1);
//        return nextChapter;
//    }

//    public Map<String,  PageModel> getAllPagesAsDictionary(){
//
//        Map<String,  PageModel> chapterMap = new HashMap<String, PageModel>();
//
//        for(ChapterModel chapter : chapters){
//            chapterMap.putAll(chapter.getAllPagesAsDictionary());
//        }
//
//        if(chapterMap.size() > 0){
//            return chapterMap;
//        }
//        else{
//            return null;
//        }
//    }

//    public ChapterModel getChapterForNumber(String chapterNumber){
//
//        for(ChapterModel chapter : chapters){
//            if(chapterNumber.equalsIgnoreCase(chapter.number)){
//                return chapter;
//            }
//        }
//
//        return null;
//    }

    //region DatabaseInterface

    public void initModelFromJsonObject(JSONObject jsonObj){

        try {
            long date = -1;

            if (jsonObj.has(DATE_MODIFIED)) {
                String dateString = jsonObj.getString(DATE_MODIFIED);
                date = JsonParser.getSecondsFromDateString(dateString);
            }

            this.dateModified = date > 0 ? date : -1;
            this.direction = jsonObj.has(DIRECTION) ? jsonObj.getString(DIRECTION) : "";
            this.language = jsonObj.has(LANGUAGE) ? jsonObj.getString(LANGUAGE) : "";

            JSONObject wordsModelObj = jsonObj.getJSONObject(APP_WORDS);
            AppWordsModel wordsModel = AppWordsModel.getAppWordsModelFromJsonObject(wordsModelObj, this);
            if(wordsModel != null){
                this.appWords = wordsModel;
            }

            if (jsonObj.has(CHAPTERS)) {

                chapters = new ArrayList<ChapterModel>();
                JSONArray jsonArray = jsonObj.getJSONArray(CHAPTERS);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject pageJsonObj = jsonArray.getJSONObject(i);

                    ChapterModel chapter = new ChapterModel();
                    chapter.initModelFromJsonObject(pageJsonObj, language);
                    chapters.add(chapter);
                }
            }
        }
        catch (JSONException e){
            Log.e(TAG, "BookModel JSON Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public ContentValues getModelAsContentValues(){

        ContentValues values = appWords.getModelAsContentValues();
        values.put(DBUtils.COLUMN_BOOK_MODIFIED, Long.toString(dateModified));
        values.put(DBUtils.COLUMN_BOOK_TXT_DIRECTION, direction);
        values.put(DBUtils.COLUMN_BOOK_LANGUAGE_ABBREVIATION, language);

        return values;
    }

    public void initModelFromCursor(Cursor cursor){

        this.dateModified =  cursor.getLong(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_MODIFIED));
        this.direction = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_MODIFIED));
        this.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_LANGUAGE_ABBREVIATION));

        this.appWords.cancel = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_CANCEL));
        this.appWords.chapters = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_CHAPTERS));
        this.appWords.languages = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_LANGUAGES));
        this.appWords.nextChapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_NEXT_CHAPTER));
        this.appWords.ok = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_OK));
        this.appWords.removeLocally = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_REMOVE_LOCALLY));
        this.appWords.removeThisString = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_REMOVE_THIS_STRING));
        this.appWords.saveLocally = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_SAVE_LOCALLY));
        this.appWords.saveThisString = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_SAVE_THIS_STRING));
        this.appWords.selectALanguage = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_BOOK_WORDS_SELECT_A_LANGUAGE));

    }

    public String getSqlTableName() {
        return DBUtils.TABLE_BOOK;
    }

    public String getSqlUpdateWhereClause() {
        return DBUtils.COLUMN_BOOK_LANGUAGE_ABBREVIATION + "=?";
    }

    public String[] getSqlUpdateWhereArgs() {
        return new String[]{language};
    }

    public String getChildrenQuery(){
        return DBUtils.QUERY_SELECT_CHAPTER_BASED_ON_BOOK;
    }

    //endregion


    @Override
    public String toString() {
        return "BookModel{" +
                "dateModified=" + dateModified +
                ", direction='" + direction + '\'' +
                ", language='" + language + '\'' +
                ", appWords=" + appWords.toString() +
                '}';
    }
}
