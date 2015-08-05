package activity.bookSelection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import activity.AnimationParadigm;
import activity.SettingsActivity;
import activity.UWBaseActivity;
import activity.reading.ReadingActivity;
import activity.reading.StoryReadingActivity;
import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.UWGeneralListAdapter;
import model.DaoDBHelper;
import model.daoModels.Language;
import model.daoModels.Project;
import services.UWUpdater;
import utils.NetWorkUtil;
import utils.URLUtils;

/**
 * Created by Fechner on 2/27/15.
 */
public class InitialScreenActivity extends UWBaseActivity{

    static final public String PROJECT_PARAM = "PROJECT_PARAM";

    static final String INDEX_STORAGE_STRING = "INDEX_STORAGE_STRING";

    static public final String GENERAL_CHECKING_LEVEL_FRAGMENT_ID = "GENERAL_CHECKING_LEVEL_FRAGMENT_ID";
    static final String STORIES_SLUG = "obs";
    public static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";

    private List<Project> mProjects = null;

    private FrameLayout visibleLayout = null;
    private Button mRefreshButton = null;
    private Button settingsButton = null;

    private ListView listview;
    UWGeneralListAdapter adapter;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_list_activity);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean firstLaunch = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(IS_FIRST_LAUNCH, true);
        if(firstLaunch){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            CheckingLevelFragment fragment = new CheckingLevelFragment();
            fragment.show(ft, GENERAL_CHECKING_LEVEL_FRAGMENT_ID);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(IS_FIRST_LAUNCH, false).commit();
        }
    }

    private void setupViews(){

        setupToolbar(true, getString(R.string.app_name), false);
        getToolbar().setRightImageVisible(true);
        setupListView();
        addSettingsFooter();
        setupRefreshButton();
    }

    private void reload(){

        mProjects = null;
        this.setupListView();
    }

    @Override
    public int getBackResource() {
        return -1;
    }

    protected void setupListView() {

        List<GeneralRowInterface> data = this.getData();

        if (listview == null) {
            listview = (ListView) findViewById(R.id.generalList);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    moveToNextActivity(mProjects.get(position - 1));
                }
            });
        }

        if(adapter == null){
            adapter = new UWGeneralListAdapter(this.getApplicationContext(), data, -1);
            listview.setAdapter(adapter);
        } else{
            adapter.updateWithData(data);
        }
    }

    private void addSettingsFooter(){
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
            listview.addFooterView(footerView);
        }
    }

    private void setupRefreshButton() {

        if(mRefreshButton == null) {
            LayoutInflater inflater = getLayoutInflater();
            View mview1 = inflater.inflate(R.layout.header_view, null);
            visibleLayout = (FrameLayout) mview1.findViewById(R.id.refreshView);
            mRefreshButton = (Button) mview1.findViewById(R.id.refreshButton);
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update();
                }
            });

            listview.addHeaderView(mview1);

            IntentFilter filter = new IntentFilter();
            filter.addAction(URLUtils.BROAD_CAST_DOWN_COMP);
            filter.addAction(URLUtils.BROAD_CAST_DOWN_ERROR);
            registerReceiver(receiver, filter);
        }
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }

    private void moveToSettings(){

        goToNewActivity(SettingsActivity.class);
    }

    protected List<GeneralRowInterface> getData(){

        if(mProjects == null){
            updateProjects();
        }

        if(mProjects == null || mProjects.size() < 1){
            return new ArrayList<GeneralRowInterface>();
        }

        List<GeneralRowInterface> dataList = new ArrayList<GeneralRowInterface>();
        for(Project row : mProjects) {
            dataList.add(new GeneralRowInterface.BasicGeneralRowInterface(row.getUniqueSlug(), row.getTitle()));
            List<Language> langs = row.getLanguages();
        }

        return dataList;
    }

    private void moveToNextActivity(Project project){

        Class nextActivity = (project.getUniqueSlug().equalsIgnoreCase(STORIES_SLUG))?
                StoryReadingActivity.class : ReadingActivity.class;

        Intent newIntent = new Intent(this, nextActivity).putExtra(PROJECT_PARAM, project);

        goToNextActivity(newIntent);
    }


    private void updateProjects() {

        mProjects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
    }

    private void update(){

        if (!NetWorkUtil.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Failed connecting to the internet.");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            visibleLayout.setVisibility(View.VISIBLE);
            // to handle new data from network
            startService(new Intent(getApplicationContext(), UWUpdater.class));
        }
    }

    @Override
    public void rightButtonClicked() {
        super.rightButtonClicked();
    }

    static public class CheckingLevelFragment extends DialogFragment {

        public CheckingLevelFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.verification_fragment, container, false);

            TextView tView = (TextView) view.findViewById(R.id.textView);
            String versionName = BuildConfig.VERSION_NAME;

            tView.setText(versionName);
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.verification_fragment_layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
            return view;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }
    }
}
