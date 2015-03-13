package model.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import model.datasource.AMDatabase.AMDatabaseDataSourceAbstract;
import model.modelClasses.mainData.AMDatabase.AMDatabaseModelAbstractObject;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.StoriesChapterModel;

/**
 * Created by Fechner on 2/24/15.
 */
public class BookDataSource extends AMDatabaseDataSourceAbstract {

    static String TABLE_BOOK = "_table_book_info";

    // Table columns of TABLE_BOOK
    static String TABLE_BOOK_COLUMN_UID = "_column_book_uid";
    static String TABLE_BOOK_COLUMN_PARENT_ID = "_column_parent_id";
    static String TABLE_BOOK_COLUMN_DATE_MODIFIED = "_column_date_modified";
    static String TABLE_BOOK_COLUMN_DIRECTION = "_column_txt_direction";
    static String TABLE_BOOK_COLUMN_LANGUAGE = "_column_language_abbreviation";
    static String TABLE_BOOK_COLUMN_SLUG = "_column_slug";

    static String TABLE_BOOK_COLUMN_WORDS_CANCEL = "_column_words_cancel";
    static String TABLE_BOOK_COLUMN_WORDS_CHAPTERS = "_column_words_chapters";
    static String TABLE_BOOK_COLUMN_WORDS_LANGUAGES = "_column_words_languages";
    static String TABLE_BOOK_COLUMN_WORDS_NEXT_CHAPTER = "_column_words_next_chapter";
    static String TABLE_BOOK_COLUMN_WORDS_OK = "_column_words_ok";
    static String TABLE_BOOK_COLUMN_WORDS_REMOVE_LOCALLY = "_column_words_remove_locally";
    static String TABLE_BOOK_COLUMN_WORDS_REMOVE_THIS_STRING = "_column_words_remove_this_string";
    static String TABLE_BOOK_COLUMN_WORDS_SAVE_LOCALLY = "_column_words_save_locally";
    static String TABLE_BOOK_COLUMN_WORDS_SAVE_THIS_STRING = "_column_words_save_this_string";
    static String TABLE_BOOK_COLUMN_WORDS_SELECT_A_LANGUAGE = "_column_words_select_a_language";

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

    public BookModel getModelForLanguage(String language){

        ArrayList<AMDatabaseModelAbstractObject> models =  this.getModelFromDatabase(TABLE_BOOK_COLUMN_LANGUAGE, language);

        if(models.size() != 1){
            return null;
        }
        else{
            return (BookModel) models.get(0);
        }
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
        values.put(TABLE_BOOK_COLUMN_DIRECTION, versionModel.direction);
        values.put(TABLE_BOOK_COLUMN_LANGUAGE, versionModel.language);
        values.put(TABLE_BOOK_COLUMN_SLUG, versionModel.slug);

        values.put(TABLE_BOOK_COLUMN_WORDS_CANCEL, versionModel.appWords.cancel);
        values.put(TABLE_BOOK_COLUMN_WORDS_CHAPTERS, versionModel.appWords.chapters);
        values.put(TABLE_BOOK_COLUMN_WORDS_LANGUAGES, versionModel.appWords.languages);
        values.put(TABLE_BOOK_COLUMN_WORDS_NEXT_CHAPTER, versionModel.appWords.nextChapter);
        values.put(TABLE_BOOK_COLUMN_WORDS_OK, versionModel.appWords.ok);

        values.put(TABLE_BOOK_COLUMN_WORDS_REMOVE_LOCALLY, versionModel.appWords.removeLocally);
        values.put(TABLE_BOOK_COLUMN_WORDS_REMOVE_THIS_STRING, versionModel.appWords.removeThisString);
        values.put(TABLE_BOOK_COLUMN_WORDS_SAVE_LOCALLY, versionModel.appWords.saveLocally);
        values.put(TABLE_BOOK_COLUMN_WORDS_SAVE_THIS_STRING, versionModel.appWords.saveThisString);
        values.put(TABLE_BOOK_COLUMN_WORDS_SELECT_A_LANGUAGE, versionModel.appWords.selectALanguage);

        return values;
    }

    @Override
    public AMDatabaseModelAbstractObject getObjectFromCursor(Cursor cursor) {

        BookModel model = new BookModel();

        model.uid = cursor.getLong(cursor.getColumnIndex(TABLE_BOOK_COLUMN_UID));
        model.parentId = cursor.getLong(cursor.getColumnIndex(TABLE_BOOK_COLUMN_PARENT_ID));
        model.dateModified =  cursor.getLong(cursor.getColumnIndex(TABLE_BOOK_COLUMN_DATE_MODIFIED));
        model.direction = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_DIRECTION));
        model.language = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_LANGUAGE));
        model.slug = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_SLUG));

        model.appWords.cancel = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_CANCEL));
        model.appWords.chapters = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_CHAPTERS));
        model.appWords.languages = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_LANGUAGES));
        model.appWords.nextChapter = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_NEXT_CHAPTER));
        model.appWords.ok = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_OK));

        model.appWords.removeLocally = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_REMOVE_LOCALLY));
        model.appWords.removeThisString = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_REMOVE_THIS_STRING));
        model.appWords.saveLocally = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_SAVE_LOCALLY));
        model.appWords.saveThisString = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_SAVE_THIS_STRING));
        model.appWords.selectALanguage = cursor.getString(cursor.getColumnIndex(TABLE_BOOK_COLUMN_WORDS_SELECT_A_LANGUAGE));

        return model;
    }

    @Override
    public String getTableCreationString() {

        String creationString =  "CREATE TABLE " + this.getTableName() + "(" +
                BookDataSource.TABLE_BOOK_COLUMN_UID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookDataSource.TABLE_BOOK_COLUMN_PARENT_ID + " INTEGER," +
                BookDataSource.TABLE_BOOK_COLUMN_DATE_MODIFIED + " INTEGER," +
                BookDataSource.TABLE_BOOK_COLUMN_DIRECTION + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_LANGUAGE + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_SLUG + " VARCHAR," +

                BookDataSource.TABLE_BOOK_COLUMN_WORDS_CANCEL + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_CHAPTERS + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_LANGUAGES + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_NEXT_CHAPTER + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_OK + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_REMOVE_LOCALLY + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_REMOVE_THIS_STRING + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_SAVE_LOCALLY + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_SAVE_THIS_STRING + " VARCHAR," +
                BookDataSource.TABLE_BOOK_COLUMN_WORDS_SELECT_A_LANGUAGE + " VARCHAR)";
        return creationString;
    }

    @Override
    public BookModel getModel(String uid) {
        BookModel model = new BookModel();
        this.getModelForKey(uid);
        return model;
    }
}
