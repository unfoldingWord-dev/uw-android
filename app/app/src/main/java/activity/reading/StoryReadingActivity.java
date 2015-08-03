package activity.reading;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

import fragments.StoryReadingFragment;
import model.DaoDBHelper;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import utils.UWPreferenceManager;

public class StoryReadingActivity extends BaseReadingActivity {

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
    protected String getVersionText() {
        return (currentChapter != null)? currentChapter.getBook().getVersion().getUniqueSlug() : "Select Version";
    }

    @Override
    protected Version getVersion() {
        if(currentChapter == null){
            return null;
        }
        else{
            return currentChapter.getBook().getVersion();
        }
    }

    @Override
    protected void scrolled() {
        long id = UWPreferenceManager.getSelectedStoryPage(getApplicationContext());
        StoryPage page = DaoDBHelper.getDaoSession(getApplicationContext()).getStoryPageDao().loadDeep(id);
        if (page.getStoryChapterId() != currentChapter.getId()) {
            currentChapter = page.getStoriesChapter();
            updateToolbarTitle();
        }
    }

    @Override
    protected boolean loadData() {

        long pageId = UWPreferenceManager.getSelectedStoryPage(getApplicationContext());

        if (pageId > -1) {
            StoryPage page = DaoDBHelper.getDaoSession(getApplicationContext()).getStoryPageDao().loadDeep(pageId);
            if(page != null){
                currentChapter = page.getStoriesChapter();
                return true;
            }
            else{
                return false;
            }
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
                readingFragment.update(currentChapter);
            }
            else {
                this.readingFragment = StoryReadingFragment.newInstance(currentChapter);
                getSupportFragmentManager().beginTransaction().add(readingLayout.getId(), readingFragment, "StoryReadingFragment").commit();
            }
        }
        else {
            readingFragment.update(currentChapter);
        }
    }
}
