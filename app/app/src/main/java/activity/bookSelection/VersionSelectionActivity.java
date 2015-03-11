package activity.bookSelection;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.VersionAdapter;
import model.datasource.LanguageDataSource;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Fechner on 2/27/15.
 */
public class VersionSelectionActivity extends GeneralSelectionActivity {

    static String VERSIONS_INDEX_STRING = "VERSIONS_INDEX_STRING";

    VersionAdapter adapter;

    private LanguageModel chosenLanguage = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_version_selector;
    }

    @Override
    protected ArrayList<String> getListOfLanguages() {

        if (chosenLanguage == null) {
            addLanguage();
        }

        return chosenLanguage.getAvailableLanguages(getApplicationContext());
    }

    @Override
    protected String getIndexStorageString() {
        return VERSIONS_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return ChapterSelectionActivity.class;
    }

    @Override
    protected String getActionBarTitle() {
        return chosenLanguage.projectName;
    }

    @Override
    protected void setUI() {
        super.setUI();

        LayoutInflater inflater = getLayoutInflater();
        View footerView = inflater.inflate(R.layout.footerview, null);


        // change version number
        TextView tView = (TextView) footerView.findViewById(R.id.textView);
        String versionName = BuildConfig.VERSION_NAME;

        tView.setText(versionName);
        mListView.addFooterView(footerView);

    }

    protected ArrayList<GeneralRowInterface> getData(){

        if (chosenLanguage == null) {
            addLanguage();
        }

        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.selected_language), "English");

        if(!chosenLanguage.languageName.equalsIgnoreCase(selectedLanguage)){
            LanguageModel correctLanguage = null;

            ArrayList<LanguageModel> languages = chosenLanguage.getParent(getApplicationContext()).getChildModels(getApplicationContext());

            for(LanguageModel model : languages){
                if(selectedLanguage.equalsIgnoreCase(model.languageName)){
                    correctLanguage = model;
                    break;
                }
            }
            if(correctLanguage != null){
                chosenLanguage = correctLanguage;
            }

        }

        ArrayList<VersionModel> versions = this.chosenLanguage.getChildModels(getApplicationContext());

        ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
        for(VersionModel model : versions){
            data.add(model);
        }

        return data;

    }

    private void addLanguage(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String chosenVersion = extras.getString(CHOSEN_ID);
            this.chosenLanguage = new LanguageDataSource(this.getApplicationContext()).getModel(chosenVersion);
        }
    }

    @Override
    protected void prepareListView(){

        //getting instance of ExpandableListView
        mListView = (ListView) findViewById(R.id.languageChooserExpandableListView);
        mListView.setOnItemClickListener(this);

        ArrayList<GeneralRowInterface> data = this.getData();

        if (data == null) {
            return;
        }

        adapter = new VersionAdapter(this, data, actionbarTextView, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);

        actionbarTextView.setText(getActionBarTitle());
    }

    @Override
    protected void storedValues() {

    }


}
