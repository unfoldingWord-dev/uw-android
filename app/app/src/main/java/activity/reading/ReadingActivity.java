/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity.reading;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import fragments.BibleReadingFragment;
import model.DaoDBHelper;
import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.BibleChapter;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWFileUtils;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;
import view.ReadingToolbarViewData;

public class ReadingActivity extends BaseReadingActivity {
    static private final String TAG = "ReadingActivity";

    static private final int HIGH_TEXT_SIZE_LIMIT = 20;
    static private final int LOW_TEXT_SIZE_LIMIT = 10;

    private BibleChapter currentChapter;
    private BibleReadingFragment readingFragment;
    private BibleReadingFragment secondaryReadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    protected String getChapterLabelText() {
//        return (currentChapter != null) ? currentChapter.getTitle() : null;
//    }
//
//    @Override
//    protected String getMainVersionText() {
//        updateChapters();
//        return (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
//    }
//
//    @Override
//    protected String getSecondaryVersionText() {
//
//        BibleChapter secondaryChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getApplicationContext(), true);
//        return (secondaryChapter != null)? secondaryChapter.getBook().getVersion().getTitle() : "";
//    }

    @Override
    protected Version getSharingVersion() {
        return (currentChapter == null)? null : currentChapter.getBook().getVersion();
    }

    @Override
    protected ReadingToolbarViewData getToolbarViewData() {
        return null;
    }

    //    @Nullable
//    @Override
//    protected AudioChapter getAudioChapter() {
//        if(currentChapter == null){
//            return null;
//        }
//        updateChapters();
//        AudioBook audioBook = this.currentChapter.getBook().getAudioBook();
//
//        if(audioBook != null){
//            AudioChapter audioChapter = audioBook.getChapter(Integer.parseInt(this.currentChapter.getNumber()));
//            if(audioChapter != null){
//                return audioChapter;
//            }
//        }
//        return null;
//    }

//    @Override
//    protected void scrolled() {
//        updateChapters();
//        updateReadingView();
//    }

    private void updateChapters(){

        currentChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getApplicationContext(), false);

        if (currentChapter != null) {
            this.book = currentChapter.getBook();
        }
    }

    @Override
    protected Project getProject() {
        List<Project> projects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));

        for(Project project : projects){

            if(!project.getUniqueSlug().equalsIgnoreCase("obs")){
                return project;
            }
        }
        return null;
    }

//    @Override
//    protected boolean loadData() {
//
//        updateChapters();
//
//        return (currentChapter != null);
//    }
//
//    @Override
//    protected void updateReadingView() {
//
//        if(currentChapter != null) {
//            if (this.readingFragment == null) {
//                this.readingFragment = createReadingFragment(readingLayout, false);
//            }
//            else {
//                readingFragment.update();
//            }
//            if(this.secondaryReadingFragment == null){
//                this.secondaryReadingFragment = createReadingFragment(secondaryReadingLayout, true);
//            }
//            else{
//                secondaryReadingFragment.update();
//            }
//        }
//    }

    @Override
    protected void makeTextLarger() {

        updateTextSize(UWPreferenceManager.getBibleTextSize(getApplicationContext()) + 1);
    }

    @Override
    protected void makeTextSmaller() {

        updateTextSize(UWPreferenceManager.getBibleTextSize(getApplicationContext()) - 1);

    }

    private void updateTextSize(int textSize){

        UWPreferenceManager.setBibleTextSize(getApplicationContext(), textSize);
        if(readingFragment != null){
            readingFragment.setTextSize(textSize);
        }
        if(secondaryReadingFragment != null){
            secondaryReadingFragment.setTextSize(textSize);
        }

        setTextLargerDisabled((textSize >= HIGH_TEXT_SIZE_LIMIT));
        setTextSmallerDisabled(textSize <= LOW_TEXT_SIZE_LIMIT);
    }

    private BibleReadingFragment createReadingFragment(FrameLayout layout, boolean secondLayout){

        Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag("BibleReadingFragment" + layout.getId());
        if(cachedFragment != null){
            BibleReadingFragment fragment = (BibleReadingFragment) cachedFragment;
            fragment.update();
            return fragment;
        }
        else {
            BibleReadingFragment fragment = BibleReadingFragment.newInstance(secondLayout, UWPreferenceManager.getBibleTextSize(getApplicationContext()));
            getSupportFragmentManager().beginTransaction().add(layout.getId(), fragment, "BibleReadingFragment" + layout.getId()).commit();
            return fragment;
        }
    }

    @Override
    protected boolean toggleDiglot(){

        boolean shouldBeDiglot = super.toggleDiglot();

        LinearLayout.LayoutParams mainReadingParams = (LinearLayout.LayoutParams) readingLayout.getLayoutParams();
        LinearLayout.LayoutParams secondaryReadingParams = (LinearLayout.LayoutParams) secondaryReadingLayout.getLayoutParams();

        mainReadingParams.weight = (shouldBeDiglot)? 0.4f : 1.0f;
        secondaryReadingParams.weight = (shouldBeDiglot)? 0.4f : 0.0f;

        readingLayout.setLayoutParams(mainReadingParams);
        secondaryReadingLayout.setLayoutParams(secondaryReadingParams);
        secondaryReadingLayout.setVisibility((shouldBeDiglot)? View.VISIBLE : View.GONE);
        return shouldBeDiglot;
    }
}

