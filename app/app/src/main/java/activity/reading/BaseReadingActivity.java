package activity.reading;


import android.animation.LayoutTransition;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.textSelection.BookSelectionActivity;
import activity.textSelection.VersionSelectionActivity;
import adapters.ReadingScrollNotifications;
import fragments.ChapterSelectionFragment;
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
        ReadingFragmentListener
{
    private static final String TAG = "ReadingActivity";

    private static final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    private static final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";
    protected static final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";

    protected FrameLayout readingLayout;
    protected FrameLayout secondaryReadingLayout;
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
            goToVersionSelection(false);
        }
    }

    private void setAnimated(){
        if(android.os.Build.VERSION.SDK_INT > 15) {
            LayoutTransition transition = new LayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            transition.enableTransitionType(LayoutTransition.APPEARING);
            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
            transition.setDuration(300);
            ((LinearLayout) findViewById(R.id.reading_layout)).setLayoutTransition(transition);
        }
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }

    protected void setupViews(){
        readingLayout = (FrameLayout) findViewById(R.id.reading_fragment_frame);
        secondaryReadingLayout = (FrameLayout) findViewById(R.id.secondary_reading_fragment_frame);
        errorTextView = (TextView) findViewById(R.id.reading_error_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setupToolbar(false);
        getToolbar().setRightImageResource(R.drawable.diglot_icon);
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
        getToolbar().setRightImageResource(R.drawable.diglot_icon);
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

        updateToolbarTitle();
    }

    protected void updateToolbarTitle(){
        String title = getChapterLabelText();
        getToolbar().setTitle(title, true);
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

    protected void goToVersionSelection(boolean isSecondVersion){

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
    public boolean toggleNavBar() {
        boolean isHidden = getToolbar().toggleHidden();
        return isHidden;
    }

    @Override
    public void rightButtonClicked() {
        toggleDiglot();
    }

    @Override
    public void centerButtonClicked() {
        goToChapterActivity();
    }

    @Override
    public void clickedChooseVersion(boolean isSecondReadingView) {
        goToVersionSelection(isSecondReadingView);
    }

    @Override
    public void showCheckingLevel(Version version) {
        goToCheckingLevelView(version);
    }

    @Override
    public void versionWasSelected(Version version, boolean isSecondVersion) {
        UWPreferenceManager.selectedVersion(getApplicationContext(), version, isSecondVersion);
        removeFragment(VERSION_FRAGMENT_ID);
    }

    @Override
    public void selectionFragmentChoseChapter() {
        removeFragment(CHAPTER_SELECTION_FRAGMENT_ID);
        loadData();
        updateViews();
    }

    private void toggleDiglot(){

        LinearLayout.LayoutParams mainReadingParams = (LinearLayout.LayoutParams) readingLayout.getLayoutParams();
        LinearLayout.LayoutParams secondaryReadingParams = (LinearLayout.LayoutParams) secondaryReadingLayout.getLayoutParams();
        boolean isDiglot = (secondaryReadingParams.weight > .1f);

        mainReadingParams.weight = (isDiglot)? 1.0f : 0.4f;
        secondaryReadingParams.weight = (isDiglot)? 0.0f : 0.4f;

        readingLayout.setLayoutParams(mainReadingParams);
        secondaryReadingLayout.setLayoutParams(secondaryReadingParams);
        secondaryReadingLayout.setVisibility((isDiglot)? View.GONE : View.VISIBLE);
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

    private void goToCheckingLevelView(Version version){
        CheckingLevelFragment fragment = CheckingLevelFragment.createFragment(version);
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
