package utils;

import android.content.Context;
import android.support.annotation.Nullable;

import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;

/**
 * Created by Fechner on 8/14/15.
 */
public class UWPreferenceDataManager {

    private static UWPreferenceDataManager ourInstance = new UWPreferenceDataManager();

    private BibleChapter currentChapter;
    private BibleChapter currentSecondChapter;

    private StoryPage currentPage;
    private StoryPage currentSecondPage;

    @Nullable
    public static BibleChapter getCurrentBibleChapter(Context context, boolean second){
        return getInstance().getBibleChapter(context, second);
    }

    @Nullable
    public static StoryPage getCurrentStoryPage(Context context, boolean second){
        return getInstance().getStoryPage(context, second);
    }

    private static UWPreferenceDataManager getInstance() {
        return ourInstance;
    }


    private UWPreferenceDataManager() {
    }

    @Nullable
    private BibleChapter getBibleChapter(Context context, boolean second){

        return (second)? getCurrentSecondChapter(context) : getCurrentChapter(context);
    }

    @Nullable
    synchronized private BibleChapter getCurrentChapter(Context context){

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, false);
        if(currentChapter == null || chapterId != currentChapter.getId()){
            currentChapter = loadChapterFromDB(context, chapterId);
        }
        return currentChapter;
    }

    @Nullable
    synchronized public BibleChapter getCurrentSecondChapter(Context context) {

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, true);
        if(currentSecondChapter == null || chapterId != currentSecondChapter.getId()){
            currentSecondChapter = loadChapterFromDB(context, chapterId);
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

    @Nullable
    synchronized private StoryPage getCurrentPage(Context context){

        long chapterId = UWPreferenceManager.getCurrentStoryPage(context, false);
        if(currentPage == null || chapterId != currentPage.getId()){
            currentPage = loadPageFromDB(context, chapterId);
        }
        return currentPage;
    }

    @Nullable
    synchronized public StoryPage getCurrentSecondPage(Context context) {

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


}
