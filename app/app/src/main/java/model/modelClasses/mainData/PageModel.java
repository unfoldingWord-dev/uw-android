package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import model.datasource.PageDataSource;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/9/15.
 */
public class PageModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "PageModel";

    private static final String ID = "id";
    private static final String IMAGE_URL = "img";
    private static final String TEXT = "text";

    public String pageNumber;
    public String chapterNumber;
    public String text;
    public String imageUrl;

    private StoriesChapterModel parent = null;
    public StoriesChapterModel getParent(Context context){

        if(parent == null){
            parent = (StoriesChapterModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }

        return parent;
    }
    public void setParent(StoriesChapterModel parent){
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

    public PageModel(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        super(jsonObject, parent);
    }


    public PageDataSource getDataSource(Context context) {
        return new PageDataSource(context);
    }

    public void initModelFromJsonObject(JSONObject jsonObj) {

        try {
            String idString = jsonObj.has(ID) ? jsonObj.getString(ID) : "";
            if (idString.length() > 1) {
                String[] splitString = idString.split("-");
                this.chapterNumber = splitString[0];
                this.pageNumber = splitString[1];
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

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {

        this.initModelFromJsonObject(jsonObject);
        this.parentId = parent.uid;
        this.slug = parent.slug + pageNumber;
    }

    @Override
    public String toString() {
        return "PageModel{" +
                "pageNumber='" + pageNumber + '\'' +
                ", chapterNumber='" + chapterNumber + '\'' +
                ", text='" + text + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", parent=" + parent +
                "} " + super.toString();
    }
}
