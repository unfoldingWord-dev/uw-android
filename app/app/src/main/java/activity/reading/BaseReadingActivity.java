package activity.reading;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.bookSelection.BookSelectionActivity;
import activity.bookSelection.VersionSelectionActivity;
import adapters.ReadingScrollNotifications;
import fragments.BooksFragment;
import fragments.ChapterSelectionFragment;
import fragments.ChaptersFragment;
import fragments.CheckingLevelFragment;
import fragments.ReadingFragmentListener;
import fragments.VersionSelectionFragment;
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
    abstract protected void updateReadingView();
    abstract protected Version getVersion();
    abstract protected Project getProject();
    abstract protected void scrolled();


    private BroadcastReceiver receiver;

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
        registerReceivers();
    }

    private void registerReceivers(){
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(ReadingScrollNotifications.SCROLLED_PAGE)) {
//                    if(intent.getExtras().containsKey(ReadingScrollNotifications.BIBLE_CHAPTER_PARAM)
//                            || intent.getExtras().containsKey(ReadingScrollNotifications.STORY_PAGE_PARAM)) {
                        scrolled();
//                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReadingScrollNotifications.SCROLLED_PAGE);
        getApplicationContext().registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar(false);
        updateViews();
    }

    @Override
    protected void onPause() {

        if(receiver != null) {
            getApplicationContext().unregisterReceiver(receiver);
        }
        receiver = null;
        super.onPause();

    }

    protected void updateViews(){

        updateToolbar();
        updateReadingView();
    }

    protected void updateToolbar() {

        int checkingLevelImage = getCheckingLevelImage();
        getToolbar().setCheckingLevelImage((checkingLevelImage > -1) ? checkingLevelImage : -1);

        updateToolbarTitle();
    }

    protected void updateToolbarTitle(){
        String title = getChapterLabelText();
        getToolbar().setTitle(title, true);
        getToolbar().setRightButtonText(getVersionText(), true);
    }

    private void setNoVersionSelectedVisibility(boolean visible){

        findViewById(R.id.reading_error_text_view).setVisibility((visible) ? View.VISIBLE : View.GONE);
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
    public void versionWasSelected(Version version) {
        UWPreferenceManager.selectedVersion(getApplicationContext(), version);
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

    protected int getCheckingLevelImage() {

        Version currentVersion = getVersion();
        if(currentVersion != null) {
            return ViewHelper.getCheckingLevelImage(Integer.parseInt(currentVersion.getStatusCheckingLevel()));
        }
        else{
            return -1;
        }
    }
}
