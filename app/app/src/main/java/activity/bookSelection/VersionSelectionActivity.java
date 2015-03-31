package activity.bookSelection;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import adapters.selectionAdapters.CollapsibleVersionAdapter;
import model.datasource.ProjectDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.VersionModel;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class VersionSelectionActivity extends ActionBarActivity {

    static final String VERSIONS_INDEX_STRING = "VERSIONS_INDEX_STRING";
    static final String STORIES_SLUG = "obs";

    CollapsibleVersionAdapter adapter;

    private View footerView = null;
    private ProjectModel chosenProject = null;

    private ActionBar mActionBar = null;

    protected ExpandableListView mListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_selector);
        setUI();
        prepareListView();
    }

    private void setUI() {

        View view = getLayoutInflater().inflate(R.layout.actionbar_base, null);
        setupActionBar(view);
        setupCloseButton(view);
    }
    private void setupActionBar(View view){

        mActionBar = getSupportActionBar();
        TextView actionbarTextView = (TextView) view.findViewById(R.id.actionbarTextView);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        actionbarTextView.setText("Select Version");
    }

    private void setupCloseButton(View view){
        FrameLayout closeButton = (FrameLayout) view.findViewById(R.id.close_image_view);
        closeButton.setVisibility(View.VISIBLE);
    }

    public void closeButtonClicked(View view) {
        handleBack();
    }

    private void addProject(){

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String chosenProjectId = extras.getString(GeneralSelectionActivity.CHOSEN_ID);
            this.chosenProject = new ProjectDataSource(this.getApplicationContext()).getModel(chosenProjectId);
        }
    }

    protected void prepareListView(){

        //getting instance of ExpandableListView
        mListView = (ExpandableListView) findViewById(R.id.versions_list);
//        mListView.setOnItemClickListener(this);

//        ArrayList<GeneralRowInterface> data = this.getData();

        if (chosenProject == null) {
            addProject();
        }

        adapter = new CollapsibleVersionAdapter(this, this.chosenProject);
        mListView.setAdapter(adapter);

        if(footerView == null) {
            LayoutInflater inflater = getLayoutInflater();
            footerView = inflater.inflate(R.layout.footerview, null);

            // change version number
            TextView tView = (TextView) footerView.findViewById(R.id.textView);
            String versionName = BuildConfig.VERSION_NAME;

            tView.setText(versionName);
            mListView.addFooterView(footerView);
        }

        int selectedIndex = 0;
        if(chosenProject.slug.equalsIgnoreCase(STORIES_SLUG)){

            String selectedVersion = UWPreferenceManager.getSelectedStoryVersion(getApplicationContext());
            if(Long.parseLong(selectedVersion) < 0){
                selectedIndex = 0;
            }
            else {
                VersionModel version = new VersionDataSource(getApplicationContext()).getModel(selectedVersion);

                for(int i = 0; i < chosenProject.getChildModels(getApplicationContext()).size(); i++){
                    if(chosenProject.getChildModels(getApplicationContext()).get(i).slug.equalsIgnoreCase(version.getParent(getApplicationContext()).slug)){
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }
        else{
            String selectedVersion = UWPreferenceManager.getSelectedBibleVersion(getApplicationContext());
            if(Long.parseLong(selectedVersion) < 0){
                selectedIndex = 0;
            }
            else {
                VersionModel version = new VersionDataSource(getApplicationContext()).getModel(selectedVersion);

                for(int i = 0; i < chosenProject.getChildModels(getApplicationContext()).size(); i++){
                    if(chosenProject.getChildModels(getApplicationContext()).get(i).slug.equalsIgnoreCase(version.getParent(getApplicationContext()).slug)){
                        selectedIndex = i;
                        break;
                    }
                }
            }
        }


        mListView.expandGroup(selectedIndex);
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareListView();
    }

    private void handleBack(){
        adapter.willDestroy();
        finish();
        overridePendingTransition(R.anim.enter_center, R.anim.exit_on_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        adapter.willDestroy();
        super.finish();
    }
}
