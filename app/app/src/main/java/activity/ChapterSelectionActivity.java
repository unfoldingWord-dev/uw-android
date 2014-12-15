package activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import adapter.ChapterAdapter;
import db.DBManager;
import models.ChaptersModel;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class ChapterSelectionActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    public static final String SELECTED_CHAPTER_POS = "SELECTED_CHAPTER_POS";
    public static String CHAPTERS_MODEL_INSTANCE = "CHAPTERS_MODEL_INSTANCE";
    ListView mChapterListView = null;
    DBManager mDbManager = null;
    ImageLoader mImageLoader;
    ActionBar mActionBar = null;
    TextView actionbarTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_screen);
        setUI();
    }

    /**
     * Setup UI components and initial statements
     */
    private void setUI() {

        mActionBar = getSupportActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);
        actionbarTextView = (TextView) view.findViewById(R.id.actionbarTextView);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mDbManager = DBManager.getInstance(this);
        prepareListView();
    }

    private void prepareListView() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String languageName = extras.getString(LanguageChooserActivity.LANGUAGE_CODE);
            ArrayList<ChaptersModel> chaptersModels = null;
            try {
                chaptersModels = mDbManager.getAllChapters(languageName);
                actionbarTextView.setText(chaptersModels.get(0).chapters);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }

            mChapterListView = (ListView) findViewById(R.id.chapterListView);
            mChapterListView.setOnItemClickListener(this);
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
            mChapterListView.setAdapter(new ChapterAdapter(this, chaptersModels, mImageLoader));
        }

    }

    @Override
    public void onBackPressed() {
        storedValues();
        //reset  Preference
//        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(SELECTED_CHAPTER_POS, -1).commit();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void storedValues() {
        if (mDbManager != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String languageName = extras.getString(LanguageChooserActivity.LANGUAGE_CODE);
                String dispName = mDbManager.getLanguageDisp(languageName);
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(LanguageChooserActivity.LAGRANGE_DEP_NAME, dispName).commit();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            storedValues();
            //reset  Preference
//            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(SELECTED_CHAPTER_POS, -1).commit();
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (itemAtPosition instanceof ChaptersModel) {
            ChaptersModel model = (ChaptersModel) itemAtPosition;
            // put selected position  to sharedprefences
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(SELECTED_CHAPTER_POS, i).commit();
            startActivity(new Intent(this, ChapterReadingActivity.class).putExtra(CHAPTERS_MODEL_INSTANCE, model));
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareListView();

// scroll previous selected
        int pos = PreferenceManager.getDefaultSharedPreferences(this).getInt(SELECTED_CHAPTER_POS, -1);
        if (pos != -1) {
            if (mChapterListView != null) {
                mChapterListView.setSelection(pos);
            }
        }
    }

}
