package activity.reading;

import android.os.Bundle;

import java.util.List;

import fragments.StoryReadingFragment;
import model.DaoDBHelper;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;
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
        return (currentChapter != null)? currentChapter.getBook().getVersion().getSlug() : "Select Version";
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
    protected boolean loadData() {

        long chapterId = UWPreferenceManager.getSelectedStoryChapter(getApplicationContext());

        if (chapterId > -1) {
            currentChapter = StoriesChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getApplicationContext()));
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

            if(project.getSlug().equalsIgnoreCase("obs")){
                return project;
            }
        }
        return null;
    }

    @Override
    protected void updateReadingView() {

        if (this.readingFragment == null) {
            this.readingFragment = StoryReadingFragment.newInstance(currentChapter);
            getSupportFragmentManager().beginTransaction().add(readingLayout.getId(), readingFragment, "StoryReadingFragment").commit();
        } else {
            readingFragment.updateReadingFragment(currentChapter);
        }
    }

    @Override
    protected int getCheckingLevelImage() {

        if(currentChapter != null) {
            return getCheckingLevelImage(Integer.parseInt(currentChapter.getBook().getVersion().getStatusCheckingLevel()));
        }
        else{
            return -1;
        }
    }
}
