package models;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utils.DBUtils;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class ChapterModel extends LanguageModel implements Serializable {

    private static final String TAG = "LanguageModel";

    private static final String NUMBER = "number";
    private static final String TITLE = "title";
    private static final String REF = "ref";
    private static final String FRAMES = "frames";

    private String framesJson = "";

    public String getFramesJson() {
        return framesJson;
    }

    public void setFramesJson(String framesJson) {
        this.framesJson = framesJson;
        setPageModels();
    }

    public String number = "";
    public String reference = "";
    public String title = "";

    public BookModel parentBook;
    public ArrayList<PageModel> pageModels = new ArrayList<PageModel>();

    public ChapterModel(){

        super();
    }
    public ChapterModel(BookModel parent){
        this();
        this.parentBook = parent;
    }


    private void setPageModels(){

        pageModels = new ArrayList<PageModel>();


        try {

            JSONArray pageArray = new JSONArray(framesJson);

            for (int i = 0; i < pageArray.length(); i++) {

                JSONObject pageObj = pageArray.getJSONObject(i);

                PageModel page = PageModel.getPageModelFromJsonObject(pageObj, this);
                pageModels.add(page);
            }
        }
        catch (JSONException e){
            Log.e(TAG, "ChapterModel JSON Exception: " + e.toString());
        }

    }

    public ContentValues getModelAsContentValues(){
        ContentValues values = new ContentValues();
        values.put(DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO, number);
        values.put(DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO, reference);
        values.put(DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO, title);
        values.put(DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO, framesJson);
        values.put(DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO, parentBook.language);
        values.put(DBUtils.COLUMN_CANCEL_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.cancel);
        values.put(DBUtils.COLUMN_CHAPTERS_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.chapters);
        values.put(DBUtils.COLUMN_LANGUAGES_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.languages);
        values.put(DBUtils.COLUMN_NEXT_CHAPTER_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.next_chapter);
        values.put(DBUtils.COLUMN_OK_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.ok);
        values.put(DBUtils.COLUMN_REMOVE_LOCALLY_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.remove_locally);
        values.put(DBUtils.COLUMN_OK_REMOVE_THIS_STRING_TABLE_FRAME_INFO, parentBook.appWords.remove_this_string);
        values.put(DBUtils.COLUMN_SAVE_LOCALLY_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.save_locally);
        values.put(DBUtils.COLUMN_SAVE_THIS_STRING_LANGUAGE_TABLE_FRAME_INFO, parentBook.appWords.save_this_string);

        return values;
    }

    public Map<String,  PageModel> getAllPagesAsDictionary(){

        Map<String,  PageModel> chapterMap = new HashMap<String, PageModel>();

        for(PageModel page : pageModels){
            chapterMap.put(page.id, page);
        }

        if(chapterMap.size() > 0){
            return chapterMap;
        }
        else{
            return null;
        }
    }

    public static ChapterModel getChapterModelFromJsonObject(JSONObject jObject, BookModel parent){

        ChapterModel model = new ChapterModel(parent);

        try {
            model.number = jObject.has(NUMBER) ? jObject.getString(NUMBER) : "";
            model.reference = jObject.has(REF) ? jObject.getString(REF) : "";
            model.title = jObject.has(TITLE) ? jObject.getString(TITLE) : "";

            if(jObject.has(FRAMES)) {
                model.setFramesJson(jObject.getString(FRAMES));
            }


        }
        catch (JSONException e){
            Log.e(TAG, "ChapterModel JSON Exception: " + e.toString());
            return null;
        }

        return model;
 }


    @Override
    public String toString() {
        return "ChapterModel{" +
                "number='" + number + '\'' +
                ", reference='" + reference + '\'' +
                ", title='" + title + '\'' +
                ", framesJson='" + framesJson + '\'' +
                ", parentBook=" + parentBook +
                '}';
    }
}
