package activity.bookSelection;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

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

/**
 * Created by Fechner on 2/27/15.
 */
public class ChapterSelectionActivity extends GeneralSelectionActivity{

    public static String CHAPTERS_INDEX_STRING = "CHAPTERS_INDEX_STRING";

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
}
