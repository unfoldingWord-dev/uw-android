package activity.bookSelection;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Locale;

import activity.SplashScreenActivity;
import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.database.DBManager;
import view.popover.ActionItem;
import view.popover.QuickAction;

/**
 * Created by Fechner on 3/3/15.
 */
public abstract class GeneralSelectionActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    static public String CHOSEN_ID = "CHOSEN_ID";

    protected static String SELECTED_POS;

    protected ListView mListView = null;
    protected ActionBar mActionBar = null;
    protected TextView actionbarTextView = null;


    /**
     * should return the main view for the activity
     * @return
     */
    abstract protected int getContentView();

    /**
     * should return a unique index storage key
     * @return
     */
    abstract protected String getIndexStorageString();

    /**
     * should return the child class for which to animate when a row is selected, or null if there is none.
     * @return
     */
    abstract protected Class getChildClass();

    protected Class getChildClass(GeneralRowInterface row){
        return this.getChildClass();
    }

    /**
     * Setup UI components and initial statements
     */
    abstract protected void setUI();// {
//
//        mActionBar = getSupportActionBar();
//        View view = getLayoutInflater().inflate(R.layout.actionbar_custom_view, null);
//        actionbarTextView = (TextView) view.findViewById(R.id.actionbarTextView);
//        mActionBar.setCustomView(view);
//        mActionBar.setDisplayShowCustomEnabled(true);
//        mActionBar.setDisplayShowHomeEnabled(true);
//        mActionBar.setHomeButtonEnabled(true);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//
//        mDbManager = DBManager.getInstance(this);
//        languagesButton = (Button) view.findViewById(R.id.languageButton);
//        languagesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addPopover(v);
//            }
//        });
//        languagesButton.setText(getCurrentLanguage());
//    }



//    protected void addPopover(View view) {
//
//        //create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout
//        //orientation
//        final QuickAction quickAction = new QuickAction(this, QuickAction.VERTICAL);
//        final ArrayList<String> languages = getListOfLanguages();
//
//        String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.selected_language), "English");
//        int i = 0;
//        for (String language : languages) {
//
//            Locale languageLocal = new Locale(language);
//
//            ActionItem item = new ActionItem(i, languageLocal.getDisplayLanguage());
//
//            item.setSticky(false);
//            item.setIcon(getResources().getDrawable(R.drawable.checkmark));
//            item.setImageEnabled(selectedLanguage.equalsIgnoreCase(language));
//
//            quickAction.addActionItem(item);
//            i++;
//        }
//
//        //Set listener for action item clicked
//        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
//            @Override
//            public void onItemClick(QuickAction source, int pos, int actionId) {
//                //here we can filter which action item was clicked with pos or actionId parameter
//                ActionItem actionItem = quickAction.getActionItem(pos);
//                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getResources().getString(R.string.selected_language), actionItem.getTitle()).commit();
//                String selectedLanguage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getResources().getString(R.string.selected_language), "English");
//
//                int i = 0;
//                for (String language : languages) {
//
//                    Locale languageLocal = new Locale(language);
//
//                    ActionItem item = new ActionItem(i, languageLocal.getDisplayLanguage());
//
//                    item.setSticky(false);
//                    item.setIcon(getResources().getDrawable(R.drawable.checkmark));
//                    item.setImageEnabled(selectedLanguage.equalsIgnoreCase(language));
//
//                    quickAction.setActionItem(i, item);
//                    i++;
//                }
//
////                languagesButton.setText(selectedLanguage);
//                Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
//
//                updateListView();
//            }
//        });
//
//        //set listener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
//        //by clicking the area outside the dialog.
//        quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
//            @Override
//            public void onDismiss() {
////                Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        quickAction.show(view);
//
//    }

//    protected void updateListView() {
//        this.prepareListView();
//    }

    abstract protected void prepareListView();// {
//
////        ArrayList<GeneralRowInterface> data = this.getData();
//
//        if (mListView == null) {
//            mListView = (ListView) findViewById(R.id.generalList);
//        }
//        if (data == null) {
//            return;
//        }
//        else if (data != null || data.size() == 0) {
//            actionbarTextView.setText(getActionBarTitle());
//        }
//
//        mListView.setOnItemClickListener(this);
//        GeneralAdapter adapter = new GeneralAdapter(this.getApplicationContext(), data, this.actionbarTextView, this, this.getIndexStorageString());
//        mListView.setAdapter(adapter);
//
//        int currentItem = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(getIndexStorageString(), -1);
//
//        if(currentItem >= 0) {
//            mListView.setSelection(currentItem);
//        }
//    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUI();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof GeneralRowInterface) {
            GeneralRowInterface model = (GeneralRowInterface) itemAtPosition;

            // put selected position  to sharedprefences
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(this.getIndexStorageString(), (int) rowIndex).commit();
            startActivity(new Intent(this, this.getChildClass(model)).putExtra(
                    CHOSEN_ID, model.getChildIdentifier()));
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
//        int width = this.languagesButton.getMeasuredWidth();
//        actionbarTextView.setPadding(width - 100, 0, 20, 0);
    }

    public void onlySuperOnWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
    }

    protected void handleBack(){

        //reset  Preference
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(getIndexStorageString(), -1).commit();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}
