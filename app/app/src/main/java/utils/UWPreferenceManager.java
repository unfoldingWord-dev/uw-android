/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.content.Context;
import android.preference.PreferenceManager;

import org.unfoldingword.mobile.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fechner on 3/25/15.
 */
public class UWPreferenceManager {

    private static final String TAG = "UWPreferenceManager";

    /**
     * @param context Context to use
     * @param isSecond true if the desired id is for the second Version in the diglot view
     * @return ID of currently chosen page
     */
    public static long getCurrentStoryPage(Context context, boolean isSecond){
        return (isSecond)? getSelectedStoryPageSecondary(context) : getSelectedStoryPage(context);
    }

    /**
     * @param context Context to use
     * @param isSecond true if the desired id is for the second Version in the diglot view
     * @return ID of currently chosen chapter
     */
    public static long getCurrentBibleChapter(Context context, boolean isSecond){
        return (isSecond)? getSelectedBibleChapterSecondary(context) : getSelectedBibleChapter(context);
    }

    private static final String BIBLE_CHAPTER_ID = "currently_selected_bible_chapter_id";
    public static long getSelectedBibleChapter(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_CHAPTER_ID, -1);
    }
    public static void setSelectedBibleChapter(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_CHAPTER_ID, newValue).commit();
    }

    private static final String BIBLE_CHAPTER_ID_SECONDARY = "currently_selected_bible_chapter_id_secondary";
    public static long getSelectedBibleChapterSecondary(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_CHAPTER_ID_SECONDARY, -1);
    }
    public static void setSelectedBibleChapterSecondary(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_CHAPTER_ID_SECONDARY, newValue).commit();
    }

    private static final String STORY_PAGE = "selected_story_page_id";
    public static long getSelectedStoryPage(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_PAGE, -1);
    }
    public static void setSelectedStoryPage(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_PAGE, newValue).commit();
    }

    private static final String STORY_PAGE_SECONDARY = "selected_secondary_story_page_id";
    public static long getSelectedStoryPageSecondary(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_PAGE_SECONDARY, -1);
    }
    public static void setSelectedStoryPageSecondary(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_PAGE_SECONDARY, newValue).commit();
    }

    private static final String LAST_UPDATED_ID = "last_updated_date";
    public static Long getLastUpdatedDate(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_UPDATED_ID, -1);
    }
    public static void setLastUpdatedDate(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LAST_UPDATED_ID, newValue).commit();
    }

    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    public static boolean getIsFirstLaunch(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_FIRST_LAUNCH, true);
    }
    public static void setIsFirstLaunch(Context context, boolean newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(IS_FIRST_LAUNCH, newValue).commit();
    }

    private static final String HAS_DOWNLOADED_LOCALES = "LAST_LOCALE_UPDATED_ID";
    public static boolean getHasDownloadedLocales(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean(HAS_DOWNLOADED_LOCALES, false);
    }
    public static void setHasDownloadedLocales(Context context, boolean newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(HAS_DOWNLOADED_LOCALES, newValue).commit();
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

    public static final List<String> BIBLE_TEXT_SIZES = Arrays.asList("10", "12", "14", "16", "18", "20");
    private static final String BIBLE_TEXT_SIZE_INDEX = "BIBLE_TEXT_SIZE_INDEX";
    public static String getBibleTextSize(Context context){
        return BIBLE_TEXT_SIZES.get(android.preference.PreferenceManager.getDefaultSharedPreferences(context).getInt(BIBLE_TEXT_SIZE_INDEX, 3));
    }
    public static int getBibleTextSizeIndex(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getInt(BIBLE_TEXT_SIZE_INDEX, 3);
    }
    public static void setBibleTextSize(Context context, int newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(BIBLE_TEXT_SIZE_INDEX, newValue).commit();
    }

    public static final List<String> STORIES_TEXT_SIZES = Arrays.asList("14", "16", "18", "20", "22", "24");
    private static final String STORIES_TEXT_SIZE_INDEX = "STORIES_TEXT_SIZE_INDEX";

    public static String getStoriesTextSize(Context context) {
        return STORIES_TEXT_SIZES.get(android.preference.PreferenceManager.getDefaultSharedPreferences(context).getInt(STORIES_TEXT_SIZE_INDEX, 3));
    }
    public static int getStoriesTextSizeIndex(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getInt(STORIES_TEXT_SIZE_INDEX, 3);
    }
    public static void setStoriesTextSize(Context context, int newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(STORIES_TEXT_SIZE_INDEX, newValue).commit();
    }

}
