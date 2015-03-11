package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import model.modelClasses.mainData.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.PageModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class BibleChapterDataSource extends AMDatabaseDataSourceAbstract {

    static String TABLE_CHAPTER = "_table_bible_chapter";

    // Table columns of TABLE_CHAPTER
    static String TABLE_CHAPTER_COLUMN_UID = "_column_bible_chapter_uid";
    static String TABLE_CHAPTER_COLUMN_PARENT_ID = "_column_parent_id";
    static String TABLE_CHAPTER_COLUMN_SLUG = "_column_slug";
    static String TABLE_CHAPTER_COLUMN_NUMBER = "_column_number";
    static String TABLE_CHAPTER_COLUMN_TEXT = "_column_text";

    public BibleChapterDataSource(Context context) {
        super(context);
    }

    public ArrayList<PageModel> getChildModels(BibleChapterModel model) {

        return null;
    }

    public ArrayList<BibleChapterModel> getChaptersForParentId(String parentId){

        ArrayList<AMDatabaseModelAbstractObject> models = this.getModelFromDatabase(TABLE_CHAPTER_COLUMN_PARENT_ID, parentId);

        ArrayList<BibleChapterModel> chapters = new ArrayList<BibleChapterModel>();
        for(AMDatabaseModelAbstractObject model : models){

            chapters.add((BibleChapterModel) model);
        }

        return chapters;
    }

    @Override
    protected String getParentIdColumnName() {
        return TABLE_CHAPTER_COLUMN_PARENT_ID;
    }

    @Override
    protected String getSlugColumnName() {
        return TABLE_CHAPTER_COLUMN_SLUG;
    }

    @Override
    public AMDatabaseDataSourceAbstract getChildDataSource() {
        return null;
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new VersionDataSource(this.context);
    }

    @Override
    public String getTableName() {
        return TABLE_CHAPTER;
    }

    @Override
    public String getUIDColumnName() {
        return TABLE_CHAPTER_COLUMN_UID;
    }

    @Override
    public BibleChapterModel getModelForSlug(String slug){
        return (BibleChapterModel) this.getModelFromDatabaseForSlug(slug);
    }

    @Override
    public BibleChapterModel getModel(String uid) {
        return (BibleChapterModel) this.getModelForKey(uid);
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model) {

        BibleChapterModel chapterModel = (BibleChapterModel) model;
        ContentValues values = new ContentValues();

        if(chapterModel.uid > 0) {
            values.put(TABLE_CHAPTER_COLUMN_UID, chapterModel.uid);
        }
        values.put(TABLE_CHAPTER_COLUMN_PARENT_ID, chapterModel.parentId);
        values.put(TABLE_CHAPTER_COLUMN_NUMBER, chapterModel.number);
        values.put(TABLE_CHAPTER_COLUMN_TEXT, chapterModel.text);
        values.put(TABLE_CHAPTER_COLUMN_SLUG, chapterModel.slug);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        BibleChapterModel model = new BibleChapterModel();

        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_UID));
        model.parentId = cursor.getLong(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_PARENT_ID));
        model.number =  cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_NUMBER));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_SLUG));
        model.text = cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_TEXT));

        return model;
    }


    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                BibleChapterDataSource.TABLE_CHAPTER_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                BibleChapterDataSource.TABLE_CHAPTER_COLUMN_PARENT_ID + " INTEGER," +
                BibleChapterDataSource.TABLE_CHAPTER_COLUMN_NUMBER + " VARCHAR," +
                BibleChapterDataSource.TABLE_CHAPTER_COLUMN_SLUG + " VARCHAR," +
                BibleChapterDataSource.TABLE_CHAPTER_COLUMN_TEXT + " VARCHAR)";
        return creationString;
    }


}
