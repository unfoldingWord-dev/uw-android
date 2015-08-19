package activity.reading;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.readingSelection.BookSelectionActivity;
import activity.readingSelection.VersionSelectionActivity;
import adapters.ReadingScrollNotifications;
import fragments.ChapterSelectionFragment;
import fragments.ChapterSelectionFragmentListener;
import fragments.VersionInfoFragment;
import fragments.ReadingFragmentListener;
import fragments.StoryChaptersFragment;
import fragments.VersionSelectionFragment;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;

/**
 * Created by PJ Fechner on 5/12/14.
 * abstract activity to handle most of the logic involved in the different reading activites.
 */
public abstract class BaseReadingActivity extends UWBaseActivity implements
        VersionSelectionFragment.VersionSelectionFragmentListener,
        ChapterSelectionFragmentListener,
        ReadingFragmentListener
{
    private static final String TAG = "ReadingActivity";

    private static final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    private static final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";
    protected static final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";

    protected FrameLayout readingLayout;
    protected FrameLayout secondaryReadingLayout;
    protected View errorView;
    protected Version version;

    private BroadcastReceiver receiver;


    //region Abstract Methods

    /**
     * should load any data necessary for operation
     * @return Whether the data could be loaded
     */
    abstract protected boolean loadData();

    /**
     * The label text for the chapter's label
     * @return label text, or null if the text should not be shown
     */
    @Nullable
    abstract protected String getChapterLabelText();

    /**
     * should update the reading view with the most current data
     */
    abstract protected void updateReadingView();

    /**
     * @return The project for the current activity
     */
    abstract protected Project getProject();

    /**
     * do any action required when the user scrolls
     */
    abstract protected void scrolled();

    /**
     * Toggle the diglot interface
     */
    abstract protected void toggleDiglot();

    //endregion

    //region Activity Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        if(!loadData()){
            versionSelectionButtonClicked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar(false);
        setupViews();

        boolean dataIsLoaded = loadData();
        setupReadingVisibility(dataIsLoaded);
        if (dataIsLoaded){
            getToolbar().setRightImageResource(R.drawable.diglot_icon);
            updateViews();
        }
        else{
            getToolbar().setRightImageVisible(false);
        }
        registerReceivers();
    }

    @Override
    protected void onPause() {

        if(receiver != null) {
            getApplicationContext().unregisterReceiver(receiver);
        }
        receiver = null;
        super.onPause();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

    @Override
    public void onBackPressed(boolean isSharing) {
        if(!isSharing) {
            super.onBackPressed(isSharing);
        }
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }

    //endregion

    //region accessors

    /**
     * @return the current version being used
     */
    protected Version getVersion(){
        return this.version;
    }

    //endregion

    //region userInteraction

    public void versionSelectionButtonClicked(View view) {

        versionSelectionButtonClicked(false);
    }

    //endregion

    //region setup

    private void setupReadingVisibility(boolean visible){

        if (visible) {
            readingLayout.setVisibility(View.VISIBLE);
            setNoVersionSelectedVisibility(false);
        }
        else{
            readingLayout.setVisibility(View.GONE);
            setNoVersionSelectedVisibility(true);
        }
    }

//    private void setAnimated(){
//        if(android.os.Build.VERSION.SDK_INT > 15) {
//            LayoutTransition transition = new LayoutTransition();
//            transition.enableTransitionType(LayoutTransition.CHANGING);
//            transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
//            transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
//            transition.enableTransitionType(LayoutTransition.APPEARING);
//            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
//            transition.setDuration(300);
//            ((LinearLayout) findViewById(R.id.reading_layout)).setLayoutTransition(transition);
//        }
//    }

    protected void setupViews(){
        readingLayout = (FrameLayout) findViewById(R.id.reading_fragment_frame);
        secondaryReadingLayout = (FrameLayout) findViewById(R.id.secondary_reading_fragment_frame);
        errorView = findViewById(R.id.no_version_layout);
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

    //endregion

    //region updating Views

    protected void updateViews(){

        updateToolbar();
        updateReadingView();
    }

    protected void updateToolbar() {
        updateToolbarTitle();
    }

    protected void updateToolbarTitle(){
        String title = getChapterLabelText();
        getToolbar().setTitle(title, true);
    }

    //endregion

    //region viewChanging

    /**
     * will show/hide the view needed if there is no chosen version
     * @param visible whether the view should be visible
     */
    private void setNoVersionSelectedVisibility(boolean visible){
        findViewById(R.id.no_version_layout).setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    //endregion

    //region navigation handling

    /**
     * will go to version selection
     * @param isSecondVersion whether the user is requesting to change the second version of the diglot view
     */
    protected void versionSelectionButtonClicked(boolean isSecondVersion){

//        if(isTablet()){
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//            VersionSelectionFragment fragment = VersionSelectionFragment.newInstance(projectId, true);
//            fragment.show(ft, VERSION_FRAGMENT_ID);
//        }
//        else {
            startActivity(new Intent(this, VersionSelectionActivity.class).putExtra(
                    VersionSelectionActivity.PROJECT_PARAM, getProject())
                    .putExtra(VersionSelectionActivity.IS_SECOND_VERSION_PARAM, isSecondVersion));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
//        }
    }

    /**
     * will go to chapter selection
     */
    private void goToChapterActivity(){

        if(isTablet()){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            getChapterFragment().show(ft, CHAPTER_SELECTION_FRAGMENT_ID);
        }
        else {
            startActivity(new Intent(getApplicationContext(), BookSelectionActivity.class).putExtra(BookSelectionActivity.PROJECT_PARAM, getProject()));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
        }
    }

    /**
     * will show the checking level fragment for the passed version
     * @param version version for which to show the info fragment
     */
    private void goToCheckingLevelView(Version version){
        VersionInfoFragment fragment = VersionInfoFragment.createFragment(version);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fragment.show(ft, CHECKING_LEVEL_FRAGMENT_ID);
    }

    //endregion

    //region helper methods

    private DialogFragment getChapterFragment(){

        boolean isStories = getProject().getUniqueSlug().contains("obs");
        if(isStories){
            return StoryChaptersFragment.newInstance(true);
        }
        else{
            return ChapterSelectionFragment.newInstance(true);
        }
    }

    /**
     * @return whether current device is a table
     */
    private boolean isTablet(){

        int screen_density = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        if (screen_density == Configuration.SCREENLAYOUT_SIZE_LARGE || screen_density == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     *
     * @param fragmentId id of fragment to be removed
     */
    private void removeFragment(String fragmentId){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag(fragmentId);

        if (previous != null) {
            ft.remove(previous);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    //endregion

    //region toolbar actions

    @Override
    public void rightButtonClicked() {
        toggleDiglot();
    }

    @Override
    public void centerButtonClicked() {
        goToChapterActivity();
    }

    //endregion

    //region ReadingFragmentListener

    @Override
    public void clickedChooseVersion(boolean isSecondReadingView) {
        versionSelectionButtonClicked(isSecondReadingView);
    }

    @Override
    public void showCheckingLevel(Version version) {
        goToCheckingLevelView(version);
    }

    @Override
    public boolean toggleNavBar() {
        boolean isHidden = getToolbar().toggleHidden();
        return isHidden;
    }

    //endregion

    //region VersionSelectionFragmentListener

    @Override
    public void versionWasSelected(Version version, boolean isSecondVersion) {
        UWPreferenceManager.selectedVersion(getApplicationContext(), version, isSecondVersion);
        removeFragment(VERSION_FRAGMENT_ID);
    }

    //endregion

    //region ChapterSelectionFragmentListener

    @Override
    public void chapterWasSelected() {
        removeFragment(CHAPTER_SELECTION_FRAGMENT_ID);
        loadData();
        updateViews();
    }

    //endregion
}
