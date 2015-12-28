/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.content.Context;

import eventbusmodels.BiblePagingEvent;
import eventbusmodels.StoriesPagingEvent;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;

/**
 * Created by Fechner on 8/21/15.
 */
public class UWPreferenceDataManager {

    public static void resetChapterSelections(Context context){
        changedToBibleChapter(context, -1, true);
        changedToBibleChapter(context, -1, false);
        changedToStoryPage(context, -1, true);
        changedToStoryPage(context, -1, false);
    }

    public static void changedToBibleChapter(Context context, long chapterId, boolean isSecond){
        if(isSecond){
            UWPreferenceManager.setSelectedBibleChapterSecondary(context, chapterId);
        }
        else{
            UWPreferenceManager.setSelectedBibleChapter(context, chapterId);
        }

        updateChapterSelection(context, chapterId, isSecond);
    }

    private static void updateChapterSelection(Context context, long activeChapterId, boolean isSecond){

        DaoSession session = DaoDBHelper.getDaoSession(context);
        long changingId = (isSecond)? UWPreferenceManager.getSelectedBibleChapter(context) : UWPreferenceManager.getSelectedBibleChapterSecondary(context);
        BibleChapter activeChapter = BibleChapter.getModelForId(activeChapterId, session);
        BibleChapter changingChapter = BibleChapter.getModelForId(changingId, session);

        BibleChapter newChapter;
        if(changingChapter != null) {
            Book correctChangingBook = changingChapter.getBook().getVersion().getBookForBookSlug(activeChapter.getBook().getSlug(), session);
            newChapter = correctChangingBook.getBibleChapterForNumber(activeChapter.getNumber());
        }
        else{
            newChapter = activeChapter;
        }

        if(isSecond){
            UWPreferenceManager.setSelectedBibleChapter(context, newChapter.getId());
        }
        else{
            UWPreferenceManager.setSelectedBibleChapterSecondary(context, newChapter.getId());
        }
    }

    public static void selectedVersion(Context context, Version version, boolean isSecond){

        if(version.getLanguage().getProject().isBibleStories()){
            setNewStoriesVersion(context, version, isSecond);
        }
        else{
            setNewBibleVersion(context, version, isSecond);
        }
    }

    public static void setNewBibleVersion(Context context, Version version, boolean isSecond){

        long currentId = UWPreferenceManager.getCurrentBibleChapter(context, isSecond);
        BibleChapter requestedChapter = null;
        if(currentId > -1){
            BibleChapter currentChapter = BibleChapter.getModelForId(currentId, DaoDBHelper.getDaoSession(context));
            Book newBook = version.getBookForBookSlug(currentChapter.getBook().getSlug(), DaoDBHelper.getDaoSession(context));
            if(newBook != null){
                requestedChapter = newBook.getBibleChapterForNumber(currentChapter.getNumber());
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

        StoriesPagingEvent currentEvent = StoriesPagingEvent.getStickyEvent(context);
        StoryPage currentPage = (isSecond)? currentEvent.secondaryStoryPage : currentEvent.mainStoryPage;

        if(currentPage == null){
            StoryPage page = newVersion.getBooks().get(0).getStoryChapters().get(0).getStoryPages().get(0);
            long newPageId = page.getId();
            changedToStoryPage(context, newPageId, isSecond);
            changedToStoryPage(context, newPageId, !isSecond);
            return;
        }

        DaoSession session = DaoDBHelper.getDaoSession(context);
        Book book = newVersion.getBookForBookSlug(currentPage.getStoriesChapter().getBook().getSlug(), session);
        StoriesChapter newChapter = book.getStoriesChapterForNumber(currentPage.getStoriesChapter().getNumber());
        StoryPage newPage = newChapter.getStoryPageForNumber(currentPage.getNumber());
        changedToStoryPage(context, newPage.getId(), isSecond);
    }

    public static void setNewStoriesPage(Context context, StoryPage newPage, boolean isSecond) {

        changedToStoryPage(context, newPage.getId(), isSecond);

        StoriesPagingEvent currentEvent = StoriesPagingEvent.getStickyEvent(context);
        StoryPage otherPage = (isSecond)? currentEvent.mainStoryPage : currentEvent.secondaryStoryPage;

        if(otherPage != null) {
            Version version = otherPage.getStoriesChapter().getBook().getVersion();
            DaoSession session = DaoDBHelper.getDaoSession(context);
            Book book = version.getBookForBookSlug(newPage.getStoriesChapter().getBook().getSlug(), session);
            StoriesChapter newChapter = book.getStoriesChapterForNumber(newPage.getStoriesChapter().getNumber());
            StoryPage newOtherPage = newChapter.getStoryPageForNumber(newPage.getNumber());
            changedToStoryPage(context, newOtherPage.getId(), !isSecond);
        }
    }

    public static void changedToStoryPage(Context context, long id, boolean isSecond){

        if(isSecond){
            UWPreferenceManager.setSelectedStoryPageSecondary(context, id);
        }
        else{
            UWPreferenceManager.setSelectedStoryPage(context, id);
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

        StoriesPagingEvent currentEvent = StoriesPagingEvent.getStickyEvent(context);

        boolean samePage = currentEvent.mainStoryPage.getId().equals(currentEvent.secondaryStoryPage.getId());

        if(currentEvent.mainStoryPage.getStoriesChapter().getBook().getVersionId() == (version.getId())){
            UWPreferenceManager.setSelectedStoryPage(context, (samePage) ? -1 : currentEvent.secondaryStoryPage.getId());
        }
        if(currentEvent.secondaryStoryPage.getStoriesChapter().getBook().getVersionId() == (version.getId())){
            UWPreferenceManager.setSelectedStoryPageSecondary(context, (samePage) ? -1 : currentEvent.mainStoryPage.getId());
        }
    }

    private static void willDeleteBibleVersion(Context context, Version version){

        BiblePagingEvent currentEvent = BiblePagingEvent.getStickyEvent(context);

        boolean sameChapter = currentEvent.mainChapter.getId().equals(currentEvent.secondaryChapter.getId());

        if(currentEvent.mainChapter.getBook().getVersionId() == (version.getId())){
            UWPreferenceManager.setSelectedBibleChapter(context, (sameChapter) ? -1 : currentEvent.secondaryChapter.getId());
        }
        if(currentEvent.secondaryChapter.getBook().getVersionId() == (version.getId())){
            UWPreferenceManager.setSelectedBibleChapterSecondary(context, (sameChapter) ? -1 : currentEvent.mainChapter.getId());
        }
    }
}
