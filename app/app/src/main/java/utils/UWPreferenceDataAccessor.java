package utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;

/**
 * Created by Fechner on 8/14/15.
 */
public class UWPreferenceDataAccessor {

    private static UWPreferenceDataAccessor ourInstance = new UWPreferenceDataAccessor();

    private BibleChapter currentChapter;
    private BibleChapter currentSecondChapter;

    private StoryPage currentPage;
    private StoryPage currentSecondPage;

    private static List<PreferencesStoryPageChangedListener> storyPageChangedListeners = new ArrayList<>();
    private static List<PreferencesBibleChapterChangedListener> bibleChapterChangedListeners = new ArrayList<>();

    public void addStoryPageListener(PreferencesStoryPageChangedListener listener){
        storyPageChangedListeners.add(listener);
    }
    public void addBibleChapterListener(PreferencesBibleChapterChangedListener listener){
        bibleChapterChangedListeners.add(listener);
    }

    public void removeStoryPageListener(PreferencesStoryPageChangedListener listener){
        storyPageChangedListeners.remove(listener);
    }
    public void removeBibleChapterListener(PreferencesBibleChapterChangedListener listener){
        bibleChapterChangedListeners.remove(listener);
    }


    /**
     * @param context Context to use
     * @param second true of this is for the second version in the diglot view
     * @return the currently chosen chapter
     */
    @Nullable
    synchronized public static BibleChapter getCurrentBibleChapter(Context context, boolean second){
        return getInstance().getBibleChapter(context, second);
    }

    /**
     * @param context Context to use
     * @param second true of this is for the second version in the diglot view
     * @return The currently chosen StoryPage
     */
    @Nullable
    synchronized public static StoryPage getCurrentStoryPage(Context context, boolean second){
        return getInstance().getStoryPage(context, second);
    }

    private static UWPreferenceDataAccessor getInstance() {
        return ourInstance;
    }

    private UWPreferenceDataAccessor() {
    }

    @Nullable
    private BibleChapter getBibleChapter(Context context, boolean second){

        return (second)? getCurrentSecondChapter(context) : getCurrentChapter(context);
    }

    /**
     * Checks if cached chapter is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private BibleChapter getCurrentChapter(Context context){

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, false);
        if(currentChapter == null || chapterId != currentChapter.getId()){
            currentChapter = loadChapterFromDB(context, chapterId);
        }
        else if(chapterId < 0){
            currentChapter = null;
        }
        return currentChapter;
    }

    /**
     * Checks if cached chapter is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private BibleChapter getCurrentSecondChapter(Context context) {

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, true);
        if(currentSecondChapter == null || chapterId != currentSecondChapter.getId()){
            currentSecondChapter = loadChapterFromDB(context, chapterId);
        }
        else if(chapterId < 0){
            currentSecondChapter = null;
        }
        return currentSecondChapter;
    }

    @Nullable
    private BibleChapter loadChapterFromDB(Context context, long id){
        if(id < 0){
            return null;
        }
        return BibleChapter.getModelForId(id, DaoDBHelper.getDaoSession(context));
    }


    @Nullable
    private StoryPage getStoryPage(Context context, boolean second){

        return (second)? getCurrentSecondPage(context) : getCurrentPage(context);
    }

    /**
     * Checks if cached page is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private StoryPage getCurrentPage(Context context){

        long chapterId = UWPreferenceManager.getCurrentStoryPage(context, false);
        if(currentPage == null || chapterId != currentPage.getId()){
            currentPage = loadPageFromDB(context, chapterId);
        }
        return currentPage;
    }

    /**
     * Checks if cached page is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private StoryPage getCurrentSecondPage(Context context) {

        long chapterId = UWPreferenceManager.getCurrentStoryPage(context, true);
        if(currentSecondPage == null || chapterId != currentSecondPage.getId()){
            currentSecondPage = loadPageFromDB(context, chapterId);
        }
        return currentSecondPage;
    }

    @Nullable
    private StoryPage loadPageFromDB(Context context, long id){
        if(id < 0){
            return null;
        }
        return DaoDBHelper.getDaoSession(context).getStoryPageDao().loadDeep(id);
    }

    public interface PreferencesStoryPageChangedListener{
        void storyPageChanged(StoryPage page);
    }

    public interface PreferencesBibleChapterChangedListener{
        void bibleChapterChanged(BibleChapter chapter);
    }
}
