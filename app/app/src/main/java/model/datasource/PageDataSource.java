package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.PageModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class PageDataSource extends AMDatabaseDataSourceAbstract {

    static String TABLE_PAGE = "_page_info";

    // Table columns of TABLE_PAGE
    static String TABLE_PAGE_COLUMN_UID = "_column_page_uid";
    static String TABLE_PAGE_COLUMN_PARENT_ID = "_column_parent_id";
    static String TABLE_PAGE_COLUMN_PAGE_NUMBER = "_column_page_number";
    static String TABLE_PAGE_COLUMN_CHAPTER_NUMBER = "_column_chapter_number";
    static String TABLE_PAGE_COLUMN_IMAGE_URL = "_column_img_url";
    static String TABLE_PAGE_COLUMN_TEXT = "_column_text";
    static String TABLE_PAGE_COLUMN_SLUG = "_column_slug";

    public PageDataSource(Context context) {
        super(context);
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_PAGE_COLUMN_SLUG;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return null;
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new StoriesChapterDataSource(this.context);
    }

    @Override
    protected String getParentIdColumnName() {
        return TABLE_PAGE_COLUMN_PARENT_ID;
    }

    @Override
    public String getTableName() {
        return TABLE_PAGE;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_PAGE_COLUMN_UID;
    }

    @Override
    public PageModel getModel(String uid) {
        return (PageModel) this.getModelForKey(uid);
    }

    @Override
    public PageModel getModelForSlug(String slug){
        return (PageModel) this.getModelFromDatabaseForSlug(slug);
    }


    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model) {

        PageModel pageModel = (PageModel) model;
        ContentValues values = new ContentValues();

        if(pageModel.uid > 0) {
            values.put(TABLE_PAGE_COLUMN_UID, pageModel.uid);
        }
        values.put(TABLE_PAGE_COLUMN_PARENT_ID, pageModel.parentId);
        values.put(TABLE_PAGE_COLUMN_PAGE_NUMBER, pageModel.pageNumber);

        values.put(TABLE_PAGE_COLUMN_CHAPTER_NUMBER, pageModel.chapterNumber);
        values.put(TABLE_PAGE_COLUMN_IMAGE_URL, pageModel.imageUrl);
        values.put(TABLE_PAGE_COLUMN_TEXT, pageModel.text);
        values.put(TABLE_PAGE_COLUMN_SLUG, pageModel.slug);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        PageModel model = new PageModel();
        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_PAGE_COLUMN_UID));
        model.parentId = cursor.getLong(cursor.getColumnIndex(TABLE_PAGE_COLUMN_PARENT_ID));
        model.pageNumber = cursor.getString(cursor.getColumnIndex(TABLE_PAGE_COLUMN_PAGE_NUMBER));

        model.chapterNumber = cursor.getString(cursor.getColumnIndex(TABLE_PAGE_COLUMN_CHAPTER_NUMBER));
        model.imageUrl = cursor.getString(cursor.getColumnIndex(TABLE_PAGE_COLUMN_IMAGE_URL));
        model.text = cursor.getString(cursor.getColumnIndex(TABLE_PAGE_COLUMN_TEXT));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_PAGE_COLUMN_SLUG));

        return model;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                PageDataSource.TABLE_PAGE_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                PageDataSource.TABLE_PAGE_COLUMN_PARENT_ID + " INTEGER," +
                PageDataSource.TABLE_PAGE_COLUMN_PAGE_NUMBER + " VARCHAR," +

                PageDataSource.TABLE_PAGE_COLUMN_CHAPTER_NUMBER + " VARCHAR," +
                PageDataSource.TABLE_PAGE_COLUMN_IMAGE_URL + " VARCHAR," +
                PageDataSource.TABLE_PAGE_COLUMN_SLUG + " VARCHAR, " +
                PageDataSource.TABLE_PAGE_COLUMN_TEXT + " VARCHAR)";
        return creationString;
    }
}
