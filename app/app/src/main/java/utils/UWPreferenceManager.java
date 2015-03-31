package utils;

import android.content.Context;

/**
 * Created by Fechner on 3/25/15.
 */
public class UWPreferenceManager {

    private static final String STORY_VERSION_ID = "selected_story_version_id";
    public static String getSelectedStoryVersion(Context context){
        return Long.toString(android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_VERSION_ID, -1));
    }
    public static void setSelectedStoryVersion(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_VERSION_ID, newValue).commit();
    }

    private static final String BIBLE_VERSION_ID = "selected_bible_version_id";
    public static String getSelectedBibleVersion(Context context){
        return Long.toString(android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_VERSION_ID, -1));
    }
    public static void setSelectedBibleVersion(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_VERSION_ID, newValue).commit();
    }

    private static final String BIBLE_CHAPTER_ID = "selected_bible_chapter_id";
    public static String getSelectedBibleChapter(Context context){
        return Long.toString(android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_CHAPTER_ID, -1));
    }
    public static void setSelectedBibleChapter(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_CHAPTER_ID, newValue).commit();
    }

    private static final String STORY_CHAPTER_ID = "selected_story_chapter_id";
    public static String getSelectedStoryChapter(Context context){
        return Long.toString(android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_CHAPTER_ID, -1));
    }
    public static void setSelectedStoryChapter(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_CHAPTER_ID, newValue).commit();
    }

}
