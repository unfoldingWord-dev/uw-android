package activity.reading;


import android.app.Dialog;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import activity.bookSelection.BookSelectionActivity;
import activity.bookSelection.GeneralSelectionActivity;
import activity.bookSelection.VersionSelectionActivity;
import adapters.ReadingPagerAdapter;
import fragments.BooksFragment;
import fragments.ChapterSelectionFragment;
import fragments.ChaptersFragment;
import fragments.VersionSelectionFragment;
import model.datasource.BibleChapterDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.VersionModel;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingActivity extends ActionBarActivity implements
        VersionSelectionFragment.VersionSelectionFragmentListener,
        ChapterSelectionFragment.ChapterSelectionListener,
        BooksFragment.BooksFragmentListener,
        ChaptersFragment.ChaptersFragmentListener
{

    static private final String TAG = "ReadingActivity";

    static final public String BOOK_INDEX_STRING = "READING_INDEX_STRING";

    static private final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    static private final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";

    private ViewPager readingViewPager = null;
    private ActionBar mActionBar = null;
    private LinearLayout versionsButton = null;
    private RelativeLayout chaptersButton = null;
    private TextView versionsTextView = null;
    private TextView chapterTextView = null;

    private BibleChapterModel mChapter = null;
    private ProjectModel selectedProject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        setData();
        if(mChapter == null || selectedProject == null){
            goToVersionSelection();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mChapter == null || selectedProject == null) {
            setContentView(R.layout.activity_reading);
        }
        selectedProject = null;
        mChapter = null;
        setData();
        setUI();

        if(mChapter == null || selectedProject == null){
            setContentView(R.layout.no_text_layout);
        }
        else {
            setupPager();
        }

    }

//    protected void updateListView() {
//        setData();
//        this.setUIWithCurrentIndex();
//    }
//
//    protected void setUIWithCurrentIndex() {
//        int index = readingViewPager.getCurrentItem();
//
//        setUI();
//        readingViewPager.setCurrentItem(index);
//    }

    private void reload(){
        mChapter = null;
        selectedProject = null;
        onStart();
    }
    /**
     * Initializing the components
     */
    protected void setUI() {
        setData();
        setupActionBar();
    }

    private void setupActionBar(){
        View view = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);

        mActionBar = getSupportActionBar();
        chaptersButton = (RelativeLayout) view.findViewById(R.id.middle_button);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        setupChapterButton(view);
        setupVersionButton(view);
        setupCheckingLevelView(view);
    }

    void setupCheckingLevelView(View view){

        ImageView imageView = (ImageView) view.findViewById(R.id.checking_level_image_view);
        if(this.mChapter != null){
            int checkingLevel = Integer.parseInt(mChapter.getParent(getApplicationContext()).getParent(getApplicationContext()).status.checkingLevel);
            imageView.setImageResource(getCheckingLevelImage(checkingLevel));
            imageView.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setVisibility(View.GONE);
        }
    }

    private int getCheckingLevelImage(int level){
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

    private void setupChapterButton(View view){

        chaptersButton = (RelativeLayout) view.findViewById(R.id.middle_button);
        chapterTextView = (TextView) view.findViewById(R.id.middle_button_text);
        if(this.mChapter != null) {
            chapterTextView.setText(this.mChapter.getTitle(getApplicationContext()));
        }
        else{
            chaptersButton.setVisibility(View.INVISIBLE);
            chapterTextView.setText("");
        }
    }

    public void updateChapter(String title){
        this.chapterTextView.setText(title);
        versionsButton.forceLayout();
    }

    private void setupVersionButton(View view) {

        versionsButton = (LinearLayout) view.findViewById(R.id.language_button);
        versionsTextView = (TextView) view.findViewById(R.id.language_text);
        if(this.mChapter != null) {
            versionsTextView.setText(this.mChapter.getParent(getApplicationContext()).getParent(getApplicationContext()).slug);
        }
        else{
            versionsTextView.setText("Select Version");
        }
    }

    private void setupPager(){

        readingViewPager = (ViewPager) findViewById(R.id.myViewPager);

        ArrayList<BibleChapterModel> chapters = mChapter.getParent(getApplicationContext()).getBibleChildModels(getApplicationContext());
        ReadingPagerAdapter adapter = new ReadingPagerAdapter(this, chapters, chapterTextView, BOOK_INDEX_STRING, getDoubleTapTouchListener());

        readingViewPager.setAdapter(adapter);
        readingViewPager.setOnTouchListener(getDoubleTapTouchListener());

        int currentItem = Integer.parseInt(mChapter.number.replaceAll("[^0-9]", "")) - 1;
        readingViewPager.setCurrentItem(currentItem);
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

    private void goToVersionSelection(){

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        String projectId = extras.getString(GeneralSelectionActivity.CHOSEN_ID);

        if(isTablet()){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            VersionSelectionFragment fragment = VersionSelectionFragment.newInstance(projectId, true);
            fragment.show(ft, VERSION_FRAGMENT_ID);
        }
        else {
            startActivity(new Intent(this, VersionSelectionActivity.class).putExtra(
                    GeneralSelectionActivity.CHOSEN_ID, projectId));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
        }
    }

    private void goToChapterActivity(){

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

//        String projectId = extras.getString(GeneralSelectionActivity.CHOSEN_ID);

        if(isTablet()){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ChapterSelectionFragment fragment = ChapterSelectionFragment.newInstance(true);
            fragment.show(ft, CHAPTER_SELECTION_FRAGMENT_ID);
        }
        else {
            startActivity(new Intent(getApplicationContext(), BookSelectionActivity.class));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
        }
    }


    private void setData(){

        if(mChapter == null || selectedProject == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Context context = getApplicationContext();

                Long versionId = Long.parseLong(UWPreferenceManager.getSelectedBibleVersion(context));

                if(versionId < 0){
                    return;
                }

                VersionModel currentVersion = new VersionDataSource(context).getModel(Long.toString(versionId));

                this.selectedProject = currentVersion.getParent(context).getParent(context);

                Long chapterId = Long.parseLong(UWPreferenceManager.getSelectedBibleChapter(context));

                if(chapterId < 0){
                    this.mChapter = currentVersion.getChildModels(context).get(0).getBibleChapter(context, 1);
                    UWPreferenceManager.setSelectedBibleChapter(context, this.mChapter.uid);
                }
                else {
                    this.mChapter = new BibleChapterDataSource(getApplicationContext()).getModel(Long.toString(chapterId));
                }
            }
        }
        else{
        }
    }

    private void handleActionBarHidden(boolean hide){

        if( hide){
            mActionBar.hide();
        }
        else{
            mActionBar.show();
        }
    }


    private View.OnTouchListener getDoubleTapTouchListener(){

        return new View.OnTouchListener() {
            Handler handler = new Handler();


            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            Resources res = getResources();
            int tapTimeout = res.getInteger(R.integer.tap_timeout);
            int doubleTapTimeout = res.getInteger(R.integer.double_tap_timeout);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        if ((numberOfTaps > 0)
                                && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);

                        if ((System.currentTimeMillis() - touchDownMs) > tapTimeout) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if ((numberOfTaps > 0)
                                && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }

                        lastTapTimeMs = System.currentTimeMillis();

//                        if(numberOfTaps == 1){
//                            checkShouldChangeNavBarHidden();
//                            return false;
//                        }

                        if(numberOfTaps == 2){
                            checkShouldChangeNavBarHidden();
                            return true;
                        }

                }

                return false;
            }
        };
    }

    private int getScreenOrientation()
    {
        Display getOrient = getWindowManager().getDefaultDisplay();

        int orientation = getOrient.getOrientation();

        // Sometimes you may get undefined orientation Value is 0
        // simple logic solves the problem compare the screen
        // X,Y Co-ordinates and determine the Orientation in such cases

        return orientation % 2; // return value 0 is portrait and 1 is Landscape Mode
    }


    private void checkShouldChangeNavBarHidden(){

//        boolean shouldHide = (getScreenOrientation() == 1)? mActionBar.isShowing() : false;
        boolean shouldHide = mActionBar.isShowing();
        handleActionBarHidden(shouldHide);
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
        }

        return true;
    }

    private void handleBack(){

        //reset  Preference
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(BOOK_INDEX_STRING, -1).commit();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void chapterButtonClicked(View view) {
        goToChapterActivity();
    }

    public void versionButtonClicked(View view) {
        goToVersionSelection();
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
        reload();
    }

    @Override
    public void bookWasSelected(String chapterUid) {

    }

    @Override
    public void chapterWasSelected() {

    }

    public void checkingLevelClicked(View view) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        CheckingLevelFragment fragment = new CheckingLevelFragment();
        fragment.show(ft, CHECKING_LEVEL_FRAGMENT_ID);
    }

    static private final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";

    static public class CheckingLevelFragment extends DialogFragment {

        public CheckingLevelFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.version_footer, container, false);

            TextView tView = (TextView) view.findViewById(R.id.textView);
            String versionName = BuildConfig.VERSION_NAME;

            tView.setText(versionName);
            return view;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }
    }
}
