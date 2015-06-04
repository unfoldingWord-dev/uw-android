package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class VersionDataSource extends AMDatabaseDataSourceAbstract {

    static final String TABLE_VERSION = "_table_version";

    //Table columns of TABLE_VERSION
    static final String TABLE_VERSION_COLUMN_UID = "_column_version_uid";
    static final String TABLE_VERSION_COLUMN_PARENT_ID = "_column_parent_id";
    static final String TABLE_VERSION_COLUMN_DATE_MODIFIED = "_column_date_modified";
    static final String TABLE_VERSION_COLUMN_NAME = "_column_name";
    static final String TABLE_VERSION_COLUMN_SLUG = "_column_slug";
    static final String TABLE_VERSION_COLUMN_SOURCE_IS_DOWNLOADED = "_column_is_downloaded";

    static final String TABLE_VERSION_COLUMN_CHECKING_ENTITY = "_column_checking_entity";
    static final String TABLE_VERSION_COLUMN_CHECKING_LEVEL = "_column_checking_level";
    static final String TABLE_VERSION_COLUMN_COMMENTS = "_column_comments";
    static final String TABLE_VERSION_COLUMN_CONTRIBUTORS = "_column_contributors";
    static final String TABLE_VERSION_COLUMN_PUBLISH_DATE = "_column_publish_date";
    static final String TABLE_VERSION_COLUMN_SOURCE_TEXT = "_column_source_text";
    static final String TABLE_VERSION_COLUMN_SOURCE_TEXT_VERSION = "_column_source_text_version";
    static final String TABLE_VERSION_COLUMN_VERSION = "_column_version";

    static final String TABLE_VERSION_COLUMN_VERIFICATION_TEXT = "_column_verification_text";
    static final String TABLE_VERSION_COLUMN_VERIFICATION_STATUS = "_column_verification_status";

    public VersionDataSource(Context context) {
        super(context);
    }

    public ArrayList<BookModel> getChildModels(VersionModel parentModel) {

        ArrayList<BookModel> modelList = new ArrayList<BookModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = this.loadChildrenModelsFromDatabase(parentModel);

        for(AMDatabaseModelAbstractObject mod : models){
            BookModel model = (BookModel) mod;
            model.setParent(parentModel);
            modelList.add( model);
        }
        return modelList;
    }

    @Override
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentId, boolean sideLoaded) {
        VersionModel newModel = new VersionModel(json, parentId, sideLoaded);
        VersionModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
            newModel.downloadState = currentModel.downloadState;
        }

        if (currentModel == null || (currentModel.dateModified < newModel.dateModified)) {

            newModel.parentId = parentId;
            saveModel(newModel);
            return getModelForSlug(newModel.slug);
        }
        else{
            return null;
        }
    }

    @Override
    protected String getParentIdColumnName() {
        return TABLE_VERSION_COLUMN_PARENT_ID;
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_VERSION_COLUMN_SLUG;
    }

    @Override
    public String getTableName() {
        return TABLE_VERSION;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_VERSION_COLUMN_UID;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return new BookDataSource(this.context);
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new LanguageDataSource(this.context);
    }


    @Override
    public VersionModel getModel(String uid) {
        return (VersionModel) this.getModelForKey(uid);
    }

    @Override
    public VersionModel getModelForSlug(String slug){
        return (VersionModel) this.getModelFromDatabaseForSlug(slug);
    }

    public VersionModel deleteDownloadedBookContent(VersionModel version){

        ArrayList<BookModel> versionBooks = version.getChildModels(context);

        BookDataSource bookSource = new BookDataSource(context);
        for(BookModel book : versionBooks){
            bookSource.deleteDownloadedBookContent(book);
        }
        version.downloadState = VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_NONE;
        version.resetBooks();
        version.verificationStatus = -1;
        version.getVerificationStatus(context);
        createOrUpdateDatabaseModel(version);
        return version;
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model) {

        VersionModel versionModel = (VersionModel) model;
        ContentValues values = new ContentValues();

        if(versionModel.uid > 0) {
            values.put(TABLE_VERSION_COLUMN_UID, versionModel.uid);
        }
        values.put(TABLE_VERSION_COLUMN_PARENT_ID, versionModel.parentId);
        values.put(TABLE_VERSION_COLUMN_DATE_MODIFIED, versionModel.dateModified);
        values.put(TABLE_VERSION_COLUMN_NAME, versionModel.name);
        values.put(TABLE_VERSION_COLUMN_SLUG, versionModel.slug);
        values.put(TABLE_VERSION_COLUMN_SOURCE_IS_DOWNLOADED, versionModel.downloadState.ordinal());

        values.put(TABLE_VERSION_COLUMN_CHECKING_ENTITY, versionModel.status.checkingEntity);
        values.put(TABLE_VERSION_COLUMN_CHECKING_LEVEL, versionModel.status.checkingLevel);
        values.put(TABLE_VERSION_COLUMN_COMMENTS, versionModel.status.comments);
        values.put(TABLE_VERSION_COLUMN_CONTRIBUTORS, versionModel.status.contributors);
        values.put(TABLE_VERSION_COLUMN_PUBLISH_DATE, versionModel.status.publishDate);
        values.put(TABLE_VERSION_COLUMN_SOURCE_TEXT, versionModel.status.sourceText);
        values.put(TABLE_VERSION_COLUMN_SOURCE_TEXT_VERSION, versionModel.status.sourceTextVersion);
        values.put(TABLE_VERSION_COLUMN_VERSION, versionModel.status.version);
        values.put(TABLE_VERSION_COLUMN_VERIFICATION_STATUS, versionModel.verificationStatus);
        values.put(TABLE_VERSION_COLUMN_VERIFICATION_TEXT, versionModel.verificationText);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        VersionModel model = new VersionModel();

        model.uid =  cursor.getLong(cursor.getColumnIndex(TABLE_VERSION_COLUMN_UID));
        model.parentId =  cursor.getLong(cursor.getColumnIndex(TABLE_VERSION_COLUMN_PARENT_ID));
        model.dateModified = cursor.getLong(cursor.getColumnIndex(TABLE_VERSION_COLUMN_DATE_MODIFIED));
        model.name = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_NAME));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_SLUG));
        model.downloadState = VersionModel.DOWNLOAD_STATE.createState(
                cursor.getInt(cursor.getColumnIndex(TABLE_VERSION_COLUMN_SOURCE_IS_DOWNLOADED)));

        model.status.checkingEntity = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_CHECKING_ENTITY));
        model.status.checkingLevel = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_CHECKING_LEVEL));
        model.status.comments = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_COMMENTS));
        model.status.contributors = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_CONTRIBUTORS));
        model.status.publishDate = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_PUBLISH_DATE));
        model.status.sourceText = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_SOURCE_TEXT));
        model.status.sourceTextVersion = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_SOURCE_TEXT_VERSION));
        model.status.version = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_VERSION));

        model.verificationStatus = cursor.getInt(cursor.getColumnIndex(TABLE_VERSION_COLUMN_VERIFICATION_STATUS));
        model.verificationText = cursor.getString(cursor.getColumnIndex(TABLE_VERSION_COLUMN_VERIFICATION_TEXT));

        return model;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                VersionDataSource.TABLE_VERSION_COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VersionDataSource.TABLE_VERSION_COLUMN_PARENT_ID + " INTEGER," +
                VersionDataSource.TABLE_VERSION_COLUMN_DATE_MODIFIED + " INTEGER," +
                VersionDataSource.TABLE_VERSION_COLUMN_VERIFICATION_STATUS + " INTEGER," +
                VersionDataSource.TABLE_VERSION_COLUMN_NAME + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_SLUG + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_SOURCE_IS_DOWNLOADED + " VARCHAR," +

                VersionDataSource.TABLE_VERSION_COLUMN_CHECKING_ENTITY + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_CHECKING_LEVEL + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_COMMENTS + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_CONTRIBUTORS + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_PUBLISH_DATE + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_SOURCE_TEXT + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_SOURCE_TEXT_VERSION + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_VERIFICATION_TEXT + " VARCHAR," +
                VersionDataSource.TABLE_VERSION_COLUMN_VERSION + " VARCHAR)";

        return creationString;
    }
}
