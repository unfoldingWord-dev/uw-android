package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.VersionDataSource;
import model.modelClasses.StatusModel;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import signing.Status;

/**
 * Created by Fechner on 1/22/15.
 */
public class VersionModel extends AMDatabaseModelAbstractObject implements GeneralRowInterface {

    private static final String TAG = "ResourceModel";

    public enum DOWNLOAD_STATE{
        DOWNLOAD_STATE_ERROR(0),
        DOWNLOAD_STATE_NONE(1),
        DOWNLOAD_STATE_DOWNLOADING(2),
        DOWNLOAD_STATE_DOWNLOADED(3);

         DOWNLOAD_STATE(int i) {
        }

        public static DOWNLOAD_STATE createState(int value) {

            switch (value) {
                case 1:{
                    return DOWNLOAD_STATE_NONE;
                }
                case 2:{
                    return DOWNLOAD_STATE_DOWNLOADING;
                }
                case 3:{
                    return DOWNLOAD_STATE_DOWNLOADED;
                }
                default:{
                    return DOWNLOAD_STATE_ERROR;
                }

            }
        }

    }

    private class VersionJsonModel {

        long mod;
        String name;
        String slug;
        StatusJsonModel status;
    }

    private class StatusJsonModel {

        String checking_entity;
        String checking_level;
        String comments;
        String contributors;
        String publish_date;
        String source_text;
        String source_text_version;
        String version;
    }

    private static final String DATE_MODIFIED_JSON_KEY = "date_modified";
    private static final String NAME_JSON_KEY = "name";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String STATUS_JSON_KEY = "status";

    public String name;
    public long dateModified;
    public StatusModel status;
    public DOWNLOAD_STATE downloadState;

    private LanguageModel parent;
    public LanguageModel getParent(Context context){

        if(parent == null){
            parent = (LanguageModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }
        return parent;
    }
    public void setParent(LanguageModel parent){
        this.parent = parent;
    }

    private ArrayList<BookModel> books = null;
    public ArrayList<BookModel> getChildModels(Context context){

        if(books == null){
            books = this.getDataSource(context).getChildModels(this);
        }

        if(books.size() == 0){
            books = null;
        }
        return books;
    }

    public VersionModel() {
        super();
        this.status = new StatusModel();
    }

    public VersionModel(String jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public VersionModel(String jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }

    public BookModel findBookForJsonSlug(Context context, String slug){

        for(BookModel model : this.getChildModels(context)){

            if(model.slug.substring(0, 3).equalsIgnoreCase(slug.substring(0, 3))){

                return model;
            }
        }
        return null;
    }
    
    @Override
    public VersionDataSource getDataSource(Context context) {
        return new VersionDataSource(context);
    }

    @Override
    public void initModelFromJson(String json, boolean preLoaded){

        VersionJsonModel model = new Gson().fromJson(json, VersionJsonModel.class);

        name = model.name;
        dateModified = model.mod;
        slug = model.slug;

        status = new StatusModel();
        status.checkingEntity = model.status.checking_entity;
        status.checkingLevel = model.status.checking_level;
        status.comments = model.status.comments;
        status.contributors = model.status.contributors;
        status.publishDate = model.status.publish_date;
        status.sourceText = model.status.source_text;
        status.sourceTextVersion = model.status.source_text_version;
        status.version = model.status.version;
        uid = -1;
    }

    @Override
    public void initModelFromJson(String json, long parentId, boolean sideLoaded) {
        if(sideLoaded){
            this.initModelFromSideLoadedJson(json);
        }
        else {
            this.initModelFromJson(json, sideLoaded);
//            this.slug += ((LanguageModel) parent).slug;
        }

        this.parentId = parentId;
        downloadState = DOWNLOAD_STATE.DOWNLOAD_STATE_NONE;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getChildIdentifier() {
        return Long.toString(this.uid);
    }

    public VersionModel save(Context context){
        this.getDataSource(context).createOrUpdateDatabaseModel(this);
        return this.getDataSource(context).getModel(Long.toString(this.uid));
    }

    public int getVerificationStatus(Context context){

        int verifyStatus = 0;

        if(this.getChildModels(context) == null){
            return -1;
        }

        for(BookModel book : this.getChildModels(context)){
            switch (book.verificationStatus){
                case 0:{
                    break;
                }
                case 1:{
                    if(verifyStatus < 1){
                        verifyStatus = 1;
                    }
                    break;
                }
                case 3:{
                    if(verifyStatus < 3){
                        verifyStatus = 3;
                    }
                    break;
                }
                default:{
                    verifyStatus = 2;
                    break;
                }
            }
        }
        return  verifyStatus;
    }

    public String toString() {
        return "VersionModel{" +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", dateModified=" + dateModified +
                ", status=" + status.toString() +
                ", parent=" + parent +
                ", books=" + books +
                status.toString() +
                "} " + super.toString();
    }

    protected class VersionSideLoadedModel {

        long date_modified;
        String name;
        String slug;

        String checking_entity;
        String checking_level;
        String comments;
        String contributors;
        String publish_date;
        String source_text;
        String source_text_version;
        String version;

        BookModel.BookSideLoadedModel[] books;

        private VersionSideLoadedModel(VersionModel model, Context context) {

            this.date_modified = model.dateModified;
            this.name = model.name;
            this.slug = model.slug;
            this.checking_entity = model.status.checkingEntity;
            this.checking_level = model.status.checkingLevel;
            this.comments = model.status.comments;
            this.contributors = model.status.contributors;
            this.publish_date = model.status.publishDate;
            this.source_text = model.status.sourceText;
            this.source_text_version = model.status.sourceTextVersion;
            this.version = model.status.version;

            ArrayList<BookModel> bookModels = model.getChildModels(context);
            this.books = new BookModel.BookSideLoadedModel[bookModels.size()];

            for(int i = 0; i < bookModels.size(); i++){
                this.books[i] = bookModels.get(i).getAsSideLoadedModel(context);
            }
        }
    }


    protected VersionSideLoadedModel getAsSideLoadedModel(Context context){

        return new VersionSideLoadedModel(this, context);
    }

    public void initModelFromSideLoadedJson(String json){

        VersionSideLoadedModel model = new Gson().fromJson(json, VersionSideLoadedModel.class);

        name = model.name;
        dateModified = model.date_modified;
        slug = model.slug;

        status = new StatusModel();
        status.checkingEntity = model.checking_entity;
        status.checkingLevel = model.checking_level;
        status.comments = model.comments;
        status.contributors = model.contributors;
        status.publishDate = model.publish_date;
        status.sourceText = model.source_text;
        status.sourceTextVersion = model.source_text_version;
        status.version = model.version;
        uid = -1;
    }
}
