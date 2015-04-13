package activity.bookSelection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import fragments.BooksFragment;
import fragments.ChapterSelectionFragment;
import model.datasource.BibleChapterDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class BookSelectionActivity extends GeneralSelectionActivity implements ChapterSelectionFragment.ChapterSelectionListener{

    static String BOOK_INDEX_STRING = "BOOK_INDEX_STRING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_activity);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.versions_frame, ChapterSelectionFragment.newInstance(false))
                    .commit();
        }
        setUI();
    }

    @Override
    protected void prepareListView() {

    }

    @Override
    protected int getContentView() {
        return -1;
    }

    @Override
    protected String getIndexStorageString() {
        return BOOK_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return null;
    }

    protected String getActionBarTitle() {
        return "Select Book";//mProjects.get(0).meta;
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
        actionbarTextView.setText(getActionBarTitle());
    }

    private void setupCloseButton(View view){
        FrameLayout closeButton = (FrameLayout) view.findViewById(R.id.close_image_view);
        closeButton.setVisibility(View.VISIBLE);
    }

    public void closeButtonClicked(View view) {
        handleBack();
    }


    @Override
    protected void handleBack(){

        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(getIndexStorageString(), -1).commit();
        finish();
        overridePendingTransition(R.anim.enter_center, R.anim.exit_on_bottom);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == 1){
            finish();
        }
    }

    @Override
    public void selectionFragmentChoseChapter() {
        handleBack();
    }
}
