package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import model.datasource.PageDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/9/15.
 */
public class PageModel extends AMDatabaseModelAbstractObject {

    private class PageJsonModel {

        String id;
        String img;
        String text;
    }

    private static final String TAG = "PageModel";

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

    public PageModel(JSONObject jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public PageModel(JSONObject jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }


    public PageDataSource getDataSource(Context context) {
        return new PageDataSource(context);
    }

    @Override
    public void initModelFromJson(JSONObject json, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
            return;
        }

        PageJsonModel model = new Gson().fromJson(json.toString(), PageJsonModel.class);

        this.imageUrl = model.img;
        this.text = model.text;

        String idString = model.id;
        if (idString.length() > 1) {
            String[] splitString = idString.split("-");
            this.chapterNumber = splitString[0];
            this.pageNumber = splitString[1];
        }
        else{
            Log.e(TAG, "Error splitting PageModel id: " + idString);
        }

        this.uid = -1;
    }

    @Override
    public void initModelFromJson(JSONObject json, long parentId, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
        }
        else {
            this.initModelFromJson(json, sideLoaded);
            this.slug = chapterNumber + pageNumber + "parent" + parentId;
        }

        this.parentId = parentId;
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

    protected class PageSideLoadedModel {

        String page_number;
        String chapter_number;
        String img;
        String text;

        public PageSideLoadedModel(PageModel page) {
            this.page_number = page.pageNumber;
            this.chapter_number = page.chapterNumber;
            this.img = page.imageUrl;
            this.text = page.text;
        }
    }

    protected PageSideLoadedModel getAsSideLoadedModel(){

        return new PageSideLoadedModel(this);
    }

    public void initModelFromSideLoadedJson(JSONObject json){

        PageSideLoadedModel model = new Gson().fromJson(json.toString(), PageSideLoadedModel.class);

        this.pageNumber = model.page_number;
        this.chapterNumber = model.chapter_number;
        this.imageUrl = model.img;
        this.text = model.text;
    }
}








