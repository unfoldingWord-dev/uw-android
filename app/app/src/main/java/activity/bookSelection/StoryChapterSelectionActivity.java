package activity.bookSelection;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import activity.reading.ReadingActivity;
import activity.reading.StoryReadingActivity;
import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.StoriesChapterAdapter;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class StoryChapterSelectionActivity extends GeneralSelectionActivity{

    public static String CHAPTERS_INDEX_STRING = "CHAPTERS_INDEX_STRING";

//    private VersionModel chosenVersion = null;
    ImageLoader mImageLoader;

    @Override
    protected int getContentView() {
        return R.layout.activity_general_list;
    }

    @Override
    protected String getIndexStorageString() {
        return CHAPTERS_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return null;
    }

    @Override
    protected void setUI() {

        View view = getLayoutInflater().inflate(R.layout.actionbar_base, null);
        setupActionBar(view);
        setupCloseButton(view);
    }

    private void setupActionBar(View view){

        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);

        actionbarTextView = (TextView) view.findViewById(R.id.actionbarTextView);
        actionbarTextView.setText("Select Chapter");
    }

    private void setupCloseButton(View view){
        FrameLayout closeButton = (FrameLayout) view.findViewById(R.id.close_image_view);
        closeButton.setVisibility(View.VISIBLE);
    }

    public void closeButtonClicked(View view) {
        handleBack();
    }

    @Override
    protected void prepareListView() {

        ArrayList<GeneralRowInterface> chapterModels = this.getData();
//        BookModel book = this.chosenVersion.getChildModels(getApplicationContext()).get(0);

        if (chapterModels != null) {
            actionbarTextView.setText("Choose Chapter");

            mListView = (ListView) findViewById(R.id.generalList);
            mListView.setOnItemClickListener(this);

            mImageLoader = ImageLoader.getInstance();

            if (mImageLoader.isInited()) {
                ImageLoader.getInstance().destroy();
            }

            mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
            mListView.setAdapter(new StoriesChapterAdapter(getApplicationContext(), chapterModels, this.actionbarTextView, this, mImageLoader, this.getIndexStorageString()));

            int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(CHAPTERS_INDEX_STRING, -1);
            mListView.setSelection((scrollPosition > 0)? scrollPosition - 1 : 0);
        }
    }


    protected ArrayList<GeneralRowInterface> getData(){

        String versionId = UWPreferenceManager.getSelectedStoryVersion(getApplicationContext());

        VersionModel version = new VersionDataSource(getApplicationContext()).getModel(versionId);

        ArrayList<StoriesChapterModel> chapters = version.getChildModels(
                getApplicationContext()).get(0).getStoryChildModels(getApplicationContext());
        Collections.sort(chapters);

        long chapterId = Long.parseLong(UWPreferenceManager.getSelectedStoryChapter(getApplicationContext()));

        ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
        int i = 0;
        for(StoriesChapterModel model : chapters){
            if(chapterId == model.uid){
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(CHAPTERS_INDEX_STRING, i).commit();
            }
            data.add(model);
            i++;
        }

        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);

        if (itemAtPosition instanceof StoriesChapterModel) {
            StoriesChapterModel model = (StoriesChapterModel) itemAtPosition;

            UWPreferenceManager.setSelectedStoryChapter(getApplicationContext(), model.uid);
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(StoryReadingActivity.STORY_INDEX_STRING, -1).commit();
        handleBack();
    }

    @Override
    protected void handleBack(){

        finish();
        overridePendingTransition(R.anim.enter_center, R.anim.exit_on_bottom);
    }

}
