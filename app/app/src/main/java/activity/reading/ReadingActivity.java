package activity.reading;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.List;

import fragments.BibleReadingFragment;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
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

        updateChapters();
    }

    private void updateChapters(){

        long id = UWPreferenceManager.getSelectedBibleChapter(getApplicationContext());
        if (id != currentChapter.getId()) {
            currentChapter = BibleChapter.getModelForId(id, DaoDBHelper.getDaoSession(getApplicationContext()));
            updateToolbarTitle();
        }
        if(readingFragment != null) {
            readingFragment.scrollToCurrentPage();
        }
        if(secondaryReadingFragment != null) {
            secondaryReadingFragment.scrollToCurrentPage();
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

        long chapterId = UWPreferenceManager.getSelectedBibleChapter(getApplicationContext());

        if (chapterId > -1) {
            currentChapter = BibleChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getApplicationContext()));
            return true;
        } else {
            currentChapter = null;
            return false;
        }
    }

    @Override
    protected void updateReadingView() {

        updateChapters();
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
}

