package models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parser.JsonParser;

/**
 * Created by Fechner on 1/9/15.
 */
public class BookModel {

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
    public ArrayList<ChapterModel> chapters = new ArrayList<ChapterModel>();
    public LanguageModel parentLanguage;

    public BookModel(){
        super();
        appWords = new AppWordsModel(this);
    }
    public BookModel(LanguageModel parent){
        this();
        this.parentLanguage = parent;
    }

    public ChapterModel getNextChapter(ChapterModel chapter){

        int index = chapters.indexOf(chapter);

        ChapterModel nextChapter = chapters.get(index + 1);
        return nextChapter;
    }

    public Map<String,  PageModel> getAllPagesAsDictionary(){

        Map<String,  PageModel> chapterMap = new HashMap<String, PageModel>();

        for(ChapterModel chapter : chapters){
            chapterMap.putAll(chapter.getAllPagesAsDictionary());
        }

        if(chapterMap.size() > 0){
            return chapterMap;
        }
        else{
            return null;
        }
    }


    public static BookModel getBookModelFromJsonObject(JSONObject jsonObj, LanguageModel parent){

        BookModel model = new BookModel(parent);

        try {
            long date = -1;

            if (jsonObj.has(DATE_MODIFIED)) {
                String dateString = jsonObj.getString(DATE_MODIFIED);
                date = JsonParser.getSecondsFromDateString(dateString);
            }

            model.dateModified = date > 0 ? date : -1;
            model.direction = jsonObj.has(DIRECTION) ? jsonObj.getString(DIRECTION) : "";
            model.language = jsonObj.has(LANGUAGE) ? jsonObj.getString(LANGUAGE) : "";

            JSONObject wordsModelObj = jsonObj.getJSONObject(APP_WORDS);
            AppWordsModel wordsModel = AppWordsModel.getAppWordsModelFromJsonObject(wordsModelObj, model);
            if(wordsModel != null){
                model.appWords = wordsModel;
            }
            JSONArray chaptersJson =  jsonObj.getJSONArray(CHAPTERS);

            for(int i = 0; i < chaptersJson.length(); i++){
                JSONObject chapJson = chaptersJson.getJSONObject(i);

                ChapterModel chapter = ChapterModel.getChapterModelFromJsonObject(chapJson, model);
                if(chapter != null){
                    model.chapters.add(chapter);
                }
            }
        }
        catch (JSONException e){
            Log.e(TAG, "BookModel JSON Exception: " + e.toString());
            e.printStackTrace();
            return null;
        }
        return model;
    }
}
