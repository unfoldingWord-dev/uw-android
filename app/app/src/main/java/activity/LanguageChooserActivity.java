package activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ExpandableListView;

import org.distantshores.unfoldingword.R;

import java.util.List;

import adapter.LanguageExpandableListAdapter;
import db.DBManager;
import models.LanguageModel;


public class LanguageChooserActivity extends ActionBarActivity {
    ExpandableListView mLanguageExpandableListView = null;
    ActionBar mActionBar = null;
    DBManager mDbManager = null;
    LanguageExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);
        setUI();
    }

    /**
     * Default Initialization of components
     */
    private void setUI() {
        // setup actionbar
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setBackgroundDrawable(new ColorDrawable(R.color.brown));
        //getting instance of ExpandableListView
        mLanguageExpandableListView = (ExpandableListView) findViewById(R.id.languageChooserExpandableListView);
        // populate language data
        prepareListData();

    }

    /**
     * Getting data from db and populating to ExpandableListView
     */
    private void prepareListData() {

        /* todo */
        mDbManager = DBManager.getInstance(this);
        List<LanguageModel> models = mDbManager.getAllLanguages();
        adapter = new LanguageExpandableListAdapter(this, models);
        mLanguageExpandableListView.setAdapter(adapter);
    }
}
