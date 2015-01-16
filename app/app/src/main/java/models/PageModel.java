package models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import utils.NetWorkUtil;
import utils.URLUtils;

/**
 * Created by Fechner on 1/9/15.
 */
public class PageModel implements Serializable{

    private static final String TAG = "PageModel";

    private static final String ID = "id";
    private static final String IMAGE_URL = "img";
    private static final String TEXT = "text";

    public String id = "";
    public String imageUrl = "";
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

    public String text = "";

    public ChapterModel parentChapter;

    public PageModel() {
        super();
    }

    public PageModel(ChapterModel parentChapter) {
        this();
        this.parentChapter = parentChapter;
    }

    public static PageModel getPageModelFromJsonObject(JSONObject jObject, ChapterModel parent){

        PageModel model = new PageModel(parent);

        try{
            model.id = jObject.has(ID) ? jObject.getString(ID) : "";
            model.imageUrl = jObject.has(IMAGE_URL) ? jObject.getString(IMAGE_URL) : "";
            model.text = jObject.has(TEXT) ? jObject.getString(TEXT) : "";
        }
        catch (JSONException e){
            Log.e(TAG, "PageModel JSON Exception: " + e.toString());
            return null;
        }

        return model;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", text='" + text + '\'' +
                ", parentChapter=" + parentChapter +
                '}';
    }
}
