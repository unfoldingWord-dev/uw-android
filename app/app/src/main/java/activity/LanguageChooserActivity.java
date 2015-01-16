package activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapter.LanguageListAdapter;
import db.DBManager;
import models.LanguageModel;
import services.UpdateService;
import utils.NetWorkUtil;
import utils.URLUtils;


public class LanguageChooserActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static String LAGRANGE_DEP_NAME = "LAGRANGE_DEP_NAME";
    public static String LANGUAGE_CODE = "language_code";
    ListView mLanguageListView = null;
    ActionBar mActionBar = null;
    DBManager mDbManager = null;
    LanguageListAdapter adapter;
    TextView actionbarTextView = null;
    FrameLayout visibleLayout = null;
    Button mRefreshButton = null;
    /**
     * This broadcast for When the update is completed
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(URLUtils.BROAD_CAST_DOWN_COMP)) {
//                Toast.makeText(context, "Download comp", Toast.LENGTH_SHORT).show();

            } else {
//                Toast.makeText(context, "Download error", Toast.LENGTH_SHORT).show();
            }
            visibleLayout.setVisibility(View.GONE);
        }
    };

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
//        toolbar = (Toolbar) findViewById(R.id.mytoolbar);
//        setSupportActionBar(toolbar);
        LayoutInflater infi = getLayoutInflater();
        View actionView = infi.inflate(R.layout.actionbar_custom_view, null);
        actionbarTextView = (TextView) actionView.findViewById(R.id.actionbarTextView);


        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setCustomView(actionView);
////        mActionBar.setHomeButtonEnabled(false);
////        mActionBar.setDisplayHomeAsUpEnabled(false);
////        mActionBar.setLogo(R.drawable.ic_launcher);
        mActionBar.setDisplayShowCustomEnabled(true);


        //getting instance of ExpandableListView
        mLanguageListView = (ListView) findViewById(R.id.languageChooserExpandableListView);

        mLanguageListView.setOnItemClickListener(this);

        // inflating footer view
        LayoutInflater inflater = getLayoutInflater();
        View mview = inflater.inflate(R.layout.footerview, null);

        View mview1 = inflater.inflate(R.layout.header_view, null);
        visibleLayout = (FrameLayout) mview1.findViewById(R.id.refreshView);
        mRefreshButton = (Button) mview1.findViewById(R.id.refreshButton);
        mRefreshButton.setOnClickListener(this);
        mLanguageListView.addFooterView(mview);
        mLanguageListView.addHeaderView(mview1);

        mDbManager = DBManager.getInstance(this);

        // populate language data
        prepareListData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(URLUtils.BROAD_CAST_DOWN_COMP);
        filter.addAction(URLUtils.BROAD_CAST_DOWN_ERROR);
        registerReceiver(receiver, filter);


    }

    /**
     * Getting data from db and populating to ExpandableListView
     */
    private void prepareListData() {


        List<LanguageModel> models = mDbManager.getAllLanguages();

        adapter = new LanguageListAdapter(this, models, actionbarTextView, this);

        actionbarTextView.setText("Languages");

        mLanguageListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (itemAtPosition instanceof LanguageModel) {
            LanguageModel model = (LanguageModel) itemAtPosition;
            startActivity(new Intent(this, ChapterSelectionActivity.class).putExtra(LanguageChooserActivity.LANGUAGE_CODE, model.language));

            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareListData();
        String lan_name = PreferenceManager.getDefaultSharedPreferences(this).getString(LAGRANGE_DEP_NAME, "");
        if (lan_name != null) {
            if (!lan_name.equals("")) {
                actionbarTextView.setText(lan_name);
            } else {
                actionbarTextView.setText("Languages");
            }
        } else {
            actionbarTextView.setText("Languages");
        }
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

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}