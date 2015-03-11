package activity.selectionActivities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import activity.SettingsActivity;
import adapter.selectionAdapters.GeneralAdapter;
import adapter.selectionAdapters.GeneralRowInterface;
import adapter.selectionAdapters.InitialPageModel;
import model.datasource.ProjectDataSource;
import model.db.DBManager;
import model.modelClasses.mainData.LanguageModel;
import model.modelClasses.mainData.ProjectModel;
import services.UpdateService;
import utils.NetWorkUtil;
import utils.URLUtils;

/**
 * Created by Fechner on 2/27/15.
 */
public class InitialPageActivity extends GeneralSelectionActivity implements View.OnClickListener {

    static String INDEX_STORAGE_STRING = "INDEX_STORAGE_STRING";

    ArrayList<ProjectModel> mProjects = null;

    FrameLayout visibleLayout = null;
    Button mRefreshButton = null;
    /**
     * This broadcast for When the update is completed
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(URLUtils.BROAD_CAST_DOWN_COMP)) {
                Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
            }
            visibleLayout.setVisibility(View.GONE);
            reload();
        }
    };

    private void reload(){

        mProjects = null;
        mDbManager = DBManager.getInstance(getApplicationContext());
        this.updateListView();
    }

    @Override
    protected String getActionBarTitle() {
        return "Unfolding Word";
    }

    @Override
    protected String getIndexStorageString() {
        return INDEX_STORAGE_STRING;
    }

    @Override
    protected Class getChildClass() {
        return BookSelectionActivity.class;
    }

    @Override
    protected Class getChildClass(GeneralRowInterface row){

        String aClass = row.getClass().toString();

        if(aClass.contains("Language")){
            return VersionSelectionActivity.class;
        }
        return this.getChildClass();
    }


    @Override
    protected void setUI() {
        super.setUI();

        LayoutInflater inflater = getLayoutInflater();
        View footerView = inflater.inflate(R.layout.settings_footer, null);


        // change version number
        Button footerButton = (Button) footerView.findViewById(R.id.settings_button);
        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSettings();
            }
        });
        mListView.addFooterView(footerView);
    }

    private void moveToSettings(){

        startActivity(new Intent(this, SettingsActivity.class));

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

        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);

        if(mRefreshButton == null) {
            LayoutInflater inflater = getLayoutInflater();
            View mview1 = inflater.inflate(R.layout.header_view, null);
            visibleLayout = (FrameLayout) mview1.findViewById(R.id.refreshView);
            mRefreshButton = (Button) mview1.findViewById(R.id.refreshButton);
            mRefreshButton.setOnClickListener(this);
            mListView.addHeaderView(mview1);
            IntentFilter filter = new IntentFilter();
            filter.addAction(URLUtils.BROAD_CAST_DOWN_COMP);
            filter.addAction(URLUtils.BROAD_CAST_DOWN_ERROR);
            registerReceiver(receiver, filter);
        }

        mListView.setAdapter(new GeneralAdapter(this.getApplicationContext(), data, this.actionbarTextView, this, this.getIndexStorageString()));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onlySuperOnWindowFocusChanged(hasFocus);
        int width = this.languagesButton.getMeasuredWidth();
        actionbarTextView.setPadding(width, 0, 5, 0);
    }

    @Override
    protected int getContentView() {
         return R.layout.activity_general_list;
    }

    @Override
    protected ArrayList<String> getListOfLanguages() {
        return DBManager.getAvailableLanguages(getApplicationContext());
    }

    @Override
    protected ArrayList<GeneralRowInterface> getData(){

        Map<String, GeneralRowInterface> data = new HashMap<String, GeneralRowInterface>();

        if(mProjects == null){
            addProjects();
        }

        if(mProjects.size() < 1){

            // this if for an initial run.
            reload();
            return null;
        }

        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getResources().getString(R.string.selected_language), "English");

        Context context = getApplicationContext();

        for(ProjectModel project : mProjects){
            if(project.meta.contains("Stories")){
                ArrayList<LanguageModel> languages = project.getChildModels(context);

                for(LanguageModel language : languages){
                    if(language.languageName.equalsIgnoreCase(selectedLanguage)){
                        data.put(language.projectName, language);
                    }
                }
            }
            else if(project.containsLanguage(selectedLanguage, context)) {

                String label = project.meta;

                if(label.equalsIgnoreCase("bible-nt")){
                    label = "Bible New Testament";
                }
                if(label.equalsIgnoreCase("bible-ot")){
                    label = "Bible Old Testament";
                }

                data.put(project.meta, new InitialPageModel(label, project.meta));
            }

        }

        ArrayList<GeneralRowInterface> dataList = new ArrayList<GeneralRowInterface>(3);
        for(GeneralRowInterface row : data.values()) {
            dataList.add(row);
        }

        for(GeneralRowInterface row : data.values()){
            int index = 0;

            if(row.getChildIdentifier().contains("bible-nt")){
                index = dataList.size() - 1;
            }
            else if(row.getChildIdentifier().contains("bible-ot")){
                index = dataList.size() - 2;
            }
            else{
                index = 0;
            }
            if(index < 0)
            {
                index = 0;
            }
            dataList.set(index, row);
        }



        return dataList;
    }

    private void addProjects() {


        if(mProjects == null){
            mProjects = new ArrayList<ProjectModel>();
            mProjects = new ProjectDataSource(this.getApplicationContext()).getAllProjects();
        }
    }

    @Override
    protected void storedValues() {

    }

    @Override
    public void onClick(View view) {
        if (!NetWorkUtil.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Unable to perform update at this time");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            visibleLayout.setVisibility(View.VISIBLE);
            // to handle new data from network
            startService(new Intent(this, UpdateService.class));
        }

    }

}
