package activity.reading;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import activity.bookSelection.GeneralSelectionActivity;
import adapters.ReadingPagerAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingActivity extends GeneralSelectionActivity {

    ViewPager readingViewPager = null;

    VersionModel versionModel = null;
    BibleChapterModel chapterModel = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_reading;
    }
    @Override
    protected ArrayList<String> getListOfLanguages() {

        if(versionModel == null){
            setVersion();
        }
        return versionModel.getAvailableLanguages(getApplicationContext());
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
    protected void updateListView() {
        setVersion();
        this.setUIWithCurrentIndex();
    }

    @Override
    protected String getActionBarTitle() {
        return chapterModel.getTitle();
    }

    protected void setUIWithCurrentIndex() {
        int index = readingViewPager.getCurrentItem();

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

        if(versionModel == null){
            this.setVersion();
        }
        actionbarTextView.setText(chapterModel.getTitle());
        ReadingPagerAdapter adapter = new ReadingPagerAdapter(this, versionModel.getBibleChildModels(getApplicationContext()),
                actionbarTextView, getIntent());

        readingViewPager.setAdapter(adapter);
        setupTouchListener(readingViewPager);
        int currentItem = Integer.parseInt(chapterModel.number.replaceAll("[^0-9]", "")) - 1;
        readingViewPager.setCurrentItem(currentItem);

    }

    private void setVersion(){

        if(chapterModel == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                String chosenChapter = extras.getString(CHOSEN_ID);
                BibleChapterModel chapter = (new BibleChapterDataSource(this.getApplicationContext())).getModel(chosenChapter);
                chapterModel = chapter;

                this.versionModel = chapter.getParent(getApplicationContext());
                checkForLanguageChange();
            }
        }
        else{
            checkForLanguageChange();
        }
    }

    private void checkForLanguageChange(){

        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.selected_language), "English");

        Context context = getApplicationContext();

        if(!chapterModel.getParent(context).getParent(context).languageName.equalsIgnoreCase(selectedLanguage)){
            LanguageModel correctLanguage = null;

            ArrayList<LanguageModel> languages = chapterModel.getParent(context).
                    getParent(getApplicationContext()).getParent(getApplicationContext()).
                    getChildModels(getApplicationContext());

            for(LanguageModel model : languages){
                if(selectedLanguage.equalsIgnoreCase(model.languageName)){
                    correctLanguage = model;
                    break;
                }
            }

            if(correctLanguage != null){
                ArrayList<BibleChapterModel> chapters = correctLanguage.getChildModels(context)
                        .get(0).getBibleChildModels(context);

                for(BibleChapterModel model : chapters){
                    if(model.number.equalsIgnoreCase(chapterModel.number)){
                        chapterModel = model;
                        this.versionModel = chapterModel.getParent(context);
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
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
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
