package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.datasource.BookDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import signing.Status;

/**
 * Created by Fechner on 1/9/15.
 */
public class BookModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface {

    private class BookJsonModel {

        long mod;
        String title;
        String slug;
        String src;
        String src_sig;
        String desc;
    }

    private class SignatureJsonModel {

        String si;
        String sig;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }

    public long dateModified;
    public String title;
    public String description;
    public String sourceUrl;
    public String signatureUrl;

    public String signingEntity;
    public String signature;

    public int verificationStatus;

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

    private ArrayList<StoriesChapterModel> storyChapters = null;
    public ArrayList<StoriesChapterModel> getStoryChildModels(Context context){

        if(storyChapters == null){
            storyChapters = this.getDataSource(context).getChildModels(this);
            Collections.sort(storyChapters);
        }
        return storyChapters;
    }

    public StoriesChapterModel getStoryChapter(Context context, int chapterNumber){

        this.getStoryChildModels(context);
        Collections.sort(this.storyChapters);

        if(chapterNumber >= this.storyChapters.size()){
            return null;
        }
        else{
            return this.storyChapters.get(chapterNumber -1);
        }
    }

    private ArrayList<BibleChapterModel> bibleChapterModels = null;
    public ArrayList<BibleChapterModel> getBibleChildModels(Context context){

        if(bibleChapterModels == null){
            bibleChapterModels = new BibleChapterDataSource(context).getChaptersForParentId(Long.toString(this.uid));
            Collections.sort(bibleChapterModels);
        }

        return bibleChapterModels;
    }

    public BibleChapterModel getBibleChapter(Context context, int chapterNumber){

        this.getBibleChildModels(context);
        Collections.sort(this.bibleChapterModels);

        if(chapterNumber >= this.bibleChapterModels.size()){
            return null;
        }
        else{
            for(BibleChapterModel chapter : bibleChapterModels) {
                if(Integer.parseInt(chapter.number.trim()) == chapterNumber){
                    return chapter;
                }
            }
        }
        return null;
    }

    public BookModel(){
        super();
    }

    public BookModel(String jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public BookModel(String jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }

    @Override
    public BookDataSource getDataSource(Context context) {
        return new BookDataSource(context);
    }

    @Override
    public void initModelFromJson(String json, boolean sideLoaded){

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
            return;
        }

        BookJsonModel model = new Gson().fromJson(json, BookJsonModel.class);

        this.dateModified = model.mod;
        this.title = model.title;
        this.slug = model.slug + model.title;
        this.sourceUrl = model.src;
        this.signatureUrl = model.src_sig;
        this.description = model.desc;
        this.uid = -1;
    }

    @Override
    public void initModelFromJson(String json, long parentId, boolean sideLoaded) {

        if(sideLoaded){
            initModelFromSideLoadedJson(json);
        }
        else {
            this.initModelFromJson(json, sideLoaded);
            if(slug.length() < 3){
                slug = sourceUrl;
            }
            else{
                slug += sourceUrl;
            }
        }
        this.parentId = parentId;
        this.verificationStatus = Status.ERROR.ordinal();
    }

    public void setEntityFromJson(String json){

        SignatureJsonModel model = new Gson().fromJson(json, SignatureJsonModel.class);

        signingEntity = model.si;
        signature = model.sig;
    }


    @Override
    public String toString() {
        return "BookModel{" +
                "dateModified=" + dateModified +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", signatureUrl='" + signatureUrl + '\'' +
                ", parent=" + parent +
                "} " + super.toString();
    }

    protected class BookSideLoadedModel {

        long date_modified;
        String title;
        String slug;
        String description;
        String source_url;
        String signature_url;

        String signing_entity;
        String signature;
        int verificationStatus;

        StoriesChapterModel.StoriesChapterSideLoadedModel[] storiesChapters;
        BibleChapterModel.BibleChapterSideLoadedModel[] bibleChapters;

        public BookSideLoadedModel(BookModel book, Context context) {
            this.date_modified = book.dateModified;
            this.title = book.title;
            this.slug = book.slug;
            this.description = book.description;
            this.source_url = book.sourceUrl;
            this.signature_url = book.signatureUrl;
            this.signing_entity = book.signingEntity;
            this.signature = book.signature;
            this.verificationStatus = book.verificationStatus;

            if(book.sourceUrl.contains("usfm")){

                ArrayList<BibleChapterModel> chapters = book.getBibleChildModels(context);
                bibleChapters = new BibleChapterModel.BibleChapterSideLoadedModel[chapters.size()];

                for(int i = 0; i < chapters.size(); i++){
                    bibleChapters[i] = chapters.get(i).getAsSideLoadedModel();
                }
                storiesChapters = new StoriesChapterModel.StoriesChapterSideLoadedModel[0];
            }
            else{
                ArrayList<StoriesChapterModel> chapters = book.getStoryChildModels(context);
                storiesChapters = new StoriesChapterModel.StoriesChapterSideLoadedModel[chapters.size()];

                for(int i = 0; i < chapters.size(); i++){
                    storiesChapters[i] = chapters.get(i).getAsSideLoadedModel(context);
                }
                bibleChapters = new BibleChapterModel.BibleChapterSideLoadedModel[0];
            }
        }
    }

    protected BookSideLoadedModel getAsSideLoadedModel(Context context){

        return new BookSideLoadedModel(this, context);
    }

    public void initModelFromSideLoadedJson(String json){

        BookSideLoadedModel model = new Gson().fromJson(json, BookSideLoadedModel.class);

        this.dateModified = model.date_modified;
        this.title = model.title;
        this.slug = model.slug;
        this.sourceUrl = model.source_url;
        this.signatureUrl = model.signature_url;
        this.signingEntity = model.signing_entity;
        this.signature = model.signature;
        this.verificationStatus = model.verificationStatus;
        this.description = model.description;

        this.uid = -1;
    }
}
