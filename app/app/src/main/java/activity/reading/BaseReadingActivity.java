package activity.reading;


import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import fragments.CheckingLevelFragment;
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
public abstract class BaseReadingActivity extends UWBaseActivity implements
        VersionSelectionFragment.VersionSelectionFragmentListener,
        ChapterSelectionFragment.ChapterSelectionListener,
        BooksFragment.BooksFragmentListener,
        ChaptersFragment.ChaptersFragmentListener,
        ReadingFragmentListener
{
    private static final String TAG = "ReadingActivity";

    private static final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    private static final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";
    protected static final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";

    protected FrameLayout readingLayout;
    protected TextView errorTextView;

    abstract protected boolean loadData();
    abstract protected String getChapterLabelText();
    abstract protected String getVersionText();
    abstract protected int getCheckingLevelImage();
    abstract protected void updateReadingView();
    abstract protected Version getVersion();
    abstract protected Project getProject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        if(!loadData()){
            goToVersionSelection();
        }
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }

    protected void setupViews(){
        readingLayout = (FrameLayout) findViewById(R.id.reading_fragment_frame);
        errorTextView = (TextView) findViewById(R.id.reading_error_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupToolbar(false);
        setupViews();

        if (loadData()) {
            readingLayout.setVisibility(View.VISIBLE);
            setNoVersionSelectedVisibility(false);
        }
        else{
            readingLayout.setVisibility(View.GONE);
            setNoVersionSelectedVisibility(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar(false);
        updateViews();
    }

    private void updateViews(){

        updateToolbar();
        updateReadingView();
    }

    private void updateToolbar() {

        int checkingLevelImage = getCheckingLevelImage();
        getToolbar().setCheckingLevelImage((checkingLevelImage > -1) ? checkingLevelImage : -1);

        String title = getChapterLabelText();
        getToolbar().setTitle(title, true);
        getToolbar().setRightButtonText(getVersionText(), true);
    }

    protected int getCheckingLevelImage(int level){

        switch (level){
            case 2:{
                return R.drawable.level_two;
            }
            case 3:{
                return R.drawable.level_three;
            }
            default:{
                return R.drawable.level_one;
            }
        }
    }

    private void setNoVersionSelectedVisibility(boolean visible){
        findViewById(R.id.reading_error_text_view).setVisibility((visible)? View.VISIBLE : View.GONE);
    }

    private boolean isTablet(){

        int screen_density = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        if (screen_density == Configuration.SCREENLAYOUT_SIZE_LARGE || screen_density == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return true;
        }
        else{
            return false;
        }
    }

    protected void goToVersionSelection(){

//        if(isTablet()){
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//            VersionSelectionFragment fragment = VersionSelectionFragment.newInstance(projectId, true);
//            fragment.show(ft, VERSION_FRAGMENT_ID);
//        }
//        else {
            startActivity(new Intent(this, VersionSelectionActivity.class).putExtra(
                    VersionSelectionActivity.PROJECT_PARAM, getProject()));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
//        }
    }

    private void goToChapterActivity(){

        if(isTablet()){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ChapterSelectionFragment fragment = ChapterSelectionFragment.newInstance(true);
            fragment.show(ft, CHAPTER_SELECTION_FRAGMENT_ID);
        }
        else {
            startActivity(new Intent(getApplicationContext(), BookSelectionActivity.class).putExtra(BookSelectionActivity.PROJECT_PARAM, getProject()));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
        }
    }

    @Override
    public void toggleNavBar() {
        getToolbar().toggleHidden();
    }

    @Override
    public void rightButtonClicked() {
        goToVersionSelection();
    }

    @Override
    public void centerButtonClicked() {
        goToChapterActivity();
    }

    @Override
    public void checkingLevelButtonClicked() {
        goToCheckingLevelView();
    }

    @Override
    public void rowWasSelected() {
        removeFragment(VERSION_FRAGMENT_ID);
    }

    @Override
    public void selectionFragmentChoseChapter() {
        removeFragment(CHAPTER_SELECTION_FRAGMENT_ID);
    }

    private void removeFragment(String fragmentId){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag(fragmentId);

        if (previous != null) {
            ft.remove(previous);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void bookWasSelected(String chapterUid) {

    }

    @Override
    public void chapterWasSelected() {

    }

    private void goToCheckingLevelView(){
        CheckingLevelFragment fragment = CheckingLevelFragment.createFragment(getVersion());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fragment.show(ft, CHECKING_LEVEL_FRAGMENT_ID);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }
        else {

        }
    }
}
