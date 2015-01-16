package models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import utils.JsonUtils;

/**
 * Created by Fechner on 1/9/15.
 */
public class AppWordsModel {

    private static final String TAG = "AppWordsModel";

    private static final String CANCEL = "cancel";
    private static final String CHAPTERS = "chapters";
    private static final String LANGUAGES = "languages";
    private static final String NEXT_CHAPTER = "next_chapter";
    private static final String OK = "ok";
    private static final String REMOVE_LOCALLY = "remove_locally";
    private static final String REMOVE_THIS_STRING = "remove_this_string";
    private static final String SAVE_LOCALLY = "save_locally";
    private static final String SAVE_THIS_STRING = "save_this_string";
    private static final  String SELECT_A_LANGUAGE = "select_a_language";
    
    public String cancel = "";
    public String chapters = "";
    public String languages = "";
    public String next_chapter = "";
    public String ok = "";
    public String remove_locally = "";
    public String remove_this_string = "";
    public String save_locally = "";
    public String save_this_string = "";
    public String select_a_language = "";
    
    BookModel bookParent;

    public AppWordsModel(){
        
    }
    public AppWordsModel(BookModel parent){
        this.bookParent = parent;
    }
            

    public static AppWordsModel getAppWordsModelFromJsonObject(JSONObject jsonObj, BookModel parent){
        
        AppWordsModel model = new AppWordsModel(parent);
            
        try{
            model.cancel = jsonObj.has(CANCEL) ? jsonObj.getString(CANCEL) : "";
            model.chapters = jsonObj.has(CHAPTERS) ? jsonObj.getString(CHAPTERS) : "";
            model.languages = jsonObj.has(LANGUAGES) ? jsonObj.getString(LANGUAGES) : "";
            model.next_chapter = jsonObj.has(NEXT_CHAPTER) ? jsonObj.getString(NEXT_CHAPTER) : "";
            model.remove_locally = jsonObj.has(REMOVE_LOCALLY) ? jsonObj.getString(REMOVE_LOCALLY) : "";
            model.remove_this_string = jsonObj.has(REMOVE_THIS_STRING) ? jsonObj.getString(REMOVE_THIS_STRING) : "";
            model.save_locally = jsonObj.has(SAVE_LOCALLY) ? jsonObj.getString(SAVE_LOCALLY) : "";
            model.save_this_string = jsonObj.has(SAVE_THIS_STRING) ? jsonObj.getString(SAVE_THIS_STRING) : "";
            model.select_a_language = jsonObj.has(SELECT_A_LANGUAGE) ? jsonObj.getString(SELECT_A_LANGUAGE) : "";
            model.ok = jsonObj.has(OK)? jsonObj.getString(OK) : "";
        }
        catch (JSONException e){
            Log.e(TAG, "AppWordsModel JSON Exception: " + e.toString());
            return null;
        }
        
        return model;
    }
    
}
