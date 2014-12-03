package utils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public interface DBUtils {
    // db info
    String DB_NAME = "_un_folding_word";
    int DB_VERSION = 1;

    // Table details
    String TABLE_LANGUAGE_CATALOG = "_table_language_catalog";
    /**
     * Getting data count
     */
    String QUERY_GET_COUNT = "SELECT COUNT(*) FROM " + TABLE_LANGUAGE_CATALOG;
    String QUERY_GET_ALL_LANGUAGES = "SELECT * FROM " + TABLE_LANGUAGE_CATALOG;
    String TABLE_FRAME_INFO = "_table_frame_info";
    // Table columns of TABLE_LANGUAGE_CATALOG
    String COLUMN_AUTO_GENERATED_ID_TABLE_LANGUAGE_CATALOG = "_column_auto_generated_id_table_language_catalog";
    String COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG = "_column_date_modified_table_language_catalog";
    String COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG = "_column_direction_table_language_catalog";
    String COLUMN_LANGUAGE_TABLE_LANGUAGE_CATALOG = "_column_language_table_language_catalog";
    String COLUMN_CHECKING_ENTITY_TABLE_LANGUAGE_CATALOG = "_column_checking_entity_table_language_catalog";
    String COLUMN_CHECKING_LEVEL_TABLE_LANGUAGE_CATALOG = "_column_checking_level_table_language_catalog";
    String COLUMN_COMMENTS_TABLE_LANGUAGE_CATALOG = "_column_comments_table_language_catalog";
    String COLUMN_CONTRIBUTORS_TABLE_LANGUAGE_CATALOG = "_column_contributors_table_language_catalog";
    String COLUMN_PUBLISH_DATE_TABLE_LANGUAGE_CATALOG = "_column_publish_date_table_language_catalog";
    String COLUMN_SOURCE_TEXT_TABLE_LANGUAGE_CATALOG = "_column_source_text_table_language_catalog";
    String COLUMN_SOURCE_TEXT_VERSION_TABLE_LANGUAGE_CATALOG = "_column_source_text_version_table_language_catalog";

    // Table columns of TABLE_FRAME_INFO
    String COLUMN_VERSION_TABLE_LANGUAGE_CATALOG = "_column_version_table_language_catalog";
    String COLUMN_LANGUAGE_NAME_TABLE_LANGUAGE_CATALOG = "_column_language_name_table_language_catalog";
    /**
     * Creating table called $TABLE_LANGUAGE_CATALOG
     */
    String CREATE_TABLE_LANGUAGE_CATALOG = "CREATE TABLE " + DBUtils.TABLE_LANGUAGE_CATALOG + "(" +
            DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_LANGUAGE_CATALOG + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBUtils.COLUMN_DATE_MODIFIED_TABLE_LANGUAGE_CATALOG + " VARCHAR,"
            + DBUtils.COLUMN_DIRECTION_TABLE_LANGUAGE_CATALOG + " VARCHAR," +
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
    String COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO = "_column_auto_generated_id_frame_info";
    String COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO = "_column_loaded_language_frame_info";
    String COLUMN_NUMBER_TABLE_FRAME_INFO = "_column_number_frame_info";


    // related queries
    String COLUMN_REFERENCE_TABLE_FRAME_INFO = "_column_reference_frame_info";
    String COLUMN_TITLE_TABLE_FRAME_INFO = "_column_title_frame_info";
    String COLUMN_FRAMES_TABLE_FRAME_INFO = "_column_frames_table_frame_info";
    /**
     * Creating table called $TABLE_TABLE_FRAME_INFO
     */
    String CREATE_TABLE_FRAME_INFO = "CREATE TABLE " + DBUtils.TABLE_FRAME_INFO +
            "(" + DBUtils.COLUMN_AUTO_GENERATED_ID_TABLE_FRAME_INFO +
            " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DBUtils.COLUMN_LOADED_LANGUAGE_TABLE_FRAME_INFO + " VARCHAR," +
            DBUtils.COLUMN_NUMBER_TABLE_FRAME_INFO + " VARCHAR," +
            DBUtils.COLUMN_REFERENCE_TABLE_FRAME_INFO + " VARCHAR," +
            DBUtils.COLUMN_TITLE_TABLE_FRAME_INFO + " VARCHAR," +
            DBUtils.COLUMN_FRAMES_TABLE_FRAME_INFO + " VARCHAR)";
}
