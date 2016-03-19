/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import eventbusmodels.BiblePagingEvent;
import eventbusmodels.DownloadResult;
import eventbusmodels.StoriesPagingEvent;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;

/**
 * Created by Fechner on 8/14/15.
 */
public class UWPreferenceDataAccessor {

    private static UWPreferenceDataAccessor sharedInstance;
    public static UWPreferenceDataAccessor getSharedInstance(Context context){
        if (sharedInstance == null) {
            sharedInstance = new UWPreferenceDataAccessor(context);
        }
        return sharedInstance;
    }

    private Context context;


    private UWPreferenceDataAccessor(Context context) {
        this.context = context;
        registerEventListeners();
    }

    private void registerEventListeners(){
        EventBus.getDefault().register(this);
    }

    public void unregisterEventListeners(){
        EventBus.getDefault().unregister(this);
    }

    public void onEventBackgroundThread(BiblePagingEvent event){
        if(event.mainChapter != null) {
            UWPreferenceDataManager.changedToBibleChapter(context, event.mainChapter.getId(), false);
        }
        if(event.secondaryChapter != null) {
            UWPreferenceDataManager.changedToBibleChapter(context, event.secondaryChapter.getId(), true);
        }
    }

    public void onEventBackgroundThread(StoriesPagingEvent event){

        UWPreferenceDataManager.setNewStoriesPage(context, event.mainStoryPage, false);
        UWPreferenceDataManager.setNewStoriesPage(context, event.secondaryStoryPage, true);
    }

    public BiblePagingEvent createBiblePagingEvent(){
        return new BiblePagingEvent(getBibleChapter(false), getBibleChapter(true));
    }

    public StoriesPagingEvent createStoriesPagingEvent(){
        return new StoriesPagingEvent(getStoryPage(false), getStoryPage(true));
    }


    /**
     * Checks if cached chapter is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private BibleChapter getBibleChapter(boolean secondary){

        long chapterId = UWPreferenceManager.getCurrentBibleChapter(context, secondary);
        return loadChapterFromDB(chapterId);
    }

    @Nullable
    private BibleChapter loadChapterFromDB(long id){
        if(id < 0){
            return null;
        }
        return BibleChapter.getModelForId(id, DaoDBHelper.getDaoSession(context));
    }

    /**
     * Checks if cached page is updated, updates if necessary, and returns chapter
     */
    @Nullable
    synchronized private StoryPage getStoryPage(boolean secondary){

        long chapterId = UWPreferenceManager.getCurrentStoryPage(context, secondary);
        return loadPageFromDB(chapterId);
    }

    @Nullable
    private StoryPage loadPageFromDB(long id){
        if(id < 0){
            return null;
        }
        return DaoDBHelper.getDaoSession(context).getStoryPageDao().loadDeep(id);
    }
}
