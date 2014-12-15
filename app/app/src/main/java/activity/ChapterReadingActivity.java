package activity;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
        LayoutInflater infi = getLayoutInflater();
        View actionView = infi.inflate(R.layout.actionbar_custom_view, null);
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
}
