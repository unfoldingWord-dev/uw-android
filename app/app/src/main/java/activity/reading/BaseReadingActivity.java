/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity.reading;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.github.peejweej.androidsideloading.fragments.TypeChoosingFragment;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.readingSelection.BookSelectionActivity;
import activity.readingSelection.VersionSelectionActivity;
import fragments.BitrateFragment;
import fragments.ResourceChoosingFragment;
import fragments.selection.ChapterSelectionFragment;
import fragments.Reading.ReadingFragmentListener;
import fragments.selection.StoryChaptersFragment;
import model.AudioBitrate;
import model.DataFileManager;
import model.DownloadState;
import model.SharingHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import model.parsers.MediaType;
import services.UWBookMediaDownloaderService;
import services.UWUpdaterService;
import singletons.UWAudioPlayer;
import utils.UWPreferenceDataAccessor;
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
        UWPreferenceDataAccessor.PreferencesBibleChapterChangedListener,
        UWAudioPlayer.UWAudioPlayerListener,
        ResourceChoosingFragment.ResourceChoosingListener
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
    private ReadingTabBar tabBar;

    private UWReadingToolbarViewGroup readingToolbar;
    private ViewGroup audioPlayerLayout;
    private AudioPlayerViewGroup audioPlayerViewGroup;

    private boolean isMini = false;
    private boolean isDiglot = false;


    private boolean showingAudio = false;

    //region Abstract Methods

    /**
     * @return the current Book being used
     */
    abstract protected Book getBook();
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

        if(getBook() == null){
            gotoVersionSelection(false);
        }
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
        gotoVersionSelection(false);
    }

    @Override
    public void secondaryVersionButtonClicked() {
        gotoVersionSelection(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(audioPlayerViewGroup != null){
            audioPlayerViewGroup.onResume();
        }

        update();
        if(getBook() == null){
            errorView.setVisibility(View.VISIBLE);
            findViewById(R.id.tab_bar_view).setVisibility(View.INVISIBLE);
        }
        else {
            findViewById(R.id.tab_bar_view).setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
            registerForListeners();
        }
    }

    private void registerForListeners(){
        UWPreferenceDataAccessor.addBibleChapterListener(this);
        UWPreferenceDataAccessor.addStoryPageListener(this);
        UWAudioPlayer.getInstance(getApplicationContext()).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(audioPlayerViewGroup != null){
            audioPlayerViewGroup.onPause();
        }

        unregisterListeners();
        UWAudioPlayer.getInstance(getApplicationContext()).reset();
    }

    private void unregisterListeners(){
        UWPreferenceDataAccessor.removeBibleChapterListener(this);
        UWPreferenceDataAccessor.removeStoryPageListener(this);
        UWAudioPlayer.getInstance(getApplicationContext()).removeListener(this);
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

    //region userInteraction

    public void versionSelectionButtonClicked(View view) {

        gotoVersionSelection(false);
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

    @Override
    public void paused() {
        setAudioButtonState(false);
    }

    @Override
    public void update(long duration, long progress) {
    }

    @Override
    public void started() {
        setAudioButtonState(true);
    }

    private void updateTabBar(){

        boolean hasAudioBook = (getBook() != null && getBook().getAudioBook() != null);
        tabBar.getButton(0).setEnabled(hasAudioBook);
        tabBar.getButton(0).setClickable(hasAudioBook);
        tabBar.setImageAtIndex((hasAudioBook) ? R.drawable.audio_normal : R.drawable.audio_disabled, 0);
        if(audioPlayerLayout != null && !hasAudioBook){
            audioPlayerLayout.setVisibility(View.GONE);
        }
        if(audioPlayerViewGroup != null) {
            Book book = getBook();
            if(book != null) {
            book.refresh();
                DataFileManager.getStateOfContent(getApplicationContext(), book.getVersion(), MediaType.MEDIA_TYPE_TEXT, new DataFileManager.GetDownloadStateResponse() {
                    @Override
                    public void foundDownloadState(DownloadState state) {
                        audioPlayerViewGroup.handleDownloadState(state);
                    }
                });
            }
        }

        boolean hasVideo = false;
        tabBar.getButton(1).setEnabled(hasVideo);
        tabBar.getButton(1).setClickable(hasVideo);
        tabBar.setImageAtIndex((hasVideo) ? R.drawable.video_normal : R.drawable.video_disabled, 1);
        setAudioPlayerVisibility(showingAudio);
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

    private void downloadBookAudio() {

        BitrateFragment.newInstance(getBook().getAudioBook().getAudioChapters().get(0).getBitRates(),
                "Select Audio Bitrate", new BitrateFragment.BitrateFragmentListener() {
                    @Override
                    public void bitrateChosen(DialogFragment fragment, AudioBitrate bitrate) {

                        setupIntentFilter();
                        Intent downloadIntent = new Intent(getApplicationContext(), UWBookMediaDownloaderService.class);
                        downloadIntent.putExtra(UWBookMediaDownloaderService.BOOK_ID_PARAM, getBook().getId());
                        downloadIntent.putExtra(UWBookMediaDownloaderService.IS_VIDEO_PARAM, false);
                        downloadIntent.putExtra(UWBookMediaDownloaderService.BITRATE_PARAM, bitrate);
                        getApplicationContext().startService(downloadIntent);
                        fragment.dismiss();
                    }

                    @Override
                    public void dismissed() {

                    }
                }).show(getSupportFragmentManager(), "BitrateFragment");



    }

    private BroadcastReceiver receiver;
    private BroadcastReceiver createBroadcastReceiver() {

        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
                Book book = getBook();
                book.refresh();
                DataFileManager.getStateOfContent(context, book.getVersion(), MediaType.MEDIA_TYPE_AUDIO, new DataFileManager.GetDownloadStateResponse() {
                    @Override
                    public void foundDownloadState(DownloadState state) {
                        audioPlayerViewGroup.handleDownloadState(state);
                    }
                });
            }
        };
    }

    private void setupIntentFilter(){
        receiver = createBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UWUpdaterService.BROAD_CAST_DOWNLOAD_ENDED);
        getApplicationContext().registerReceiver(receiver, filter);
    }

    private void setAudioButtonState(boolean isPlaying){
        tabBar.setImageAtIndex((isPlaying) ? R.drawable.audio_active : R.drawable.audio_normal, 0);
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
        if(getBook() != null && !getBook().getVersion().hasAudio()){
            visible = false;
        }


        audioPlayerLayout.setVisibility((visible) ? View.VISIBLE : View.GONE);
        if(getBook() != null) {
            DataFileManager.getStateOfContent(getApplicationContext(), getBook().getVersion(), MediaType.MEDIA_TYPE_AUDIO, new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    audioPlayerViewGroup.handleDownloadState(state);
                }
            });
        }
        showingAudio = visible;
    }

    private void shareVersion(){

        Version sharingVersion = getSharingVersion();
        if(sharingVersion != null) {
            shareVersion(sharingVersion);
        }
    }

    private void shareVersion(Version version){

        if(version.hasVideo() || version.hasAudio()){
            ResourceChoosingFragment.newInstance(version).show(getSupportFragmentManager(), "ResourceChoosingFragment");
        }
        else{
            shareVersion(new ArrayList<MediaType>(), version);
        }
    }

    @Override
    public void resourcesChosen(DialogFragment dialogFragment, List<MediaType> types) {

        shareVersion(types, getSharingVersion());
        dialogFragment.dismiss();
    }

    private void shareVersion(List<MediaType> types, Version version){

        TypeChoosingFragment.constructFragment(SharingHelper.getShareInformation(getApplicationContext(), version, types))
                .show(getSupportFragmentManager(), "TypeChoosingFragment");
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
    protected void gotoVersionSelection(boolean isSecondVersion){

//        if(isTablet()){
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//            VersionSelectionFragment fragment = VersionSelectionFragment.newInstance(projectId, true);
//            fragment.show(ft, VERSION_FRAGMENT_ID);
//        }
//        else {
            startActivityForResult(new Intent(this, VersionSelectionActivity.class).putExtra(
                    VersionSelectionActivity.PROJECT_PARAM, getProject())
                    .putExtra(VersionSelectionActivity.IS_SECOND_VERSION_PARAM, isSecondVersion), 1);
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && data.getExtras() != null && data.getExtras().containsKey(VersionSelectionActivity.MEDIA_TYPE_PARAM)){
            MediaType type = (MediaType) data.getExtras().getSerializable(VersionSelectionActivity.MEDIA_TYPE_PARAM);

            switch (type){
                case MEDIA_TYPE_AUDIO:{
                    setAudioPlayerVisibility(true);
                }
            }
        }
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
