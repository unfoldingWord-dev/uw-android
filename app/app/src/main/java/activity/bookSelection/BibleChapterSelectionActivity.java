package activity.bookSelection;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import activity.reading.ReadingActivity;
import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.datasource.BookDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class BibleChapterSelectionActivity extends GeneralSelectionActivity{

    public static String STORY_CHAPTERS_INDEX_STRING = "STORY_CHAPTERS_INDEX_STRING";

    private int resultCode = 0;

    @Override
    protected int getContentView() {
        return R.layout.activity_general_list;
    }

    @Override
    protected String getIndexStorageString() {
        return STORY_CHAPTERS_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return null;
    }

    protected String getActionBarTitle() {
        return "Select Chapter";
    }


    @Override
    protected void setUI() {
        setupActionBar();
    }

    private void setupActionBar(){

        mActionBar = getSupportActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_base, null);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        actionbarTextView = (TextView) view.findViewById(R.id.actionbarTextView);
        actionbarTextView.setText(getActionBarTitle());
    }


    @Override
    protected void prepareListView() {

        ArrayList<GeneralRowInterface> data = this.getData();

        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.generalList);
        }
        if (data == null) {
            return;
        }
        else if (data != null || data.size() == 0) {
            actionbarTextView.setText(getActionBarTitle());
        }

        mListView.setOnItemClickListener(this);
        GeneralAdapter adapter = new GeneralAdapter(this.getApplicationContext(), data, this.actionbarTextView, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);

        int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(STORY_CHAPTERS_INDEX_STRING, -1);
        mListView.setSelection((scrollPosition > 0)? scrollPosition - 1 : 0);
    }

    protected ArrayList<GeneralRowInterface> getData(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String chosenBook = extras.getString(CHOSEN_ID);

            BookModel book = new BookDataSource(getApplicationContext()).getModel(chosenBook);

            ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();

            ArrayList<BibleChapterModel> chapters = book.getBibleChildModels(getApplicationContext());
            Collections.sort(chapters);

            long chapterId = Long.parseLong(UWPreferenceManager.getSelectedBibleChapter(getApplicationContext()));

            int i = 0;
            for (BibleChapterModel model : chapters) {
                if(model.uid == chapterId){
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(STORY_CHAPTERS_INDEX_STRING, i).commit();
                }
                data.add(model);
                i++;
            }
            return data;
        }

        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);

        if (itemAtPosition instanceof BibleChapterModel) {
            BibleChapterModel model = (BibleChapterModel) itemAtPosition;

            UWPreferenceManager.setSelectedBibleChapter(getApplicationContext(), model.uid);
        }
        resultCode = 1;
        setResult(resultCode);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(ReadingActivity.BOOK_INDEX_STRING, -1).commit();
        finish();
        overridePendingTransition(R.anim.enter_center, R.anim.exit_on_bottom);
    }

    @Override
    protected void onStop() {
        setResult(resultCode);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        setResult(resultCode);
        super.onDestroy();
    }

    public void closeButtonClicked(View view) {
    }
}
