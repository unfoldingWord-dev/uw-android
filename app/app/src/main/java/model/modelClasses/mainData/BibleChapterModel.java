package model.modelClasses.mainData;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class BibleChapterModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface, Comparable<BibleChapterModel> {

    private static final String TAG = "LanguageModel";

    public String number;
    public String text;

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

    public BibleChapterModel() {
        super();
    }

    public BibleChapterModel(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {
        super(jsonObject, parent);
    }

    public BibleChapterDataSource getDataSource(Context context) {
        return new BibleChapterDataSource(context);
    }

    public void initModelFromJsonObject(JSONObject jsonObj) {

        return;
    }

    @Override
    public void initModelFromJsonObject(JSONObject jsonObject, AMDatabaseModelAbstractObject parent) {

        this.initModelFromJsonObject(jsonObject);
        this.parentId = parent.uid;
        this.slug = parent.slug + this.number;
        this.text = "";
    }

    public ArrayList<String> getAvailableLanguages(Context context) {

        ArrayList<String> languages = this.getParent(context).getAvailableLanguages(context);
        return languages;
    }


    @Override
    public String getTitle() {
        return "Chapter " + this.number;
    }

    public String getTitle(Context context) {
        return this.getParent(context).getParent(context).getTitle() + " " + this.number;
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
}