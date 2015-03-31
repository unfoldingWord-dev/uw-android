package activity.reading;


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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.unfoldingword.mobile.R;

import java.util.Locale;

import activity.bookSelection.GeneralSelectionActivity;
import activity.bookSelection.StoryChapterSelectionActivity;
import activity.bookSelection.VersionSelectionActivity;
import adapters.StoryPagerAdapter;
import model.datasource.StoriesChapterDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class StoryReadingActivity extends ActionBarActivity {

    static final public String STORY_INDEX_STRING = "STORY_INDEX_STRING";

    private ViewPager readingViewPager = null;
    private ActionBar mActionBar = null;
    private LinearLayout versionsButton = null;
    private LinearLayout chaptersButton = null;
    private TextView versionsTextView = null;
    private TextView chapterTextView = null;

    ImageLoader mImageLoader = null;

    private StoriesChapterModel mChapter = null;
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

    protected void setUI() {

        setupActionBar();

        if(mChapter == null || selectedProject == null){
            return;
        }

        setupPager();
    }

    private void setupActionBar(){
        View view = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);

        mActionBar = getSupportActionBar();
        chaptersButton = (LinearLayout) view.findViewById(R.id.middle_button);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        setupChapterButton(view);
        setupVersionButton(view);
    }

    private void setupChapterButton(View view){

        chaptersButton = (LinearLayout) view.findViewById(R.id.middle_button);
        chapterTextView = (TextView) view.findViewById(R.id.middle_button_text);
        if(this.mChapter != null) {
            chapterTextView.setText(this.mChapter.title);
        }
        else{
            chaptersButton.setVisibility(View.INVISIBLE);
            chapterTextView.setText("");
        }
    }

    private void setupVersionButton(View view) {

        versionsButton = (LinearLayout) view.findViewById(R.id.language_button);
        versionsTextView = (TextView) view.findViewById(R.id.language_text);
        if(this.mChapter != null) {
            Locale currentLocale = new Locale(this.mChapter.getParent(getApplicationContext()).getParent(getApplicationContext()).getParent(getApplicationContext()).languageAbbreviation);
            versionsTextView.setText(currentLocale.getDisplayLanguage());
        }
        else{
            versionsTextView.setText("Select Version");
        }
    }

    private void setupPager(){

        readingViewPager = (ViewPager) findViewById(R.id.myViewPager);
        mImageLoader = ImageLoader.getInstance();

        if(mImageLoader.isInited()) {
            ImageLoader.getInstance().destroy();
        }

        int currentItem = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(STORY_INDEX_STRING, -1);
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));

        StoryPagerAdapter adapter = new StoryPagerAdapter(this, mChapter,
                mImageLoader, chapterTextView, STORY_INDEX_STRING);

        readingViewPager.setAdapter(adapter);

        setupTouchListener(readingViewPager);

        if(currentItem > 0){
            readingViewPager.setCurrentItem(currentItem);
        }
    }

    private void goToVersionSelection(){

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String projectId = extras.getString(GeneralSelectionActivity.CHOSEN_ID);
        startActivity(new Intent(this, VersionSelectionActivity.class).putExtra(
                GeneralSelectionActivity.CHOSEN_ID, projectId));
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
    }

    private void goToChapterActivity(){

        startActivity(new Intent(getApplicationContext(), StoryChapterSelectionActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
    }

    private void setData(){

        if(mChapter == null || selectedProject == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Context context = getApplicationContext();

                Long versionId = Long.parseLong(UWPreferenceManager.getSelectedStoryVersion(context));

                if(versionId < 0){
                    return;
                }

                VersionModel currentVersion = new VersionDataSource(context).getModel(Long.toString(versionId));

                this.selectedProject = currentVersion.getParent(context).getParent(context);

                Long chapterId = Long.parseLong(UWPreferenceManager.getSelectedStoryChapter(context));

                if(chapterId < 0){
                    this.mChapter = currentVersion.getChildModels(context).get(0).getStoryChapter(context, 1);
                    UWPreferenceManager.setSelectedStoryChapter(context, this.mChapter.uid);
                }
                else {
                    this.mChapter = new StoriesChapterDataSource(getApplicationContext()).getModel(Long.toString(chapterId));
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

    private void setupTouchListener(View view){

        view.setOnTouchListener(new View.OnTouchListener() {
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
                                && ((System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout)) {
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
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(mImageLoader != null) {
                ImageLoader.getInstance().destroy();
            }
        }
        handleBack();
        return true;
    }

    @Override
    public void onBackPressed() {
        ImageLoader.getInstance().destroy();
        handleBack();
    }

    private void handleBack(){

        //reset  Preference
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(STORY_INDEX_STRING, -1).commit();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void chapterButtonClicked(View view) {
        goToChapterActivity();
    }

    public void versionButtonClicked(View view) {
        goToVersionSelection();
    }
}
