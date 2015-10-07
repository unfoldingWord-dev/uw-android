package activity.reading;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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
import fragments.VersionSelectionFragment;
import model.DownloadState;
import model.SharingHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import services.UWBookMediaDownloaderService;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceDataManager;
import view.AudioPlayerViewGroup;
import view.ReadingTabBar;
import view.ReadingToolbarViewBibleModel;
import view.ReadingToolbarViewData;
import view.ReadingToolbarViewStoriesModel;
import view.UWReadingToolbarViewGroup;
import view.UWTabBar;

/**
 * Created by PJ Fechner on 5/12/14.
 * abstract activity to handle most of the logic involved in the different reading activites.
 */
public abstract class BaseReadingActivity extends UWBaseActivity implements
        ReadingFragmentListener,
        UWReadingToolbarViewGroup.UWReadingToolbarListener,
        UWPreferenceDataAccessor.PreferencesStoryPageChangedListener,
        UWPreferenceDataAccessor.PreferencesBibleChapterChangedListener
{
    private static final String TAG = "ReadingActivity";

    private static final String IS_HIDDEN_PARAM = "IS_HIDDEN_PARAM";
    private static final String IS_DIGLOT_PARAM = "IS_DIGLOT_PARAM";

    public static final String SCROLLED_PAGE = "SCROLLED_PAGE"    ;

    private static final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    private static final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";
//    protected static final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";


    protected FrameLayout readingLayout;
    protected FrameLayout secondaryReadingLayout;
    protected View errorView;
    protected Book book;
    private ReadingTabBar tabBar;

    private UWReadingToolbarViewGroup readingToolbar;
    private ViewGroup audioPlayerLayout;
    private AudioPlayerViewGroup audioPlayerViewGroup;

    private boolean isMini = false;
    private boolean isDiglot = false;

    //region Abstract Methods

    /**
     * @return The project for the current activity
     */
    abstract protected Project getProject();

    /**
     * @return Version to be shared
     */
    abstract protected Version getSharingVersion();

    /**
     * signal to make the reading text larger
     */
    abstract protected void makeTextLarger();

    /**
     * signal to make the reading text smaller
     */
    abstract protected void makeTextSmaller();

    /**
     * @return View data for the toolbar
     */
    abstract protected ReadingToolbarViewData getToolbarViewData();

    //endregion

    //region Activity Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        setupViews();
    }

    protected void setupViews(){
        audioPlayerLayout = (ViewGroup) (findViewById(R.id.audio_player));
        readingLayout = (FrameLayout) findViewById(R.id.reading_fragment_frame);
        secondaryReadingLayout = (FrameLayout) findViewById(R.id.secondary_reading_fragment_frame);
        errorView = findViewById(R.id.no_version_layout);

        findViewById(R.id.larger_text_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeTextLarger();
            }
        });

        findViewById(R.id.smaller_text_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeTextSmaller();
            }
        });
        findViewById(R.id.reading_text_options).setVisibility(View.GONE);
    }

    public void updateToolbar(ReadingToolbarViewData data){

        if(readingToolbar != null){
            readingToolbar.setViewData(data);
        }
        else {
            readingToolbar = new UWReadingToolbarViewGroup((Toolbar) findViewById(R.id.toolbar), this, data, this);
        }
    }

    /**
     * after this method is run all views and data should be updated
     */
    protected void update(){
        updateToolbar(getToolbarViewData());
        if(audioPlayerViewGroup != null){
            audioPlayerViewGroup.resume();
        }
        setupTabBar();
        updateTabBar();
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
        update();
        if(audioPlayerViewGroup != null){
            onResume();
        }
        UWPreferenceDataAccessor.addBibleChapterListener(this);
        UWPreferenceDataAccessor.addStoryPageListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(audioPlayerViewGroup != null){
            audioPlayerViewGroup.onPause();
        }

        UWPreferenceDataAccessor.removeBibleChapterListener(this);
        UWPreferenceDataAccessor.removeStoryPageListener(this);
    }

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
     * @return the current Book being used
     */
    protected Book getBook(){
        if(this.book != null) {
            this.book.refresh();
        }
        return this.book;
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

    protected void setTextLargerDisabled(boolean disabled){

        findViewById(R.id.larger_text_button).setClickable(!disabled);
        findViewById(R.id.larger_text_button).setEnabled(!disabled);

    }

    protected void setTextSmallerDisabled(boolean disabled){

        findViewById(R.id.smaller_text_button).setClickable(!disabled);
        findViewById(R.id.smaller_text_button).setEnabled(!disabled);
    }

    private void setupTabBar(){

        ViewGroup baseAudioPlayerLayout = (RelativeLayout) findViewById(R.id.audio_player);
        baseAudioPlayerLayout.setVisibility(View.GONE);

        int[] images = {R.drawable.audio_normal, R.drawable.video_normal,
                R.drawable.font_normal, R.drawable.diglot_normal,
                R.drawable.share_normal};

        tabBar = new ReadingTabBar(getApplicationContext(), images, (ViewGroup) findViewById(R.id.tab_bar_view), baseAudioPlayerLayout, new UWTabBar.BottomBarListener() {
            @Override
            public void buttonPressedAtIndex(int index) {
                tabBarPressed(index);
            }
        });
    }

    private void updateTabBar(){

        boolean hasAudioBook = (getBook() != null && getBook().getAudioBook() != null);
        tabBar.getButton(0).setEnabled(hasAudioBook);
        tabBar.getButton(0).setClickable(hasAudioBook);
        tabBar.setImageAtIndex((hasAudioBook) ? R.drawable.audio_normal : R.drawable.audio_disabled, 0);
        if(audioPlayerLayout != null && !hasAudioBook){
            audioPlayerLayout.setVisibility(View.GONE);
        }

        boolean hasVideo = false;
        tabBar.getButton(1).setEnabled(hasVideo);
        tabBar.getButton(1).setClickable(hasVideo);
        tabBar.setImageAtIndex((hasVideo) ? R.drawable.video_normal : R.drawable.video_disabled, 1);
    }

    private void setupAudioPlayer(){

        audioPlayerViewGroup = new AudioPlayerViewGroup(getApplicationContext(), audioPlayerLayout, new AudioPlayerViewGroup.AudioPlayerViewGroupListener() {
            @Override
            public void downloadClicked() {
                audioPlayerViewGroup.handleDownloadState(DownloadState.DOWNLOAD_STATE_DOWNLOADING);
                downloadBookAudio();
            }
        });
    }

    private void downloadBookAudio(){

        Intent downloadIntent = new Intent(getApplicationContext(), UWBookMediaDownloaderService.class);
        downloadIntent.putExtra(UWBookMediaDownloaderService.BOOK_PARAM, getBook().getId());
        downloadIntent.putExtra(UWBookMediaDownloaderService.IS_VIDEO_PARAM, false);
        getApplicationContext().startService(downloadIntent);
    }

//    private void setupMediaPlayer(AudioChapter chapter){
//
//        if(chapter == null){
//            return;
//        }
//
//        File audioFile = UWFileUtils.loadSourceFile(chapter.getAudioUrl(), getApplicationContext());
//        Uri uri = Uri.fromFile(audioFile);
//
//        List<AudioMarker> markers = AudioMarkerParser.createAudioMarkers(uri, chapter.getLength());
//
//        if(mediaPlayer != null && audioPlayerViewGroup != null && mediaPlayer.isPlaying()){
//            audioPlayerViewGroup.stopPlayback();
//            mediaPlayer.release();
//        }
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//
//        if(audioPlayerViewGroup != null ){
//            audioPlayerViewGroup.setupMediaPlayer();
//        }
//    }

    private void setAudioButtonState(boolean isPlaying){
        tabBar.setImageAtIndex((isPlaying)? R.drawable.audio_active : R.drawable.audio_normal, 0);
    }

    private void tabBarPressed(int index){

        switch (index){
            case 0:{
                toggleAudioPlayerVisibility();
                break;
            }
            case 1:{
                Intent intent = new Intent((android.content.Intent.ACTION_VIEW));
                intent.setDataAndType(Uri.parse("https://api.unfoldingword.org/uw/video/beta/01-GEN-br256-640x432.mp4"), "video/avi");
                startActivity(intent);
                break;
            }
            case 2:{
                toggleTextSizeVisibility();
                break;
            }
            case 3:{
                readingToolbar.setHasTwoVersions(toggleDiglot());
                break;
            }
            case 4:{
                shareVersion();
                break;
            }
        }
    }

    private void toggleAudioPlayerVisibility(){

        setAudioPlayerVisibility(audioPlayerLayout.getVisibility() != View.VISIBLE);
    }

    private void setAudioPlayerVisibility(boolean visible){

        if(audioPlayerViewGroup == null){
            setupAudioPlayer();
        }

        audioPlayerViewGroup.handleDownloadState(getBook().getAudioSaveStateEnum());
        audioPlayerLayout.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    private void shareVersion(){

        Version sharingVersion = getSharingVersion();
        if(sharingVersion != null) {
            goToNewActivity(SharingHelper.getIntentForSharing(getApplicationContext(), sharingVersion));
        }
    }

    private void toggleTextSizeVisibility(){
        View view = findViewById(R.id.reading_text_options);
        view.setVisibility((view.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
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

        if(false){//isTablet()){
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
//     * @param version version for which to show the info fragment
     */
//    private void goToCheckingLevelView(Version version){
//        VersionInfoFragment fragment = VersionInfoFragment.createFragment(version);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//        fragment.show(ft, CHECKING_LEVEL_FRAGMENT_ID);
//    }

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
        hideTabs(isMini);
        return isMini;
    }

    private void hideTabs(boolean isHidden){
        ViewGroup bottomBar = (ViewGroup) findViewById(R.id.bottom_menu_layout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bottomBar.getLayoutParams());
        if(isHidden){
            params.addRule(RelativeLayout.BELOW, R.id.bottom_marker_layout);
        }
        else{
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        bottomBar.setLayoutParams(params);
    }

    //endregion

    //region dataChangeListeners

    @Override
    public void bibleChapterChanged(BibleChapter mainChapter, BibleChapter secondaryChapter) {
        updateToolbar(new ReadingToolbarViewBibleModel(mainChapter, secondaryChapter));
    }

    @Override
    public void storyPageChanged(StoryPage mainPage, StoryPage secondaryPage) {
        updateToolbar(new ReadingToolbarViewStoriesModel(mainPage, secondaryPage));
    }


    //endregion
}
