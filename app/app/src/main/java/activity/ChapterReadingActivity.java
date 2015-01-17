package activity;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.unfoldingword.mobile.R;

import adapter.ViewPagerAdapter;
import model.db.DBManager;
import model.modelClasses.ChapterModel;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ChapterReadingActivity extends ActionBarActivity {

    ViewPager readingViewPager = null;
    ImageLoader mImageLoader;
    ActionBar mActionBar = null;
    TextView actionbarTextView = null;

    ChapterModel chapterModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String languageCode= extras.getString(LanguageChooserActivity.LANGUAGE_CODE);
            String chapterNumber = extras.getString(ChapterSelectionActivity.SELECTED_CHAPTER_POS);

            chapterModel = DBManager.getInstance(getApplicationContext()).getChapterForLanguageAndNumber(languageCode, chapterNumber);
        }

        setUI();

    }

    /**
     * Initializing the components
     */
    private void setUI() {
        mActionBar = getSupportActionBar();
        LayoutInflater infl = getLayoutInflater();
        View actionView = infl.inflate(R.layout.actionbar_custom_view, null);
        actionView.setPadding(0,0,60,0);
        actionbarTextView = (TextView) actionView.findViewById(R.id.actionbarTextView);

        mActionBar.setCustomView(actionView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        readingViewPager = (ViewPager) findViewById(R.id.myViewPager);

        mImageLoader = ImageLoader.getInstance();

        if(mImageLoader.isInited()) {
            ImageLoader.getInstance().destroy();
        }

        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));

//        ViewPagerAdapter adapter = new ViewPagerAdapter(this, );

        actionbarTextView.setText(chapterModel.title);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, chapterModel.getChildModels(getApplicationContext()),
                mImageLoader, "next chapter",
                actionbarTextView, getIntent(), "eng?");

        readingViewPager.setAdapter(adapter);

        setupTouchListener(readingViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            ImageLoader.getInstance().destroy();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
        ImageLoader.getInstance().destroy();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);

                        int tapTimeout = ViewConfiguration.getTapTimeout();
                        if ((System.currentTimeMillis() - touchDownMs) > tapTimeout) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if (numberOfTaps > 0
                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
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
}
