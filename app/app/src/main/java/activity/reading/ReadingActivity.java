/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity.reading;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import eventbusmodels.BiblePagingEvent;
import fragments.Reading.BibleReadingFragment;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceManager;
import view.ReadingToolbarViewBibleModel;
import view.ReadingToolbarViewData;

public class ReadingActivity extends BaseReadingActivity {
//    static private final String TAG = "ReadingActivity";

    static private final int HIGH_TEXT_SIZE_LIMIT = 20;
    static private final int LOW_TEXT_SIZE_LIMIT = 10;

    private BibleReadingFragment readingFragment;
    private BibleReadingFragment secondaryReadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void update() {
        super.update();
        updateReadingView();
    }

    @Override
    protected Version getSharingVersion() {
        BibleChapter activeChapter = getActiveChapter();
        return(activeChapter != null)? activeChapter.getBook().getVersion() : null;
    }

    private BibleChapter getActiveChapter(){
        return BiblePagingEvent.getStickyEvent(getApplicationContext()).mainChapter;
    }

    @Override
    protected ReadingToolbarViewData getToolbarViewData() {
        return new ReadingToolbarViewBibleModel(getApplicationContext());
    }

    @Override
    protected Book getBook() {
        BibleChapter activeChapter = getActiveChapter();
        return (activeChapter != null)? activeChapter.getBook() : null;
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

    protected void updateReadingView() {

        if(getActiveChapter() != null) {
            if (this.readingFragment == null) {
                this.readingFragment = createReadingFragment(readingLayout, false);
            } else {
                readingFragment.update();
            }
            if (this.secondaryReadingFragment == null) {
                this.secondaryReadingFragment = createReadingFragment(secondaryReadingLayout, true);
            } else {
                secondaryReadingFragment.update();
            }
        }
    }

    @Override
    protected int getCurrentTextSizeIndex() {
        return UWPreferenceManager.getBibleTextSizeIndex(getApplicationContext());
    }

    @Override
    protected List<String> getTextSizeOptions() {
        return UWPreferenceManager.BIBLE_TEXT_SIZES;
    }

    @Override
    protected void setNewTextSize(int index) {
        updateTextSize(index);
    }

//    @Override
//    protected void makeTextLarger() {
//
//        updateTextSize(UWPreferenceManager.getBibleTextSize(getApplicationContext()) + 1);
//    }
//
//    @Override
//    protected void makeTextSmaller() {
//
//        updateTextSize(UWPreferenceManager.getBibleTextSize(getApplicationContext()) - 1);
//
//    }

    private void updateTextSize(int index){

        UWPreferenceManager.setBibleTextSize(getApplicationContext(), index);
        int textSize = Integer.parseInt(UWPreferenceManager.getBibleTextSize(getApplicationContext()));
        if(readingFragment != null){
            readingFragment.setTextSize(textSize);
        }
        if(secondaryReadingFragment != null){
            secondaryReadingFragment.setTextSize(textSize);
        }

//        setTextLargerDisabled((textSize >= HIGH_TEXT_SIZE_LIMIT));
//        setTextSmallerDisabled(textSize <= LOW_TEXT_SIZE_LIMIT);
    }

    private BibleReadingFragment createReadingFragment(FrameLayout layout, boolean secondLayout){

        Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag("BibleReadingFragment" + layout.getId());
        if(cachedFragment != null){
            BibleReadingFragment fragment = (BibleReadingFragment) cachedFragment;
            fragment.update();
            return fragment;
        }
        else {
            BibleReadingFragment fragment = BibleReadingFragment.newInstance(secondLayout, Integer.parseInt(UWPreferenceManager.getBibleTextSize(getApplicationContext())));
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

