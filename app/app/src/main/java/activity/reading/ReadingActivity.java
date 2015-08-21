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
import utils.UWPreferenceDataManager;

/**
 * Created by PJ Fechner on 5/12/14.
 * Activity for reading Bible Chapters
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
    public boolean toggleNavBar() {
        boolean isHidden = super.toggleNavBar();

        readingFragment.setBottomBarHidden(isHidden);
        secondaryReadingFragment.setBottomBarHidden(isHidden);
        return isHidden;
    }

    @Override
    protected void scrolled() {
        updateChapters();
        updateReadingView();
    }

    private void updateChapters(){

        currentChapter = UWPreferenceDataManager.getCurrentBibleChapter(getApplicationContext(), false);

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
    protected void toggleDiglot(){

        LinearLayout.LayoutParams mainReadingParams = (LinearLayout.LayoutParams) readingLayout.getLayoutParams();
        LinearLayout.LayoutParams secondaryReadingParams = (LinearLayout.LayoutParams) secondaryReadingLayout.getLayoutParams();
        boolean isDiglot = (secondaryReadingParams.weight > .1f);

        mainReadingParams.weight = (isDiglot)? 1.0f : 0.4f;
        secondaryReadingParams.weight = (isDiglot)? 0.0f : 0.4f;

        readingLayout.setLayoutParams(mainReadingParams);
        secondaryReadingLayout.setLayoutParams(secondaryReadingParams);
        secondaryReadingLayout.setVisibility((isDiglot)? View.GONE : View.VISIBLE);
    }
}

