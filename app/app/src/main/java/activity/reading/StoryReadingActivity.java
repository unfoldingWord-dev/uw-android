package activity.reading;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.List;

import fragments.StoryReadingFragment;
import model.DaoDBHelper;
import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import utils.UWPreferenceDataAccessor;

/**
 * Created by PJ Fechner
 * Activity to handle reading Open Bible Story text
 */
public class StoryReadingActivity extends BaseReadingActivity {
    static private final String TAG = "StoryReadingActivity";

    private StoryReadingFragment readingFragment;
    private StoriesChapter currentChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getChapterLabelText() {
        return (currentChapter != null) ? currentChapter.getTitle() : null;
    }

    @Override
    protected Version getSharingVersion() {
        return (currentChapter == null)? null : currentChapter.getBook().getVersion();
    }

    @Override
    protected void scrolled() {
        boolean isLoaded = loadData();
        if (isLoaded){
            updateToolbar();
        }
    }

    @Override
    protected String getMainVersionText() {
        loadData();
        return  (currentChapter != null)? currentChapter.getBook().getVersion().getTitle() : "";
    }

    @Override
    protected String getSecondaryVersionText() {
        StoryPage page = UWPreferenceDataAccessor.getCurrentStoryPage(getApplicationContext(), true);
        return (page != null)? page.getStoriesChapter().getBook().getVersion().getTitle() : "";
    }

    @Override
    protected boolean loadData() {

        StoryPage page = UWPreferenceDataAccessor.getCurrentStoryPage(getApplicationContext(), false);

        if(page != null){
                currentChapter = page.getStoriesChapter();
                this.book = currentChapter.getBook();
                return true;
        } else {
            currentChapter = null;
            return false;
        }
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
    protected void updateReadingView() {

        if (this.readingFragment == null) {

            Fragment cachedFragment = getSupportFragmentManager().findFragmentByTag("StoryReadingFragment");
            if(cachedFragment != null){
                this.readingFragment = (StoryReadingFragment) cachedFragment;
                readingFragment.update();
            }
            else {
                this.readingFragment = StoryReadingFragment.newInstance();
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
    public boolean toggleHidden() {
        boolean isHidden = super.toggleHidden();
        readingFragment.setBottomBarHidden(isHidden);
        return isHidden;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        readingFragment.setOrientationAsLandscape((newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE));
    }

    @Nullable
    @Override
    protected String getAudioUrl() {
        loadData();
        AudioBook audioBook = this.currentChapter.getBook().getAudioBook();

        if(audioBook != null){
            AudioChapter audioChapter = audioBook.getChapter(Integer.parseInt(this.currentChapter.getNumber()));
            if(audioChapter != null){
                return audioChapter.getSource();
            }
        }

        return null;
    }
}
