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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.readingSelection.BookSelectionActivity;
import activity.readingSelection.VersionSelectionActivity;
import fragments.ChapterSelectionFragment;
import fragments.ChapterSelectionFragmentListener;
import fragments.ReadingFragmentListener;
import fragments.StoryChaptersFragment;
import fragments.VersionInfoFragment;
import fragments.VersionSelectionFragment;
import model.SharingHelper;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceDataManager;
import view.ReadingTabBar;
import view.UWReadingToolbarViewGroup;
import view.UWTabBar;

/**
 * Created by PJ Fechner on 5/12/14.
 * abstract activity to handle most of the logic involved in the different reading activites.
 */
public abstract class BaseReadingActivity extends UWBaseActivity implements
        VersionSelectionFragment.VersionSelectionFragmentListener,
        ChapterSelectionFragmentListener,
        ReadingFragmentListener,
        UWReadingToolbarViewGroup.UWReadingToolbarListener
{
    private static final String TAG = "ReadingActivity";

    private static final String IS_HIDDEN_PARAM = "IS_HIDDEN_PARAM";
    private static final String IS_DIGLOT_PARAM = "IS_DIGLOT_PARAM";

    public static final String SCROLLED_PAGE = "SCROLLED_PAGE"    ;

    private static final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    private static final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";
    protected static final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";


    protected FrameLayout readingLayout;
    protected FrameLayout secondaryReadingLayout;
    protected View errorView;
    protected Version version;
    private ReadingTabBar tabBar;

    private UWReadingToolbarViewGroup readingToolbar;
    private BroadcastReceiver receiver;

    private boolean isMini = false;
    private boolean isDiglot = false;

    //region Abstract Methods

    /**
     * should load any data necessary for operation
     * @return Whether the data could be loaded
     */
    abstract protected boolean loadData();

    /**
     * The text for the chapter's label
     * @return label text, or null if the text should not be shown
     */
    @Nullable
    abstract protected String getChapterLabelText();

    /**
     * should update the reading view with the current data
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
     * @return Version to be shared
     */
    abstract protected Version getSharingVersion();

    /**
     * @return Text for main version label
     */
    abstract protected String getMainVersionText();

    /**
     * Text for secondary version label
     * @return
     */
    abstract protected String getSecondaryVersionText();
    //endregion

    //region Activity Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        if(!loadData()){
            versionSelectionButtonClicked(false);
        }
        setupTabBar();
        setupToolbar();
    }

    public void setupToolbar(){
        readingToolbar = new UWReadingToolbarViewGroup((Toolbar) findViewById(R.id.toolbar), this, this);
        updateToolbar();
    }

    protected boolean toggleDiglot(){
        isDiglot = !isDiglot;
        return isDiglot;
    }

    @Override
    public void backButtonClicked() {
        handleBack();
    }

    @Override
    public void chaptersButtonClicked() {
        goToChapterActivity();
    }

    @Override
    public void mainVersionButtonClicked() {
        versionSelectionButtonClicked(false);
    }

    @Override
    public void secondaryVersionButtonClicked() {
        versionSelectionButtonClicked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar();
        setupViews();

        boolean dataIsLoaded = loadData();
        setupReadingVisibility(dataIsLoaded);
        if (dataIsLoaded){
//            getToolbar().setRightImageResource(R.drawable.diglot_icon);
            updateViews();
        }
        else{
//            getToolbar().setRightImageVisible(false);
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

    private void setupTabBar(){

        ViewGroup baseAudioPlayerLayout = (RelativeLayout) findViewById(R.id.audio_player);
        baseAudioPlayerLayout.setVisibility(View.GONE);

        int[] images = {R.drawable.audio_normal, R.drawable.video_normal, R.drawable.font_normal, R.drawable.diglot_normal, R.drawable.share_normal};

        tabBar = new ReadingTabBar(getApplicationContext(), images, (ViewGroup) findViewById(R.id.tab_bar_view), baseAudioPlayerLayout, new UWTabBar.BottomBarListener() {
            @Override
            public void buttonPressedAtIndex(int index) {
                tabBarPressed(index);
            }
        });
    }

    private void tabBarPressed(int index){

        switch (index){
            case 0:{
                tabBar.showAudioPlayer();
                View view = findViewById(R.id.bottom_menu_layout);
                view.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                break;
            }
            case 1:{
                //video
                break;
            }
            case 2:{
                tabBar.showTextSizeChooser();
                break;
            }
            case 3:{
                readingToolbar.setHasTwoVersions(toggleDiglot());
                updateToolbar();
                break;
            }
            case 4:{
                shareVersion();
                break;
            }
        }
    }

    private void shareVersion(){

        Version sharingVersion = getSharingVersion();
        if(sharingVersion != null) {
            goToNewActivity(SharingHelper.getIntentForSharing(getApplicationContext(), sharingVersion));
        }
    }

    protected void setupViews(){
        readingLayout = (FrameLayout) findViewById(R.id.reading_fragment_frame);
        secondaryReadingLayout = (FrameLayout) findViewById(R.id.secondary_reading_fragment_frame);
        errorView = findViewById(R.id.no_version_layout);
    }

    private void registerReceivers(){

        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(SCROLLED_PAGE)) {
//                    if(intent.getExtras().containsKey(ReadingScrollNotifications.BIBLE_CHAPTER_PARAM)
//                            || intent.getExtras().containsKey(ReadingScrollNotifications.STORY_PAGE_PARAM)) {
                    scrolled();
                    updateToolbar();
//                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(SCROLLED_PAGE);
        getApplicationContext().registerReceiver(receiver, filter);
    }

    //endregion

    //region updating Views

    protected void updateViews(){

        updateToolbar();
        updateReadingView();
    }

    protected void updateToolbar() {
        readingToolbar.setChapterText(getChapterLabelText());
        readingToolbar.setMainVersionText(getMainVersionText());
        readingToolbar.setSecondaryVersionText(getSecondaryVersionText());
        readingToolbar.setViewState(isMini, isDiglot);
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
    public boolean toggleHidden() {
        isMini =  readingToolbar.toggleIsMinni();
        tabBar.setHidden(isMini);
        return isMini;
    }

    //endregion

    //region VersionSelectionFragmentListener

    @Override
    public void versionWasSelected(Version version, boolean isSecondVersion) {
        UWPreferenceDataManager.selectedVersion(getApplicationContext(), version, isSecondVersion);
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
