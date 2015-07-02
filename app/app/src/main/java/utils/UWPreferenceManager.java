package utils;

import android.content.Context;
import android.preference.PreferenceManager;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 3/25/15.
 */
public class UWPreferenceManager {

    private static final String STORY_VERSION_ID = "selected_story_version_id";
    public static long getSelectedStoryVersion(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_VERSION_ID, -1);
    }
    public static void setSelectedStoryVersion(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_VERSION_ID, newValue).commit();
    }

//    private static final String BIBLE_VERSION_ID = "selected_bible_version_id";
//    public static long getSelectedBibleVersion(Context context){
//        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_VERSION_ID, -1);
//    }
//    public static void setSelectedBibleVersion(Context context, long newValue){
//
//        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_VERSION_ID, newValue).commit();
//    }

    private static final String BIBLE_CHAPTER_ID = "selected_bible_chapter_id";
    public static long getSelectedBibleChapter(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_CHAPTER_ID, -1);
    }
    public static void setSelectedBibleChapter(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_CHAPTER_ID, newValue).commit();
    }

    private static final String STORY_CHAPTER_ID = "selected_story_chapter_id";
    public static long getSelectedStoryChapter(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_CHAPTER_ID, -1);
    }
    public static void setSelectedStoryChapter(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_CHAPTER_ID, newValue).commit();
    }

    private static final String LAST_UPDATED_ID = "last_updated_date";
    public static Long getLastUpdatedDate(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_UPDATED_ID, -1);
    }
    public static void setLastUpdatedDate(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_UPDATED_ID, newValue).commit();
    }

    private static final String HAS_DOWNLOADED_LOCALES_ID = "LAST_LOCALE_UPDATED_ID";
    public static boolean getHasDownloadedLocales(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean(HAS_DOWNLOADED_LOCALES_ID, false);
    }
    public static void setHasDownloadedLocales(Context context, boolean newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(HAS_DOWNLOADED_LOCALES_ID, newValue).commit();
    }

    private static final String DATA_DOWNLOAD_URL_KEY = "base_url";
    public static String getDataDownloadUrl(Context context){
       return PreferenceManager.getDefaultSharedPreferences(context).getString(DATA_DOWNLOAD_URL_KEY, context.getResources().getString(R.string.pref_default_base_url));
    }
    public static void setDataDownloadUrl(Context context, String newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString(DATA_DOWNLOAD_URL_KEY, newValue).commit();
    }

    private static final String LANGUAGES_DOWNLOAD_URL_KEY = "languages_json_url";
    public static String getLanguagesDownloadUrl(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGES_DOWNLOAD_URL_KEY,  context.getResources().getString(R.string.languages_json_url));
    }

}
