package utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.unfoldingword.mobile.R;

import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;

/**
 * Created by Fechner on 3/25/15.
 */
public class UWPreferenceManager {

    private static final String TAG = "UWPreferenceManager";

    public static long getCurrentStoryPage(Context context, boolean isSecond){

        return (isSecond)? getSelectedStoryPageSecondary(context) : getSelectedStoryPage(context);
    }

    public static long getCurrentBibleChapter(Context context, boolean isSecond){

        return (isSecond)? getSelectedBibleChapterSecondary(context) : getSelectedBibleChapter(context);
    }

    public static void changedToBibleChapter(Context context, long chapterId, boolean isSecond){
        if(isSecond){
            setSelectedBibleChapterSecondary(context, chapterId);
        }
        else{
            setSelectedBibleChapter(context, chapterId);
        }

        updateChapterSelection(context, chapterId, isSecond);
    }

    private static void updateChapterSelection(Context context, long activeChapterId, boolean isSecond){

        DaoSession session = DaoDBHelper.getDaoSession(context);
        long changingId = (isSecond)? getSelectedBibleChapter(context) : getSelectedBibleChapterSecondary(context);
        BibleChapter activeChapter = BibleChapter.getModelForId(activeChapterId, session);
        BibleChapter changingChapter = BibleChapter.getModelForId(changingId, session);

        BibleChapter newChapter;
        if(changingChapter != null) {
            Book correctChangingBook = changingChapter.getBook().getVersion().getBookForBookSlug(activeChapter.getBook().getSlug(), session);
            newChapter = correctChangingBook.getBibleChapterForNumber(activeChapter.getNumber(), session);
        }
        else{
            newChapter = activeChapter;
        }

        if(isSecond){
            setSelectedBibleChapter(context, newChapter.getId());
        }
        else{
            setSelectedBibleChapterSecondary(context, newChapter.getId());
        }
    }

    public static void selectedVersion(Context context, Version version, boolean isSecond){

        if(version.getLanguage().getProject().isBibleStories()){
            UWPreferenceManager.setNewStoriesVersion(context, version, isSecond);
        }
        else{
            UWPreferenceManager.setNewBibleVersion(context, version, isSecond);
        }
    }

    public static void setNewBibleVersion(Context context, Version version, boolean isSecond){

        long currentId = getCurrentBibleChapter(context, isSecond);
        BibleChapter requestedChapter = null;
        if(currentId > -1){
            BibleChapter currentChapter = BibleChapter.getModelForId(currentId, DaoDBHelper.getDaoSession(context));
            Book newBook = version.getBookForBookSlug(currentChapter.getBook().getSlugIdentifier(), DaoDBHelper.getDaoSession(context));
            if(newBook != null){
                requestedChapter = newBook.getBibleChapterForNumber(currentChapter.getNumber(), DaoDBHelper.getDaoSession(context));
            }
        }
        if(requestedChapter == null){
            requestedChapter = version.getBooks().get(0).getBibleChapters(true).get(0);
        }

        changedToBibleChapter(context, requestedChapter.getId(), isSecond);

//        if(isSecond) {
//            setSelectedBibleChapterSecondary(context, requestedChapter.getId());
//        }
//        else{
//            setSelectedBibleChapter(context, requestedChapter.getId());
//        }
    }

    public static void setNewStoriesVersion(Context context, Version newVersion, boolean isSecond) {

        StoryPage currentPage = UWPreferenceDataManager.getCurrentStoryPage(context, isSecond);

        if(currentPage == null){
            StoryPage page = newVersion.getBooks().get(0).getStoryChapters().get(0).getStoryPages().get(0);
            long newPageId = page.getId();
            changedToStoryPage(context, newPageId, isSecond);
            changedToStoryPage(context, newPageId, !isSecond);
            return;
        }

        DaoSession session = DaoDBHelper.getDaoSession(context);
        Book book = newVersion.getBookForBookSlug(currentPage.getStoriesChapter().getBook().getSlug(), session);
        StoriesChapter newChapter = book.getStoriesChapterForNumber(currentPage.getStoriesChapter().getNumber(), session);
        StoryPage newPage = newChapter.getStoryPageForNumber(currentPage.getNumber(), session);
        changedToStoryPage(context, newPage.getId(), isSecond);
    }

    public static void setNewStoriesPage(Context context, StoryPage newPage, boolean isSecond) {

        changedToStoryPage(context, newPage.getId(), isSecond);

        StoryPage otherPage = UWPreferenceDataManager.getCurrentStoryPage(context, !isSecond);
        if(otherPage != null) {
            Version version = otherPage.getStoriesChapter().getBook().getVersion();
            DaoSession session = DaoDBHelper.getDaoSession(context);
            Book book = version.getBookForBookSlug(newPage.getStoriesChapter().getBook().getSlug(), session);
            StoriesChapter newChapter = book.getStoriesChapterForNumber(newPage.getStoriesChapter().getNumber(), session);
            StoryPage newOtherPage = newChapter.getStoryPageForNumber(newPage.getNumber(), session);
            changedToStoryPage(context, newOtherPage.getId(), !isSecond);
        }
    }

    private static void changedToStoryPage(Context context, long id, boolean isSecond){

        if(isSecond){
            setSelectedStoryPageSecondary(context, id);
        }
        else{
            setSelectedStoryPage(context, id);
        }
    }

    public static void willDeleteVersion(Context context, Version version){
        if(version.getLanguage().getProject().isBibleStories()) {
            willDeleteStoryVersion(context, version);
        }
        else {
            willDeleteBibleVersion(context, version);
        }
    }

    private static void willDeleteStoryVersion(Context context, Version version){

        StoryPage currentPage = UWPreferenceDataManager.getCurrentStoryPage(context, false);
        StoryPage secondaryPage = UWPreferenceDataManager.getCurrentStoryPage(context, true);

        if(currentPage != null && currentPage.getStoriesChapter().getBook().getVersionId() == (version.getId())){
            setSelectedStoryPage(context, -1);
        }
        if(secondaryPage != null && secondaryPage.getStoriesChapter().getBook().getVersionId() == (version.getId())){
            setSelectedStoryPageSecondary(context, -1);
        }
    }

    private static void willDeleteBibleVersion(Context context, Version version){

        BibleChapter currentChapter = UWPreferenceDataManager.getCurrentBibleChapter(context, false);
        BibleChapter secondaryChapter = UWPreferenceDataManager.getCurrentBibleChapter(context, true);

        if(currentChapter == null || secondaryChapter == null){
            return;
        }
        boolean sameChapter = currentChapter.getId().equals(secondaryChapter.getId());

        if(currentChapter.getBook().getVersionId() == (version.getId())){
            setSelectedBibleChapter(context, (sameChapter)? -1 : secondaryChapter.getId());
        }
        if(secondaryChapter.getBook().getVersionId() == (version.getId())){
            setSelectedBibleChapterSecondary(context, (sameChapter)? -1 : currentChapter.getId());
        }
    }

    private static final String BIBLE_CHAPTER_ID = "selected_normal_bible_chapter_id";
    public static long getSelectedBibleChapter(Context context){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(BIBLE_CHAPTER_ID, -1);
    }

    public static void setSelectedBibleChapter(Context context, long newValue){
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(BIBLE_CHAPTER_ID, newValue).commit();
    }

    private static final String BIBLE_CHAPTER_ID_SECONDARY = "selected_normal_bible_chapter_id_secondary";
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
        Log.i(TAG, "set main story page: " + newValue);
    }

    private static final String STORY_PAGE_SECONDARY = "selected_secondary_story_page_id";
    public static long getSelectedStoryPageSecondary(Context context){

        long id = android.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong(STORY_PAGE_SECONDARY, -1);
        return id;
    }
    public static void setSelectedStoryPageSecondary(Context context, long newValue){

        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(STORY_PAGE_SECONDARY, newValue).commit();
        Log.i(TAG, "set secondary story page: " + newValue);
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
