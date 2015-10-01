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
import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import utils.UWFileUtils;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceManager;

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



    @Override
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
    protected Uri getAudioUri() {
        loadData();
        AudioBook audioBook = this.currentChapter.getBook().getAudioBook();

        if(audioBook != null){
            AudioChapter audioChapter = audioBook.getChapter(Integer.parseInt(this.currentChapter.getNumber()));
            if(audioChapter != null){
                File audioFile = UWFileUtils.loadSourceFile(audioChapter.getAudioUrl(), getApplicationContext());

                if(audioFile != null){
                    return Uri.fromFile(audioFile);
                }
            }
        }

        return null;
    }
}
