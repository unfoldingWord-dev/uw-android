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

import fragments.BibleReadingFragment;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceDataAccessor;

public class ReadingActivity extends BaseReadingActivity {
    static private final String TAG = "ReadingActivity";

    private BibleChapter currentChapter;
    private BibleReadingFragment readingFragment;
    private BibleReadingFragment secondaryReadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getChapterLabelText() {
        return (currentChapter != null) ? currentChapter.getTitle() : null;
    }

    @Override
    protected String getMainVersionText() {
        updateChapters();
        return (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
    }

    @Override
    protected String getSecondaryVersionText() {

        BibleChapter secondaryChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getApplicationContext(), true);
        return (secondaryChapter != null)? secondaryChapter.getBook().getVersion().getTitle() : "";
    }

    @Override
    protected Version getSharingVersion() {
        return (currentChapter == null)? null : currentChapter.getBook().getVersion();
    }

    @Override
    protected void scrolled() {
        updateChapters();
        updateReadingView();
    }

    private void updateChapters(){

        currentChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getApplicationContext(), false);

        if (currentChapter != null) {
            this.version = currentChapter.getBook().getVersion();
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

    @Override
    protected boolean loadData() {

        updateChapters();

        return (currentChapter != null);
    }

    @Override
    protected void updateReadingView() {

        if(currentChapter != null) {
            if (this.readingFragment == null) {
                this.readingFragment = createReadingFragment(readingLayout, false);
            }
            else {
                readingFragment.update();
            }
            if(this.secondaryReadingFragment == null){
                this.secondaryReadingFragment = createReadingFragment(secondaryReadingLayout, true);
            }
            else{
                secondaryReadingFragment.update();
            }
        }
    }

    private BibleReadingFragment createReadingFragment(FrameLayout layout, boolean secondLayout){

        Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag("BibleReadingFragment" + layout.getId());
        if(cachedFragment != null){
            BibleReadingFragment fragment = (BibleReadingFragment) cachedFragment;
            fragment.update();
            return fragment;
        }
        else {
            BibleReadingFragment fragment = BibleReadingFragment.newInstance(secondLayout);
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

