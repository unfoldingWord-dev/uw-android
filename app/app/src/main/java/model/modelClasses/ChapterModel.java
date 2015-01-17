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
import utils.DBUtils;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class ChapterModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "LanguageModel";

    private static final String NUMBER = "number";
    private static final String TITLE = "title";
    private static final String REF = "ref";
    private static final String PAGES = "frames";

    public String number = "";
    public String reference = "";
    public String title = "";
    public String language = "";
    public String languageChapterKey = "";

    private BookModel parent = null;
    public BookModel getParent(Context context){

        if(parent == null){
            parent = DBManager.getInstance(context).getBookModelForLanguage(language);
        }

        return parent;
    }
    public void setParent(BookModel parent){
        this.parent = parent;
    }

    private ArrayList<PageModel> pages = null;
    public ArrayList<PageModel> getChildModels(Context context){

        if(pages == null){
            pages = DBManager.getInstance(context).getAllPagesForChapter(this);
        }

        return pages;
    }



    public ChapterModel() {
        super();
    }

    //region DatabaseInterface

    public void initModelFromJsonObject(JSONObject jsonObj, String language) {
        this.language = language;
        initModelFromJsonObject(jsonObj);
    }

    public void initModelFromJsonObject(JSONObject jsonObj) {

        try {
            this.number = jsonObj.has(NUMBER) ? jsonObj.getString(NUMBER) : "";
            this.reference = jsonObj.has(REF) ? jsonObj.getString(REF) : "";
            this.title = jsonObj.has(TITLE) ? jsonObj.getString(TITLE) : "";

            this.languageChapterKey = language + number;

            if (jsonObj.has(PAGES)) {

                pages = new ArrayList<PageModel>();
                JSONArray jsonArray = jsonObj.getJSONArray(PAGES);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject pageJsonObj = jsonArray.getJSONObject(i);

                    PageModel page = new PageModel();
                    page.initModelFromJsonObject(pageJsonObj, language);
                    pages.add(page);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "ChapterModel JSON Exception: " + e.toString());
        }

    }

    public ContentValues getModelAsContentValues() {

        ContentValues values = new ContentValues();
        values.put(DBUtils.COLUMN_CHAPTER_NUMBER, number);
        values.put(DBUtils.COLUMN_CHAPTER_REFERENCE, reference);
        values.put(DBUtils.COLUMN_CHAPTER_TITLE, title);
        values.put(DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_KEY, language);
        values.put(DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_CHAPTER_KEY, languageChapterKey);

        return values;
    }

    public void initModelFromCursor(Cursor cursor) {

        this.number = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTER_NUMBER));
        this.reference = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTER_REFERENCE));
        this.title = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTER_TITLE));
        this.language = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_KEY));
        this.languageChapterKey = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_CHAPTER_KEY));
    }

    public String getSqlTableName() {
        return DBUtils.TABLE_CHAPTER;
    }

    public String getSqlUpdateWhereClause() {
        return DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_CHAPTER_KEY + "=?";
    }

    public String[] getSqlUpdateWhereArgs() {
        return new String[]{language + number};
    }

    public String getChildrenQuery(){
        return DBUtils.QUERY_SELECT_PAGE_BASED_ON_CHAPTER;
    }

    //endregion


    @Override
    public String toString() {
        return "ChapterModel{" +
                "number='" + number + '\'' +
                ", reference='" + reference + '\'' +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}