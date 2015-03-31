package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.StoriesChapterDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class StoriesChapterModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface, Comparable<StoriesChapterModel> {

    private class StoriesChapterJsonModel {

        String number;
        String title;
        String ref;
    }

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

    public StoriesChapterModel(String jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public StoriesChapterModel(String jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }

    public StoriesChapterDataSource getDataSource(Context context) {
        return new StoriesChapterDataSource(context);
    }

    @Override
    public void initModelFromJson(String json, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
            return;
        }
        StoriesChapterJsonModel model = new Gson().fromJson(json, StoriesChapterJsonModel.class);

        number = model.number;
        description = model.ref;
        title = model.title;

        uid = -1;
    }

    @Override
    public void initModelFromJson(String json, long parentId, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
        }
        else {
            this.initModelFromJson(json, sideLoaded);
            this.slug = this.title + "parent" + parentId;
        }

        this.parentId = parentId;
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
        return "StoriesChapterModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", number='" + number + '\'' +
                "} " + super.toString();
    }

    protected class StoriesChapterSideLoadedModel {

        String number;
        String title;
        String description;

        PageModel.PageSideLoadedModel[] pages;


        public StoriesChapterSideLoadedModel(StoriesChapterModel chapter, Context context) {
            this.number = chapter.number;
            this.title = chapter.title;
            this.description = chapter.description;

            ArrayList<PageModel> pageList = chapter.getChildModels(context);
            pages = new PageModel.PageSideLoadedModel[pageList.size()];

            for(int i = 0; i < pageList.size(); i++){
                pages[i] = pageList.get(i).getAsSideLoadedModel();
            }

        }
    }

    protected StoriesChapterSideLoadedModel getAsSideLoadedModel(Context context){

        return new StoriesChapterSideLoadedModel(this, context);
    }

    public void initModelFromSideLoadedJson(String json){

        StoriesChapterSideLoadedModel model = new Gson().fromJson(json, StoriesChapterSideLoadedModel.class);

        number = model.number;
        description = model.description;
        title = model.title;

        uid = -1;
    }
}