package activity.selectionActivities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import activity.ReadingActivity;
import activity.StoryReadingActivity;
import adapter.selectionAdapters.GeneralRowInterface;
import adapter.selectionAdapters.StoriesChapterAdapter;
import model.datasource.VersionDataSource;
import model.db.DBManager;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Fechner on 2/27/15.
 */
public class ChapterSelectionActivity extends GeneralSelectionActivity{

    static String CHAPTERS_INDEX_STRING = "VERSIONS_INDEX_STRING";

    private VersionModel chosenVersion = null;
    ImageLoader mImageLoader;
    boolean isStoryBook;

    @Override
    protected int getContentView() {
        return R.layout.activity_general_list;
    }

    @Override
    protected ArrayList<String> getListOfLanguages() {

        if (chosenVersion == null) {
            addVersion();
        }

    return chosenVersion.getAvailableLanguages(getApplicationContext());
}

    @Override
    protected String getIndexStorageString() {
        return CHAPTERS_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return (isStoryBook)? StoryReadingActivity.class : ReadingActivity.class;
    }

    @Override
    protected String getActionBarTitle() {
        return chosenVersion.name;
    }


    @Override
    protected void setUI() {
        if (chosenVersion == null) {
            addVersion();
        }
        isStoryBook = (chosenVersion.usfmUrl.length() < 2);
        super.setUI();
    }

    @Override
    protected void prepareListView() {

        if(!isStoryBook){
            super.prepareListView();
        }
        else {

                BookModel book = this.chosenVersion.getStoriesChildModels(getApplicationContext()).get(0);
                ArrayList<GeneralRowInterface> chapterModels = this.getData();

                if (chapterModels != null) {
                    actionbarTextView.setText(book.appWords.chapters);

                    mListView = (ListView) findViewById(R.id.generalList);
                    mListView.setOnItemClickListener(this);

                    mImageLoader = ImageLoader.getInstance();

                    if (mImageLoader.isInited()) {
                        ImageLoader.getInstance().destroy();
                    }

                    mImageLoader.init(ImageLoaderConfiguration.createDefault(this));

                    mListView.setAdapter(new StoriesChapterAdapter(getApplicationContext(), chapterModels, this.actionbarTextView, this, mImageLoader, this.getIndexStorageString()));
                }
            }
        }

    @Override
    protected ArrayList<GeneralRowInterface> getData(){

        if (chosenVersion == null) {
            addVersion();
        }

        checkForLanguageChange();


        ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();

        if(isStoryBook){
            ArrayList<StoriesChapterModel> chapters = this.chosenVersion.getStoriesChildModels(getApplicationContext()).get(0).getChildModels(getApplicationContext());
            Collections.sort(chapters);

            for(StoriesChapterModel model : chapters){
                data.add(model);
            }
        }

        else{
            ArrayList<BibleChapterModel> chapters = this.chosenVersion.getBibleChildModels(getApplicationContext());
            Collections.sort(chapters);

            for(BibleChapterModel model : chapters){
                data.add(model);
            }
        }

        return data;
    }

    private void checkForLanguageChange(){

        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.selected_language), "English");

        if(!chosenVersion.getParent(getApplicationContext()).languageName.equalsIgnoreCase(selectedLanguage)){
            LanguageModel correctLanguage = null;

            ArrayList<LanguageModel> languages = chosenVersion.getParent(getApplicationContext()).getParent(getApplicationContext()).getChildModels(getApplicationContext());

            for(LanguageModel model : languages){
                if(selectedLanguage.equalsIgnoreCase(model.languageName)){
                    correctLanguage = model;
                    break;
                }
            }
            if(correctLanguage != null){
                chosenVersion = correctLanguage.getChildModels(getApplicationContext()).get(0);
            }

        }
    }

    private void addVersion(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String chosenVersion = extras.getString(CHOSEN_ID);
            VersionModel version = (new VersionDataSource(this.getApplicationContext())).getModel(chosenVersion);

            this.chosenVersion = version;
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(!isStoryBook){
//            return super.onOptionsItemSelected(item);
//        }
//
//
//        if (item.getItemId() == android.R.id.home) {
//            storedValues();
//            //reset  Preference
//            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(getIndexStorageString(), -1).commit();
//            finish();
//            overridePendingTransition(R.anim.left_in, R.anim.right_out);
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if(!isStoryBook){
//            super.onItemClick(adapterView, view, i, l);
//            return;
//        }
//
//        Object itemAtPosition = adapterView.getItemAtPosition(i);
//        if (itemAtPosition instanceof ChapterModel) {
//            ChapterModel model = (ChapterModel) itemAtPosition;
//
//            // put selected position  to sharedprefences
//            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(getIndexStorageString(), i).commit();
//            ImageLoader.getInstance().destroy();
//            startActivity(new Intent(this, StoryReadingActivity.class).putExtra(
//                    LanguageChooserActivity.LANGUAGE_CODE, model.language).putExtra(
//                    SELECTED_CHAPTER_POS, model.number));
//            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
//        }
//    }

    @Override
    protected void storedValues() {

    }


}
