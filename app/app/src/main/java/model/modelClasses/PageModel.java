package model.modelClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import model.db.AMDatabaseModelAbstractObject;
import model.db.DBManager;
import utils.DBUtils;

/**
 * Created by Fechner on 1/9/15.
 */
public class PageModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "PageModel";

    private static final String ID = "id";
    private static final String IMAGE_URL = "img";
    private static final String TEXT = "text";

    public String pageNumber = "";
    public String chapterNumber = "";
    public String text = "";
    public String imageUrl = "";
    public String languageAndChapter = "";
    public String languageChapterAndPage = "";

    private ChapterModel parent = null;
    public ChapterModel getParent(Context context){

        if(parent == null){
            parent = DBManager.getInstance(context).getChapterModelForKey(languageAndChapter);
        }

        return parent;
    }
    public void setParent(ChapterModel parent){
        this.parent = parent;
    }

    public String getComparableImageUrl(){

        String comparableUrl = imageUrl;
        if (comparableUrl.contains("}")) {
            comparableUrl = comparableUrl.replace("}", "");
        }
        if (comparableUrl.contains("{")) {
            comparableUrl = comparableUrl.replace("{", "");
        }
        return comparableUrl;
    }

    public PageModel() {
        super();
    }

    //region DatabaseInterface

    public void initModelFromJsonObject(JSONObject jsonObj, String language) {

        this.languageAndChapter = language;
        initModelFromJsonObject(jsonObj);
    }

    public void initModelFromJsonObject(JSONObject jsonObj) {

        try {
            String idString = jsonObj.has(ID) ? jsonObj.getString(ID) : "";
            if (idString.length() > 1) {
                String[] splitString = idString.split("-");
                this.chapterNumber = splitString[0];
                this.pageNumber = splitString[1];

                this.languageAndChapter += chapterNumber;
                this.languageChapterAndPage = languageAndChapter + pageNumber;
            }
            else{
                Log.e(TAG, "Error splitting PageModel id: " + idString);
            }
            this.imageUrl = jsonObj.has(IMAGE_URL) ? jsonObj.getString(IMAGE_URL) : "";
            this.text = jsonObj.has(TEXT) ? jsonObj.getString(TEXT) : "";
        } catch (JSONException e) {
            Log.e(TAG, "PageModel JSON Exception: " + e.toString());
        }

    }
    public ContentValues getModelAsContentValues() {

        ContentValues values = new ContentValues();
        values.put(DBUtils.COLUMN_PAGE_PAGE_NUMBER, pageNumber);
        values.put(DBUtils.COLUMN_PAGE_CHAPTER_NUMBER, chapterNumber);
        values.put(DBUtils.COLUMN_PAGE_TEXT, text);
        values.put(DBUtils.COLUMN_PAGE_IMG_URL, imageUrl);
        values.put(DBUtils.COLUMN_PAGE_LANGUAGE_AND_CHAPTER, languageAndChapter);
        values.put(DBUtils.COLUMN_PAGE_LANGUAGE_AND_KEY, languageChapterAndPage);

        return values;
    }

    public void initModelFromCursor(Cursor cursor) {

        this.pageNumber = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PAGE_PAGE_NUMBER));
        this.chapterNumber = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PAGE_CHAPTER_NUMBER));
        this.text = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PAGE_TEXT));
        this.imageUrl = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PAGE_IMG_URL));
        this.languageChapterAndPage = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PAGE_LANGUAGE_AND_KEY));
        this.languageAndChapter = cursor.getString(cursor.getColumnIndex(DBUtils.COLUMN_PAGE_LANGUAGE_AND_CHAPTER));
    }

    public String getSqlTableName() {
        return DBUtils.TABLE_PAGE;
    }

    public String getSqlUpdateWhereClause() {
        return DBUtils.COLUMN_PAGE_LANGUAGE_AND_KEY + "=?";
    }

    public String[] getSqlUpdateWhereArgs() {
        return new String[]{languageChapterAndPage};
    }

    public String getChildrenQuery(){
        return null;
    }

    public AMDatabaseModelAbstractObject getChildModelFromCursor(Cursor cursor){
        return null;
    }

    public String getSelectModelQuery(){

        return DBUtils.QUERY_SELECT_PAGE_FROM_KEY;
    }
    //endregion


    @Override
    public String toString() {
        return "PageModel{" +
                "pageNumber='" + pageNumber + '\'' +
                ", chapterNumber='" + chapterNumber + '\'' +
                ", text='" + text + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", languageAndChapter='" + languageAndChapter + '\'' +
                '}';
    }
}
