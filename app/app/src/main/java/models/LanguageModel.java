package models;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parser.JsonParser;
import utils.DBUtils;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class LanguageModel {

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
    public ArrayList<BookModel> books = new ArrayList<BookModel>();

    public static LanguageModel getLanguageModelFromJsonObject(JSONObject jsonObj){

        LanguageModel model = new LanguageModel();

        try {
            long date = -1;

            if (jsonObj.has(DATE_MODIFIED)) {
                String dateString = jsonObj.getString(DATE_MODIFIED);
                date = JsonParser.getSecondsFromDateString(dateString);
            }

            model.dateModified = date > 0 ? date : -1;
            model.direction = jsonObj.has(DIRECTION) ? jsonObj.getString(DIRECTION) : "";
            model.language = jsonObj.has(LANGUAGE) ? jsonObj.getString(LANGUAGE) : "";
            model.languageName = jsonObj.has(LANGUAGE_NAME) ? jsonObj.getString(LANGUAGE_NAME) : "";


            JSONObject statusJsonObject = jsonObj.getJSONObject(STATUS);
            StatusModel statMod = StatusModel.getStatusModelFromJsonObject(statusJsonObject, model);

            if(statMod != null){
                model.status = statMod;
            }
        }
        catch (JSONException e){
            Log.e(TAG, "LanguageModel JSON Exception: " + e.toString());
            return null;
        }

        return model;
    }

    public ContentValues getModelAsContentValues(){

        ContentValues values = status.getModelAsContentValues();

        values.put(DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG, this.dateModified);
        values.put(DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG, this.direction);
        values.put(DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG, this.language);
        values.put(DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG, this.languageName);

        return values;
    }

    public void addBooksFromJson(String json) throws JSONException{

        JSONObject jsonObj = new JSONObject(json);

        books.add(BookModel.getBookModelFromJsonObject(jsonObj, this));
    }

    public Map<String,  PageModel> getAllPagesAsDictionary(){

        Map<String,  PageModel> chapterMap = new HashMap<String, PageModel>();

        for(BookModel book : books){

            chapterMap.putAll(book.getAllPagesAsDictionary());
        }

        if(chapterMap.size() > 0){
            return chapterMap;
        }
        else{
            return null;
        }
    }


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
