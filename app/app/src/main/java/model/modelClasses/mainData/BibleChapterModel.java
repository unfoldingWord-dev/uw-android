package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class BibleChapterModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface, Comparable<BibleChapterModel> {

    private static final String TAG = "LanguageModel";

    public String number;
    public String text;

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

    public BibleChapterModel() {
        super();
    }

    public BibleChapterModel(String json, long parentId, boolean sideLoaded) {
        super(json, parentId, sideLoaded);
    }

    public BibleChapterDataSource getDataSource(Context context) {
        return new BibleChapterDataSource(context);
    }

    public void initModelFromJson(String json, boolean sideLoaded) {

        return;
    }

    @Override
    public void initModelFromJson(String json, long parentId, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
        }
        else{
            Log.e(TAG, "BibleChapterModel. This should't happen!");
        }
        this.parentId = parentId;
    }

    @Override
    public String getTitle() {
        return "Chapter " + this.number;
    }

    public String getTitle(Context context) {
        return this.getParent(context).getTitle() + " " + this.number;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(uid);
    }


    @Override
    public int compareTo(BibleChapterModel another) {
        int thisChapterNumber = Integer.parseInt(this.number.replaceAll("[^0-9]", ""));
        int otherChapterNumber = Integer.parseInt(another.number.replaceAll("[^0-9]", ""));

        return thisChapterNumber - otherChapterNumber;
    }

    @Override
    public String toString() {
        return "BibleChapterModel{" +
                "text='" + text + '\'' +
                ", number='" + number + '\'' +
                "} " + super.toString();
    }

    protected class BibleChapterSideLoadedModel {

        String number;
        String slug;
        String text;

        PageModel.PageSideLoadedModel[] pages;

        public BibleChapterSideLoadedModel(BibleChapterModel chapter) {
            this.number = chapter.number;
            this.text = chapter.text;
            this.slug = chapter.slug;
        }
    }

    protected BibleChapterSideLoadedModel getAsSideLoadedModel(){

        return new BibleChapterSideLoadedModel(this);
    }

    public void initModelFromSideLoadedJson(String json){

        BibleChapterSideLoadedModel model = new Gson().fromJson(json, BibleChapterSideLoadedModel.class);

        this.number = model.number;
        this.text = model.text;
        this.slug = model.slug;
        this.uid = -1;
    }

}