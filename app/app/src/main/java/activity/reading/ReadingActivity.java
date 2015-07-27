package activity.reading;


import android.app.Dialog;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.bookSelection.BookSelectionActivity;
import activity.bookSelection.VersionSelectionActivity;
import fragments.BibleReadingFragment;
import fragments.BooksFragment;
import fragments.ChapterSelectionFragment;
import fragments.ChaptersFragment;
import fragments.ReadingFragmentListener;
import fragments.VersionSelectionFragment;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;
import view.ViewHelper;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingActivity extends BaseReadingActivity {
    static private final String TAG = "ReadingActivity";

    private BibleChapter currentChapter;

    private BibleReadingFragment readingFragment;

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
    protected Project getProject() {
        List<Project> projects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));

        for(Project project : projects){

            if(!project.getSlug().equalsIgnoreCase("obs")){
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

        if(currentChapter != null) {
            if (this.readingFragment == null) {
                this.readingFragment = BibleReadingFragment.newInstance(currentChapter.getBook());
                getSupportFragmentManager().beginTransaction().add(readingLayout.getId(), readingFragment, "BibleReadingFragment").commit();
            } else {
                readingFragment.updateReadingFragment(currentChapter.getBook());
            }
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

