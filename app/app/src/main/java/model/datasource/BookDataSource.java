package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.StoriesChapterModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class BookDataSource extends AMDatabaseDataSourceAbstract {

    static final String TABLE_BOOK = "_table_book";

    // Table columns of TABLE_BOOK
    static final String TABLE_BOOK_COLUMN_UID = "_column_book_uid";
    static final String TABLE_BOOK_COLUMN_PARENT_ID = "_column_parent_id";
    static final String TABLE_BOOK_COLUMN_DATE_MODIFIED = "_column_date_modified";
    static final String TABLE_BOOK_COLUMN_TITLE= "_column_title";
    static final String TABLE_BOOK_COLUMN_DESCRIPTION = "_column_description";
    static final String TABLE_BOOK_COLUMN_SLUG = "_column_slug";
    static final String TABLE_BOOK_COLUMN_SOURCE_URL = "_column_source_url";
    static final String TABLE_BOOK_COLUMN_SIGNATURE_URL = "_column_signature_url";


    public BookDataSource(Context context) {
        super(context);
    }

    public ArrayList<StoriesChapterModel> getChildModels(BookModel parentModel) {

        ArrayList<StoriesChapterModel> modelList = new ArrayList<StoriesChapterModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = this.loadChildrenModelsFromDatabase(parentModel);

        for(AMDatabaseModelAbstractObject mod : models){
            StoriesChapterModel model = (StoriesChapterModel) mod;
            model.setParent(parentModel);
            modelList.add( model);
        }
        return modelList;
    }

    @Override
    public AMDatabaseModelAbstractObject saveOrUpdateModel(JSONObject json, long parentId, boolean sideLoaded)  {
        BookModel newModel = new BookModel(json, parentId, sideLoaded);
        BookModel currentModel = getModelForSlug(newModel.slug);

        if(currentModel != null) {
            newModel.uid = currentModel.uid;
        }

        if (currentModel == null || (currentModel.dateModified < newModel.dateModified)) {
            saveModel(newModel);
            return getModelForSlug(newModel.slug);
        }
        else{
            return null;
        }
    }

    @Override
    protected String getParentIdColumnName() {
        return TABLE_BOOK_COLUMN_PARENT_ID;
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_BOOK_COLUMN_SLUG;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return new StoriesChapterDataSource(this.context);
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new VersionDataSource(this.context);
    }

    @Override
    public String getTableName() {
        return TABLE_BOOK;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_BOOK_COLUMN_UID;
    }

    @Override
    public BookModel getModelForSlug(String slug){

        return (BookModel) this.getModelFromDatabaseForSlug(slug);
    }

    public void deleteDownloadedBookContent(BookModel book){

        AMDatabaseDataSourceAbstract dataSource = (book.sourceUrl.contains("usfm"))?
                new BibleChapterDataSource(context) :  new StoriesChapterDataSource(context);

        dataSource.deleteChildrenOfParent(book);
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model) {

        BookModel versionModel = (BookModel) model;
        ContentValues values = new ContentValues();

        if(versionModel.uid > 0) {
            values.put(TABLE_BOOK_COLUMN_UID, versionModel.uid);
        }

        values.put(TABLE_BOOK_COLUMN_PARENT_ID, versionModel.parentId);
        values.put(TABLE_BOOK_COLUMN_DATE_MODIFIED, versionModel.dateModified);
        values.put(TABLE_BOOK_COLUMN_TITLE, versionModel.title);
        values.put(TABLE_BOOK_COLUMN_DESCRIPTION, versionModel.description);
        values.put(TABLE_BOOK_COLUMN_SLUG, versionModel.slug);

        values.put(TABLE_BOOK_COLUMN_SOURCE_URL, versionModel.sourceUrl);
        values.put(TABLE_BOOK_COLUMN_SIGNATURE_URL, versionModel.signatureUrl);
        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        BookModel model = new BookModel();

        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_BOOK_COLUMN_UID));
        model.parentId = cursor.getLong(cursor.getColumnIndex(TABLE_BOOK_COLUMN_PARENT_ID));
        model.dateModified =  cursor.getLong(cursor.getColumnIndex(TABLE_BOOK_COLUMN_DATE_MODIFIED));
        model.title = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_TITLE));
        model.description = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_DESCRIPTION));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_SLUG));

        model.sourceUrl = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_SOURCE_URL));
        model.signatureUrl = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_SIGNATURE_URL));

        return model;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                BookDataSource.TABLE_BOOK_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +

                BookDataSource.TABLE_BOOK_COLUMN_PARENT_ID + " INTEGER," +
                BookDataSource.TABLE_BOOK_COLUMN_DATE_MODIFIED + " INTEGER," +
                BookDataSource.TABLE_BOOK_COLUMN_TITLE + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_DESCRIPTION + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_SLUG + " VARCHAR," +

                BookDataSource.TABLE_BOOK_COLUMN_SOURCE_URL + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_SIGNATURE_URL + " VARCHAR)";

        return creationString;
    }

    @Override
    public BookModel getModel(String uid) {
        BookModel model = new BookModel();
        model = (BookModel) this.getModelForKey(uid);
        return model;
    }
}
