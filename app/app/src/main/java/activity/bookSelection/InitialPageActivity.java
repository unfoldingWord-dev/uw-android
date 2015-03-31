package activity.bookSelection;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import activity.SettingsActivity;
import activity.SplashScreenActivity;
import activity.reading.ReadingActivity;
import activity.reading.StoryReadingActivity;
import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.InitialPageModel;
import model.datasource.ProjectDataSource;
import model.modelClasses.mainData.ProjectModel;
import services.UpdateService;
import utils.NetWorkUtil;
import utils.URLUtils;

/**
 * Created by Fechner on 2/27/15.
 */
public class InitialPageActivity extends GeneralSelectionActivity implements View.OnClickListener {

    static final String INDEX_STORAGE_STRING = "INDEX_STORAGE_STRING";

    static final String STORIES_SLUG = "obs";

    ArrayList<ProjectModel> mProjects = null;

    FrameLayout visibleLayout = null;
    Button mRefreshButton = null;
    Button settingsButton = null;
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
        this.prepareListView();
    }

    @Override
    protected void setUI() {

        setupActionBar();
        this.prepareListView();
    }

    private void setupActionBar(){

        mActionBar = getSupportActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_base, null);
        TextView actionbarTextView = (TextView) view.findViewById(R.id.actionbarTextView);
        mActionBar.setCustomView(view);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        actionbarTextView.setText(getActionBarTitle());
    }

//    @Override
    protected String getActionBarTitle() {
        return getResources().getString(R.string.app_name);
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

    private void moveToSettings(){

        startActivity(new Intent(this, SettingsActivity.class));

    }

//    @Override
    protected void prepareListView() {

        ArrayList<GeneralRowInterface> data = this.getData();

        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.generalList);
        }
        mListView.setOnItemClickListener(this);

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

        if (data == null) {
//            return;
            // test code for adding database.
            data = new ArrayList<GeneralRowInterface>();
//            data.add(new InitialPageModel("test", "-1"));
        }
        mListView.setAdapter(new GeneralAdapter(this.getApplicationContext(), data, this.actionbarTextView, this, this.getIndexStorageString()));

        if(settingsButton == null) {
            LayoutInflater inflater = getLayoutInflater();
            View footerView = inflater.inflate(R.layout.settings_footer, null);


            // change version number
            settingsButton = (Button) footerView.findViewById(R.id.settings_button);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToSettings();
                }
            });
            mListView.addFooterView(footerView);
        }
    }

    @Override
    protected int getContentView() {
         return R.layout.activity_general_list;
    }

    protected ArrayList<GeneralRowInterface> getData(){

        Map<String, GeneralRowInterface> data = new HashMap<String, GeneralRowInterface>();

        if(mProjects == null){
            addProjects();
        }

        if(mProjects == null || mProjects.size() < 1){
//            Intent refresh = new Intent(this, SplashScreenActivity.class);
//            startActivity(refresh);
//            this.finish();
            return null;
        }

        ArrayList<GeneralRowInterface> dataList = new ArrayList<GeneralRowInterface>(3);
        for(ProjectModel row : mProjects) {
            dataList.add(row);
        }

        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof GeneralRowInterface) {
           PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(INDEX_STORAGE_STRING, (int) rowIndex);

            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(
                    this.getIndexStorageString(), (int) rowIndex).commit();

            ProjectModel model = (ProjectModel) itemAtPosition;
            moveToNextActivity(model);
        }
    }

    private void moveToNextActivity(ProjectModel project){

        Class nextActivity = (project.slug.equalsIgnoreCase(STORIES_SLUG))?
                StoryReadingActivity.class : ReadingActivity.class;

        startActivity(new Intent(this, nextActivity).putExtra(CHOSEN_ID, project.getChildIdentifier()));
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
    }


    private void addProjects() {

        if(mProjects == null){
            mProjects = new ArrayList<ProjectModel>();
            mProjects = new ProjectDataSource(this.getApplicationContext()).getAllProjects();
        }
    }

    @Override
    public void onClick(View view) {
        if (!NetWorkUtil.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Failed connecting to the internet.");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            visibleLayout.setVisibility(View.VISIBLE);
            // to handle new data from network
            startService(new Intent(this, UpdateService.class));
        }

    }


    public void closeButtonClicked(View view) {
    }
}
