package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.datasource.BookDataSource;
import model.modelClasses.AppWordsModel;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/9/15.
 */
public class BookModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "BookModel";

    static final String APP_WORDS = "app_words";
    static final String CHAPTERS = "chapters";
    static final String DATE_MODIFIED = "date_modified";
    static final String DIRECTION = "direction";
    static final String LANGUAGE = "language";

    public long dateModified;
    public String language;
    public String direction;
    public AppWordsModel appWords;


    private VersionModel parent = null;
    public VersionModel getParent(Context context){

        if(parent == null){
            parent = (VersionModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }
        return parent;
    }
    public void setParent(VersionModel parent){
        this.parent = parent;
    }

    private ArrayList<StoriesChapterModel> chapters = null;
    public ArrayList<StoriesChapterModel> getChildModels(Context context){

        if(chapters == null){
            chapters = this.getDataSource(context).getChildModels(this);
        }
        return chapters;
    }

    public Map<Long, PageModel> getPages(Context context){

        ArrayList<PageModel> pages = new ArrayList<PageModel>();

        for(StoriesChapterModel chapter : this.getChildModels(context)){
            pages.addAll(chapter.getChildModels(context));
        }

        Map<Long, PageModel> pageMap = new HashMap<Long, PageModel>();

        for(PageModel page : pages){
            pageMap.put(page.uid, page);
        }

        return pageMap;
    }

    public BookModel(){
        super();
        this.appWords = new AppWordsModel();
    }

    public BookModel(JSONObject jsonObject) {
        super(jsonObject);
    }

    public BookModel(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        super(jsonObject, parent);
    }

    @Override
    public BookDataSource getDataSource(Context context) {
        return new BookDataSource(context);
    }

    public void initModelFromJsonObject(JSONObject jsonObj){

        try {
            long date = -1;

            if (jsonObj.has(DATE_MODIFIED)) {
                String dateString = jsonObj.getString(DATE_MODIFIED);
                date = Integer.parseInt(dateString);
            }

            this.dateModified = date > 0 ? date : -1;
            this.direction = jsonObj.has(DIRECTION) ? jsonObj.getString(DIRECTION) : "";
            this.language = jsonObj.has(LANGUAGE) ? jsonObj.getString(LANGUAGE) : "";

            JSONObject wordsModelObj = jsonObj.getJSONObject(APP_WORDS);
            AppWordsModel wordsModel = AppWordsModel.getAppWordsModelFromJsonObject(wordsModelObj, this);
            if(wordsModel != null){
                this.appWords = wordsModel;
            }
            else{
                this.appWords = new AppWordsModel();
            }

//            if (jsonObj.has(CHAPTERS)) {
//
//                chapters = new ArrayList<ChapterModel>();
//                JSONArray jsonArray = jsonObj.getJSONArray(CHAPTERS);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject pageJsonObj = jsonArray.getJSONObject(i);
//
//                    ChapterModel chapter = new ChapterModel();
//                    chapter.initModelFromJsonObject(pageJsonObj);
//                    chapters.add(chapter);
//                }
//            }
        }
        catch (JSONException e){
            Log.e(TAG, "BookModel JSON Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {

        this.initModelFromJsonObject(jsonObject);
        this.parentId = parent.uid;
        this.slug = parent.slug + this.language;
    }


    @Override
    public String toString() {
        return "BookModel{" +
                "appWords=" + appWords +
                ", dateModified=" + dateModified +
                ", language='" + language + '\'' +
                ", direction='" + direction + '\'' +
                "} " + super.toString();
    }
}
