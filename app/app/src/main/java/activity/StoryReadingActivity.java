package activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import activity.selectionActivities.GeneralSelectionActivity;
import adapter.selectionAdapters.GeneralRowInterface;
import adapter.StoryPagerAdapter;
import model.datasource.StoriesChapterDataSource;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.StoriesChapterModel;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class StoryReadingActivity extends GeneralSelectionActivity {

    ViewPager readingViewPager = null;
    ImageLoader mImageLoader;

    StoriesChapterModel storiesChapterModel = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_reading;
    }
    @Override
    protected ArrayList<String> getListOfLanguages() {

        if(storiesChapterModel == null){
            setChapter();
        }

        return storiesChapterModel.getAvailableLanguages(getApplicationContext());
    }
    @Override
    protected ArrayList<GeneralRowInterface> getData() {
        return null;
    }
    @Override
    protected void storedValues() {
    }
    @Override
    protected String getIndexStorageString() {
        return null;
    }
    @Override
    protected Class getChildClass() {
        return null;
    }


    @Override
    protected String getActionBarTitle() {
        return this.storiesChapterModel.getTitle();
    }

    @Override
    protected void updateListView() {
        setChapter();
        this.setUIWithCurrentIndex();
    }

    protected void setUIWithCurrentIndex() {
        int index =readingViewPager.getCurrentItem();
        setUI();
        readingViewPager.setCurrentItem(index);
    }
    /**
     * Initializing the components
     */
    @Override
    protected void setUI() {

        super.setUI();

        readingViewPager = (ViewPager) findViewById(R.id.myViewPager);
        mImageLoader = ImageLoader.getInstance();

        if(mImageLoader.isInited()) {
            ImageLoader.getInstance().destroy();
        }

        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));

        if(storiesChapterModel == null){
            this.setChapter();
        }
        actionbarTextView.setText(storiesChapterModel.getTitle());
        StoryPagerAdapter adapter = new StoryPagerAdapter(this, storiesChapterModel,
                mImageLoader,
                actionbarTextView, getIntent());

        readingViewPager.setAdapter(adapter);

        setupTouchListener(readingViewPager);
    }

    private void setChapter(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String chosenChapter = extras.getString(CHOSEN_ID);
            StoriesChapterModel chapter = (new StoriesChapterDataSource(this.getApplicationContext())).getModel(chosenChapter);

            this.storiesChapterModel = chapter;
            checkForLanguageChange();
        }
    }

    private void checkForLanguageChange(){

        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.selected_language), "English");

        Context context = getApplicationContext();

        if(!storiesChapterModel.getParent(context).getParent(context).getParent(context).languageName.equalsIgnoreCase(selectedLanguage)){
            LanguageModel correctLanguage = null;

            ArrayList<LanguageModel> languages = storiesChapterModel.getParent(context).
                    getParent(getApplicationContext()).getParent(getApplicationContext()).
                    getParent(context).getChildModels(getApplicationContext());

            for(LanguageModel model : languages){
                if(selectedLanguage.equalsIgnoreCase(model.languageName)){
                    correctLanguage = model;
                    break;
                }
            }

            if(correctLanguage != null){

                ArrayList<StoriesChapterModel> chapters = correctLanguage.getChildModels(context)
                        .get(0).getStoriesChildModels(context).get(0).getChildModels(context);

                for(StoriesChapterModel model : chapters){

                    if(model.number.equalsIgnoreCase(storiesChapterModel.number)){
                        storiesChapterModel = model;
                        break;
                    }
                }
            }

        }
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
