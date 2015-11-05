package activity.readingSelection;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.door43.tools.reporting.BugReporterActivity;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import activity.AnimationParadigm;
import activity.SettingsActivity;
import activity.UWBaseActivity;
import activity.reading.ReadingActivity;
import activity.reading.StoryReadingActivity;
import activity.sharing.ShareActivity;
import adapters.ShareAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.InitialPageAdapter;
import fragments.CheckingLevelInfoFragment;
import model.DaoDBHelper;
import model.SharingHelper;
import model.daoModels.Project;
import services.UWSideLoaderService;
import services.UWUpdaterService;
import utils.NetWorkUtil;
import utils.URLUtils;
import utils.UWPreferenceManager;
import view.UWTabBar;

/**
 * Created by PJ Fechner on 2/27/15.
 *
 */
public class InitialScreenActivity extends UWBaseActivity{

    static final private String TAG = "InitialScreenActivity";

    static final public String PROJECT_PARAM = "PROJECT_PARAM";
    static public final String GENERAL_CHECKING_LEVEL_FRAGMENT_ID = "GENERAL_CHECKING_LEVEL_FRAGMENT_ID";

    private ViewGroup updateLayout = null;

    private View mRefreshButton = null;
    private ListView listview;

    private InitialPageAdapter adapter;
    private List<Project> mProjects = null;
    private UWTabBar tabBar;
    /**
     * This broadcast for When the update is completed
     */
    private BroadcastReceiver updateReceiver;

    //region Activity Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_list);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UWPreferenceManager.getIsFirstLaunch(getApplicationContext())){
            showCheckingLevelFragment();
        }
    }

    @Override
    protected void onPause() {

        unregisterUpdateReceiver();

        super.onPause();
    }

    @Override
    public int getBackResource() {
        return -1;
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }
    @Override
    public void rightButtonClicked() {

        startSharingActivity();
    }

    //endregion

    //region Receiver Handling

    private BroadcastReceiver createReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(URLUtils.BROAD_CAST_DOWN_COMP)) {
                    Toast.makeText(context, "Update complete", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Update error", Toast.LENGTH_SHORT).show();
                }
                updateLayout.setVisibility(View.GONE);
                reload();
            }
        };
    }

    private void unregisterUpdateReceiver(){

        if(updateReceiver != null) {
            try {
                getApplicationContext().unregisterReceiver(updateReceiver);
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
        updateReceiver = null;
    }

    private void registerUpdateReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(URLUtils.BROAD_CAST_DOWN_COMP);
        filter.addAction(URLUtils.BROAD_CAST_DOWN_ERROR);

        if(updateReceiver == null) {
            updateReceiver = createReceiver();
        }

        registerReceiver(updateReceiver, filter);
    }

    //endregion

    //region Setup

    private void setupViews(){

        setupToolbar(true, getString(R.string.app_name), false);
        getToolbar().setRightImageFontAwesome(FontAwesomeIcons.fa_download);
        setupListView();
        addSettingsFooter();
        setupTabBar();
    }

    private void setupListView() {

        List<GeneralRowInterface> data = this.getData();

        if (listview == null) {
            listview = (ListView) findViewById(R.id.generalList);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startReadingActivity(mProjects.get(position - 1));
                }
            });
            setupRefreshButton();
        }

        if(adapter == null){
            adapter = new InitialPageAdapter(this.getApplicationContext(), data, -1);
            listview.setAdapter(adapter);
        } else{
            adapter.updateWithData(data);
        }
    }

    private void addSettingsFooter(){

        LayoutInflater inflater = getLayoutInflater();
        View footerView = inflater.inflate(R.layout.footer_settings, null);


        // change version number
         Button settingsButton = (Button) footerView.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
        footerView.findViewById(R.id.report_but_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReportBug();
            }
        });

        listview.addFooterView(footerView);
    }

    private void goToReportBug(){

        startActivity(new Intent(getApplicationContext(), BugReporterActivity.class));
    }

    private void setupRefreshButton() {

        if(mRefreshButton == null) {
            LayoutInflater inflater = getLayoutInflater();
            View mview1 = inflater.inflate(R.layout.refresh_header_view, null);
            updateLayout = (ViewGroup) mview1.findViewById(R.id.refreshView);
            mRefreshButton = mview1.findViewById(R.id.refreshButton);
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update();
                }
            });

            listview.addHeaderView(mview1);

            registerUpdateReceiver();
        }
    }

    protected List<GeneralRowInterface> getData(){

        if(mProjects == null){
            updateProjects();
        }

        if(mProjects == null || mProjects.size() < 1){
            return new ArrayList<>();
        }

        List<GeneralRowInterface> dataList = new ArrayList<>();
        for(Project row : mProjects) {
            dataList.add(new GeneralRowInterface.BasicGeneralRowInterface(row.getUniqueSlug(), row.getTitle()));
//            List<Language> langs = row.getLanguages();
        }

        return dataList;
    }

    //endregion

    //region updating

    private void update(){

        if (!NetWorkUtil.isConnected(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Failed connecting to the internet.");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            updateLayout.setVisibility(View.VISIBLE);
            // to handle new data from network
            startService(new Intent(getApplicationContext(), UWUpdaterService.class));
        }
    }

    private void reload(){

        mProjects = null;
        this.setupListView();
    }

    private void updateProjects() {

        mProjects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
    }

    //endregion

    //region Navigation actions

    private void goToSettings(){

        goToNewActivity(SettingsActivity.class);
    }

    private void startReadingActivity(Project project){

        Class nextActivity = (project.getUniqueSlug().equalsIgnoreCase(getString(R.string.open_bible_stories_slug)))?
                StoryReadingActivity.class : ReadingActivity.class;

        Intent newIntent = new Intent(this, nextActivity).putExtra(PROJECT_PARAM, project);

        goToNextActivity(newIntent);
    }

    private void showCheckingLevelFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        CheckingLevelInfoFragment fragment = new CheckingLevelInfoFragment();
        fragment.show(ft, GENERAL_CHECKING_LEVEL_FRAGMENT_ID);
        UWPreferenceManager.setIsFirstLaunch(getApplicationContext(), false);
    }

    private void startSharingActivity(){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        AlertDialog dialogue = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setCustomTitle(titleView)
                .setAdapter(new ShareAdapter(getApplicationContext(), Arrays.asList("Send/Save Versions", "Receive/Load Versions")),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0: {
                                        goToNewActivity(new Intent(getApplicationContext(), ShareActivity.class));
                                        break;
                                    }
                                    case 1: {
                                        startSideLoadActivity();
                                        break;
                                    }
                                    default: {
                                        dialog.cancel();
                                    }
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialogue.show();
    }

    private void startSideLoadActivity(){

        int enterAnimation = AnimationParadigm.getNextAnimationEnter(AnimationParadigm.ANIMATION_VERTICAL);
        int exitAnimation = AnimationParadigm.getNextAnimationExit(AnimationParadigm.ANIMATION_VERTICAL);
        startActivityForResult(SharingHelper.getIntentForLoading(getApplicationContext()), 0);

        overridePendingTransition(enterAnimation, exitAnimation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){

            Uri file = data.getData();
            if(file != null){
                loadVersion(file);
            }
        }
    }

    private void loadVersion(Uri file){

        setLoadingFragmentVisibility(true, "Loading Version File: " + file.getLastPathSegment(), false);
        registerPreloadReceiver();
        Intent intent = new Intent(getApplicationContext(), UWSideLoaderService.class)
                .setData(file);
        startService(intent);
        Log.i(TAG, "Version Loading Started");

    }

    private void registerPreloadReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(UWSideLoaderService.BROAD_CAST_SIDE_LOAD_SUCCESSFUL);
        registerReceiver(SideLoadReceiever, filter);
    }

    private void unRegisterPreloadReceiver(){

        unregisterReceiver(SideLoadReceiever);
    }

    private BroadcastReceiver SideLoadReceiever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unRegisterPreloadReceiver();
            setLoadingFragmentVisibility(false, "", false);
            showSuccessAlert(true);
        }
    };

    private void showSuccessAlert(boolean success){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Load Status");
        new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage((success) ? "Loading was successful" : "Loading failed")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void setupTabBar(){

        int[] images = {R.drawable.settings_icon_selector, R.drawable.update_icon_selector,
                R.drawable.share_icon_selector};

        tabBar = new UWTabBar(getApplicationContext(), images, (ViewGroup) findViewById(R.id.intial_activity_tab_bar_view), new UWTabBar.BottomBarListener() {
            @Override
            public void buttonPressedAtIndex(int index) {
                tabBarPressed(index);
            }
        });
    }

    private void tabBarPressed(int index){

        switch (index){
            case 0:{
                goToSettings();
                break;
            }
            case 1:{
                refre
                break;
            }
            case 2:{
                startSharingActivity();
                break;
            }
        }

    }



    //endregion
}
