package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.StoriesChapterDataSource;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class StoriesChapterModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface, Comparable<StoriesChapterModel> {

    private static final String TAG = "LanguageModel";

    private static final String NUMBER = "number";
    private static final String TITLE = "title";
    private static final String REF = "ref";
//    private static final String PAGES = "frames";

    public String number;
    public String description;
    public String title;

    private BookModel parent = null;
    public BookModel getParent(Context context){

        if(parent == null){
            parent = (BookModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }

        return parent;
    }
    public void setParent(BookModel parent){
        this.parent = parent;
    }

    private ArrayList<PageModel> pages = null;
    public ArrayList<PageModel> getChildModels(Context context){

        if(pages == null){
            pages = this.getDataSource(context).getChildModels(this);
        }

        return pages;
    }

    public StoriesChapterModel() {
        super();
    }

    public StoriesChapterModel(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        super(jsonObject, parent);
    }

    public StoriesChapterDataSource getDataSource(Context context) {
        return new StoriesChapterDataSource(context);
    }

    public void AddBlankPageToEnd(){
        PageModel pageModel = pages.get(0);
        pages.add(pageModel);
    }

    public void initModelFromJsonObject(JSONObject jsonObj) {

        try {
            this.number = jsonObj.has(NUMBER) ? jsonObj.getString(NUMBER) : "";
            this.description = jsonObj.has(REF) ? jsonObj.getString(REF) : "";
            this.title = jsonObj.has(TITLE) ? jsonObj.getString(TITLE) : "";

        } catch (JSONException e) {
            Log.e(TAG, "ChapterModel JSON Exception: " + e.toString());
        }
    }

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {

        this.initModelFromJsonObject(jsonObject);
        this.parentId = parent.uid;
        this.slug = parent.slug + this.number;
    }

    public ArrayList<String> getAvailableLanguages(Context context) {

        ArrayList<String> languages = this.getParent(context).getParent(context).getAvailableLanguages(context);
        return languages;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(uid);
    }


    @Override
    public int compareTo(StoriesChapterModel another) {

        int thisChapterNumber = Integer.parseInt(this.number);
        int otherChapterNumber = Integer.parseInt(another.number);

        return thisChapterNumber - otherChapterNumber;
    }

    @Override
    public String toString() {
        return "ChapterModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", number='" + number + '\'' +
                "} " + super.toString();
    }
}