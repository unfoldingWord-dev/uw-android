package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.PageModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class StoriesChapterDataSource extends AMDatabaseDataSourceAbstract {

    static String TABLE_CHAPTER = "_table_stories_chapter";

    // Table columns of TABLE_CHAPTER
    static String TABLE_CHAPTER_COLUMN_UID = "_column_stories_chapter_uid";
    static String TABLE_CHAPTER_COLUMN_PARENT_ID = "_column_parent_id";
    static String TABLE_CHAPTER_COLUMN_SLUG = "_column_slug";
    static String TABLE_CHAPTER_COLUMN_NUMBER = "_column_number";
    static String TABLE_CHAPTER_COLUMN_DESCRIPTION = "_column_description";
    static String TABLE_CHAPTER_COLUMN_TITLE = "_column_title";

    public StoriesChapterDataSource(Context context) {
        super(context);
    }

    public ArrayList<PageModel> getChildModels(StoriesChapterModel parentModel) {

        ArrayList<PageModel> modelList = new ArrayList<PageModel>();
        ArrayList<AMDatabaseModelAbstractObject> models = this.loadChildrenModelsFromDatabase(parentModel);

        for(AMDatabaseModelAbstractObject mod : models){
            PageModel model = (PageModel) mod;
            model.setParent(parentModel);
            modelList.add( model);
        }
        return modelList;
    }

    public ArrayList<StoriesChapterModel> getChaptersForParentId(String parentId){

        ArrayList<AMDatabaseModelAbstractObject> models = this.getModelFromDatabase(TABLE_CHAPTER_COLUMN_PARENT_ID, parentId);

        ArrayList<StoriesChapterModel> chapters = new ArrayList<StoriesChapterModel>();
        for(AMDatabaseModelAbstractObject model : models){

            chapters.add((StoriesChapterModel) model);
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
        return new PageDataSource(this.context);
    }

    @Override
    public AMDatabaseDataSourceAbstract getParentDataSource() {
        return new BookDataSource(this.context);
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
    public StoriesChapterModel getModelForSlug(String slug){

        return (StoriesChapterModel) this.getModelFromDatabaseForSlug(slug);
    }

    @Override
    public StoriesChapterModel getModel(String uid) {
        return (StoriesChapterModel) this.getModelForKey(uid);
    }

    @Override
    public ContentValues getModelAsContentValues(AMDatabaseModelAbstractObject model) {

        StoriesChapterModel storiesChapterModel = (StoriesChapterModel) model;
        ContentValues values = new ContentValues();

        if(storiesChapterModel.uid > 0) {
            values.put(TABLE_CHAPTER_COLUMN_UID, storiesChapterModel.uid);
        }
        values.put(TABLE_CHAPTER_COLUMN_PARENT_ID, storiesChapterModel.parentId);
        values.put(TABLE_CHAPTER_COLUMN_NUMBER, storiesChapterModel.number);
        values.put(TABLE_CHAPTER_COLUMN_SLUG, storiesChapterModel.slug);
        values.put(TABLE_CHAPTER_COLUMN_DESCRIPTION, storiesChapterModel.description);
        values.put(TABLE_CHAPTER_COLUMN_TITLE, storiesChapterModel.title);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        StoriesChapterModel model = new StoriesChapterModel();

        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_UID));
        model.parentId = cursor.getLong(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_PARENT_ID));
        model.number =  cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_NUMBER));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_SLUG));
        model.description = cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_DESCRIPTION));
        model.title = cursor.getString(cursor.getColumnIndex(TABLE_CHAPTER_COLUMN_TITLE));

        return model;
    }



    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                StoriesChapterDataSource.TABLE_CHAPTER_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoriesChapterDataSource.TABLE_CHAPTER_COLUMN_PARENT_ID + " INTEGER," +
                StoriesChapterDataSource.TABLE_CHAPTER_COLUMN_NUMBER + " VARCHAR," +
                StoriesChapterDataSource.TABLE_CHAPTER_COLUMN_DESCRIPTION + " VARCHAR," +
                StoriesChapterDataSource.TABLE_CHAPTER_COLUMN_SLUG + " VARCHAR," +
                StoriesChapterDataSource.TABLE_CHAPTER_COLUMN_TITLE + " VARCHAR)";
        return creationString;
    }


}
