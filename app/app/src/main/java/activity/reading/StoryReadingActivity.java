package activity.reading;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.List;

import fragments.StoryReadingFragment;
import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import utils.UWFileUtils;
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
    private StoriesChapter currentChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Version getSharingVersion() {
        return (currentChapter == null)? null : currentChapter.getBook().getVersion();
    }

    @Override
    protected void update() {
        super.update();
        updateReadingView();
    }

    @Override
    protected ReadingToolbarViewData getToolbarViewData() {
        return new ReadingToolbarViewStoriesModel(getApplicationContext());
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
    protected void makeTextLarger() {

        updateTextSize(UWPreferenceManager.getStoriesTextSize(getApplicationContext()) + 1);
    }

    @Override
    protected void makeTextSmaller() {

        updateTextSize(UWPreferenceManager.getStoriesTextSize(getApplicationContext()) - 1);
    }

    private void updateTextSize(int textSize){

        UWPreferenceManager.setStoriesTextSize(getApplicationContext(), textSize);
        if(readingFragment != null){
            readingFragment.setTextSize(textSize);
        }
        setTextLargerDisabled((textSize >= HIGH_TEXT_SIZE_LIMIT));
        setTextSmallerDisabled(textSize <= LOW_TEXT_SIZE_LIMIT);
    }

    protected void updateReadingView() {

        if (this.readingFragment == null) {

            Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag("StoryReadingFragment");
            if(cachedFragment != null){
                this.readingFragment = (StoryReadingFragment) cachedFragment;
                readingFragment.update();
            }
            else {
                this.readingFragment = StoryReadingFragment.newInstance(UWPreferenceManager.getStoriesTextSize(getApplicationContext()));
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
