/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */
package activity.reading;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

import eventbusmodels.StoriesPagingEvent;
import fragments.Reading.StoryReadingFragment;
import model.DaoDBHelper;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import singletons.UWAudioPlayer;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceManager;
import view.ReadingToolbarViewData;
import view.ReadingToolbarViewStoriesModel;

/**
 * Created by PJ Fechner
 * Activity to handle reading Open Bible Story text
 */
public class StoryReadingActivity extends BaseReadingActivity {
    static private final String TAG = "StoryReadingActivity";

    static private final int HIGH_TEXT_SIZE_LIMIT = 25;
    static private final int LOW_TEXT_SIZE_LIMIT = 15;

    private StoryReadingFragment readingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Version getSharingVersion() {

        StoryPage page = getMainPage();
        if(page != null){
            return page.getStoriesChapter().getBook().getVersion();
        }
        else{
            return null;
        }
    }

    @Override
    protected void update() {
        super.update();
        updateReadingView();

        StoryPage page = getMainPage();
        if(page != null){
            UWAudioPlayer.getInstance(getApplicationContext()).prepareAudio(page);
        }
    }

    @Override
    protected ReadingToolbarViewData getToolbarViewData() {
        return new ReadingToolbarViewStoriesModel(getApplicationContext());
    }

    @Override
    protected Book getBook() {

        StoryPage page = getMainPage();
        return(page != null)? page.getStoriesChapter().getBook() : null;
    }

    private StoryPage getMainPage(){
        return StoriesPagingEvent.getStickyEvent(getApplicationContext()).mainStoryPage;
    }

    @Override
    protected Project getProject() {
        List<Project> projects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));

        for(Project project : projects){
            if(project.getUniqueSlug().equalsIgnoreCase("obs")){
                return project;
            }
        }
        return null;
    }

    @Override
    protected int getCurrentTextSizeIndex() {
        return UWPreferenceManager.getStoriesTextSizeIndex(getApplicationContext());
    }

    @Override
    protected List<String> getTextSizeOptions() {
        return UWPreferenceManager.STORIES_TEXT_SIZES;
    }

    @Override
    protected void setNewTextSize(int index) {
        updateTextSize(index);
    }

//    @Override
//    protected void makeTextLarger() {
//
//        updateTextSize(UWPreferenceManager.getStoriesTextSize(getApplicationContext()) + 1);
//    }
//
//    @Override
//    protected void makeTextSmaller() {
//
//        updateTextSize(UWPreferenceManager.getStoriesTextSize(getApplicationContext()) - 1);
//    }

    private void updateTextSize(int index){

        UWPreferenceManager.setStoriesTextSize(getApplicationContext(), index);
        int textSize = Integer.parseInt(UWPreferenceManager.getStoriesTextSize(getApplicationContext()));
        if(readingFragment != null){
            readingFragment.setTextSize(textSize);
        }
//        setTextLargerDisabled((textSize >= HIGH_TEXT_SIZE_LIMIT));
//        setTextSmallerDisabled(textSize <= LOW_TEXT_SIZE_LIMIT);
    }

    protected void updateReadingView() {

        if (this.readingFragment == null) {

            Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag("StoryReadingFragment");
            if(cachedFragment != null){
                this.readingFragment = (StoryReadingFragment) cachedFragment;
                readingFragment.update();
            }
            else {
                this.readingFragment = StoryReadingFragment.newInstance(Integer.parseInt(UWPreferenceManager.getStoriesTextSize(getApplicationContext())));
                getSupportFragmentManager().beginTransaction().add(readingLayout.getId(), readingFragment, "StoryReadingFragment").commit();
            }
        }
        else {
            readingFragment.update();
        }
    }

    @Override
    protected boolean toggleDiglot() {

        boolean isDiglot = super.toggleDiglot();
        readingFragment.setDiglotShowing(isDiglot);
        return isDiglot;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        readingFragment.setOrientationAsLandscape((newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE));
    }
}
