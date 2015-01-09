package activity;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import adapter.ViewPagerAdapter;
import models.ChaptersModel;
import parser.JsonParser;
import utils.AppVariable;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ChapterReadingActivity extends ActionBarActivity {
    ViewPager readingViewPager = null;
    ImageLoader mImageLoader;
    ActionBar mActionBar = null;
    TextView actionbarTextView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
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
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
//        ViewPagerAdapter adapter = new ViewPagerAdapter(this, );
        ChaptersModel model =
                (ChaptersModel) getIntent().getSerializableExtra(ChapterSelectionActivity.CHAPTERS_MODEL_INSTANCE);

        ArrayList<ChaptersModel> models = null;
        try {
            if (model == null) {
                if (AppVariable.MODELS != null) {
                    model = AppVariable.MODELS;
                    models = JsonParser.parseStory(model.jsonArray);
                }
            } else {

                models = JsonParser.parseStory(model.jsonArray);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

        actionbarTextView.setText(model.title);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, models, mImageLoader, model.next_chapter, model.number, actionbarTextView, getIntent(), model.loadedLanguage);
        readingViewPager.setAdapter(adapter);

        setupTouchListener(readingViewPager);
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


    private void handleDeviceOrientation(boolean hide){

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

    private void checkShouldChangeNavBarHidden(){

        boolean shouldHide = mActionBar.isShowing();

        handleDeviceOrientation(shouldHide);
    }
}
