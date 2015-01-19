package utils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public interface DBUtils {

    //region General Declarations
    // model.db info
    String DB_NAME = "_un_folding_word";
    int DB_VERSION = 2;

    // Table details
    String TABLE_LANGUAGE = "_table_language_catalog";
    String TABLE_BOOK = "_table_book_info";
    String TABLE_CHAPTER = "_table_chapter_info";
    String TABLE_PAGE = "_table_page_info";


    //endregion

    //region Language Section

    /**
     * Getting data count
     */
    String QUERY_GET_COUNT = "SELECT COUNT(*) FROM " + DBUtils.TABLE_LANGUAGE;

    /**
     * Selecting all modified date from DB
     */
    String QUERY_GET_MOD_DATE = "SELECT " + DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG + " FROM " + DBUtils.TABLE_LANGUAGE + " ORDER BY "
            + DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + " ASC";
    /**
     * Get all language info
     */
    String QUERY_GET_ALL_LANGUAGES = "SELECT * FROM " + DBUtils.TABLE_LANGUAGE + " ORDER BY " +
            DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + " ASC";

    String QUERY_SELECT_LANGUAGE_FROM_LANGUAGE_KEY = "SELECT * FROM " +
            DBUtils.TABLE_LANGUAGE + " WHERE " +
            DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + "=?";

    // Table columns of TABLE_LANGUAGE
    String COLUMN_AUTO_GENERATED_ID_TABLE_LANGUAGE_CATALOG = "_column_auto_generated_id_table_language_catalog";
    String COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG = "_column_date_modified_table_language_catalog";
    String COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG = "_column_language_table_language_catalog";
    String COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG = "_column_direction_table_language_catalog";
    String COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG = "_column_checking_entity_table_language_catalog";
    String COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG = "_column_checking_level_table_language_catalog";
    String COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG = "_column_comments_table_language_catalog";
    String COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG = "_column_contributors_table_language_catalog";
    String COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG = "_column_publish_date_table_language_catalog";
    String COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG = "_column_source_text_table_language_catalog";
    String COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG = "_column_source_text_version_table_language_catalog";
    String COLUMN_VERSION_TABLE_LANGUAGE_CATALOG = "_column_version_table_language_catalog";
    String COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG = "_column_language_name_table_language_catalog";

    /**
     * Creating table called $TABLE_LANGUAGE
     */
    String CREATE_TABLE_LANGUAGE_CATALOG = "CREATE TABLE " + DBUtils.TABLE_LANGUAGE + "(" +
            DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_LANGUAGE_CATALOG + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG + " INTEGER," +
            DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_VERSION_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
            DBUtils.COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG + " VARCHAR)";

    //endregion


    //region Book Section

    String QUERY_SELECT_BOOK_BASED_ON_LANGUAGE = "SELECT * FROM " +
            DBUtils.TABLE_BOOK + " WHERE " +
            DBUtils.COLUMN_BOOK_LANGUAGE_ABBREVIATION + "=?";

    String QUERY_SELECT_BOOK_FROM_BOOK_KEY = "SELECT * FROM " +
            DBUtils.TABLE_BOOK + " WHERE " +
            DBUtils.COLUMN_BOOK_LANGUAGE_ABBREVIATION + "=?";

    // Table columns of TABLE_BOOK
    String COLUMN_BOOK_AUTO_GENERATED_ID_TABLE_BOOK = "_column_auto_generated_id_table_book";
    String COLUMN_BOOK_MODIFIED = "_column_modified";
    String COLUMN_BOOK_TXT_DIRECTION = "_column_txt_direction";
    String COLUMN_BOOK_LANGUAGE_ABBREVIATION = "_column_language_abbreviation";
    String COLUMN_BOOK_WORDS_CANCEL = "_column_words_cancel";
    String COLUMN_BOOK_WORDS_CHAPTERS = "_column_words_chapters";
    String COLUMN_BOOK_WORDS_LANGUAGES = "_column_words_languages";
    String COLUMN_BOOK_WORDS_NEXT_CHAPTER = "_column_words_next_chapter";
    String COLUMN_BOOK_WORDS_OK = "_column_words_ok";
    String COLUMN_BOOK_WORDS_REMOVE_LOCALLY = "_column_words_remove_locally";
    String COLUMN_BOOK_WORDS_REMOVE_THIS_STRING = "_column_words_remove_this_string";
    String COLUMN_BOOK_WORDS_SAVE_LOCALLY = "_column_words_save_locally";
    String COLUMN_BOOK_WORDS_SAVE_THIS_STRING = "_column_words_save_this_string";
    String COLUMN_BOOK_WORDS_SELECT_A_LANGUAGE = "_column_words_select_a_language";

    /**
     *
     */
    /**
     * Creating table called TABLE_BOOK
     */
    String CREATE_TABLE_BOOK_INFO = "CREATE TABLE " + DBUtils.TABLE_BOOK +
            "(" + DBUtils.COLUMN_BOOK_AUTO_GENERATED_ID_TABLE_BOOK +
            " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBUtils.COLUMN_BOOK_MODIFIED + " VARCHAR," +
            DBUtils.COLUMN_BOOK_TXT_DIRECTION + " VARCHAR," +
            DBUtils.COLUMN_BOOK_LANGUAGE_ABBREVIATION + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_CANCEL + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_CHAPTERS + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_LANGUAGES + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_NEXT_CHAPTER + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_OK + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_REMOVE_LOCALLY + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_REMOVE_THIS_STRING + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_SAVE_LOCALLY + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_SAVE_THIS_STRING + " VARCHAR," +
            DBUtils.COLUMN_BOOK_WORDS_SELECT_A_LANGUAGE + " VARCHAR)";


    //endregion

    //region Chapter Section


    String QUERY_SELECT_CHAPTER_BASED_ON_BOOK = "SELECT * FROM " +
            DBUtils.TABLE_CHAPTER + " WHERE " +
            DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_KEY + "=?";

    String QUERY_SELECT_CHAPTER_WITH_LANG_NUMB = "SELECT * FROM " +
            DBUtils.TABLE_CHAPTER + " WHERE " +
            DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_CHAPTER_KEY + "=?";

    String QUERY_TAG_CHAPTER_BY_CHAPTER_NUMBER_ASCENDING = " ORDER BY " + DBUtils.COLUMN_CHAPTER_NUMBER + " ASC";

    // Table columns of TABLE_CHAPTER
    String COLUMN_AUTO_GENERATED_ID_TABLE_CHAPTER_INFO = "_column_auto_generated_id_chapter_info";
    String COLUMN_CHAPTER_NUMBER = "_column_number";
    String COLUMN_CHAPTER_REFERENCE = "_column_reference";
    String COLUMN_CHAPTER_TITLE = "_column_title";
    String COLUMN_CHAPTER_BOOK_LANGUAGE_KEY = "_column_book_language_key";
    String COLUMN_CHAPTER_BOOK_LANGUAGE_CHAPTER_KEY = "_column_book_language_chapter_key";

    /**
     * Creating table called $TABLE_TABLE_CHAPTER_INFO
     */
    String CREATE_TABLE_CHAPTER = "CREATE TABLE " + TABLE_CHAPTER +
            "(" + DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_CHAPTER_INFO +
            " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBUtils.COLUMN_CHAPTER_NUMBER + " VARCHAR," +
            DBUtils.COLUMN_CHAPTER_REFERENCE + " VARCHAR," +
            DBUtils.COLUMN_CHAPTER_TITLE + " VARCHAR," +
            DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_KEY + " VARCHAR," +
            DBUtils.COLUMN_CHAPTER_BOOK_LANGUAGE_CHAPTER_KEY + " VARCHAR)";

    //endregion


    //region Page Section

    String QUERY_SELECT_PAGE_BASED_ON_CHAPTER = "SELECT * FROM " +
            DBUtils.TABLE_PAGE + " WHERE " +
            DBUtils.COLUMN_PAGE_LANGUAGE_AND_CHAPTER + "=?";

    String QUERY_SELECT_PAGE_FROM_KEY = "SELECT * FROM " +
            DBUtils.TABLE_PAGE + " WHERE " +
            DBUtils.COLUMN_PAGE_LANGUAGE_AND_KEY + "=?";

    String QUERY_TAG_PAGE_BY_PAGE_NUMBER_ASCENDING = " ORDER BY " + DBUtils.COLUMN_PAGE_PAGE_NUMBER + " ASC";

    // Table columns of TABLE_PAGE
    String COLUMN_AUTO_GENERATED_ID_TABLE_PAGE_INFO = "_column_auto_generated_id_page_info";
    String COLUMN_PAGE_PAGE_NUMBER = "_column_page_number";
    String COLUMN_PAGE_CHAPTER_NUMBER = "_column_chapter_number";
    String COLUMN_PAGE_IMG_URL = "_column_img_url";
    String COLUMN_PAGE_TEXT = "_column_text";
    String COLUMN_PAGE_LANGUAGE_AND_CHAPTER = "_column_language_and_chapter";
    String COLUMN_PAGE_LANGUAGE_AND_KEY = "_column_language_and_key";



    /**
     * Creating table called TABLE_PAGE
     */
    String CREATE_TABLE_PAGE_INFO = "CREATE TABLE " + DBUtils.TABLE_PAGE +
            "(" + DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_PAGE_INFO +
            " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBUtils.COLUMN_PAGE_LANGUAGE_AND_KEY + " VARCHAR," +
            DBUtils.COLUMN_PAGE_CHAPTER_NUMBER + " VARCHAR," +
            DBUtils.COLUMN_PAGE_PAGE_NUMBER + " VARCHAR," +
            DBUtils.COLUMN_PAGE_IMG_URL + " VARCHAR," +
            DBUtils.COLUMN_PAGE_LANGUAGE_AND_CHAPTER + " VARCHAR," +
            DBUtils.COLUMN_PAGE_TEXT + " VARCHAR)";


    //endregion
}