package activity.bookSelection;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.ProjectModel;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class BookSelectionActivity extends GeneralSelectionActivity{

    static String BOOK_INDEX_STRING = "BOOK_INDEX_STRING";


    @Override
    protected int getContentView() {
        return R.layout.activity_general_list;
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
        GeneralAdapter adapter = new GeneralAdapter(this.getApplicationContext(), data, this.actionbarTextView, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);

        int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BOOK_INDEX_STRING, -1);
        mListView.setSelection((scrollPosition > 0) ? scrollPosition - 1 : 0);
    }

    @Override
    protected String getIndexStorageString() {
        return BOOK_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return BibleChapterSelectionActivity.class;
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

    protected ArrayList<GeneralRowInterface> getData(){

        Context context = getApplicationContext();

        String chapterId = UWPreferenceManager.getSelectedBibleChapter(context);
        if(Long.parseLong(chapterId) < 0) {
            return null;
        }
        else {
            BibleChapterModel model = new BibleChapterDataSource(context).getModel(chapterId);
            ArrayList<BookModel> books = model.getParent(context).getParent(context).getChildModels(context);

            long selectedId = model.getParent(context).uid;
            ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
            int i = 0;
            for (BookModel book : books) {
                if(book.uid == selectedId){
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(BOOK_INDEX_STRING, i).commit();
                }
                data.add(book);
                i++;
            }


            return data;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof GeneralRowInterface) {
            GeneralRowInterface model = (GeneralRowInterface) itemAtPosition;

            // put selected position  to sharedprefences
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(this.getIndexStorageString(), (int) rowIndex).commit();
            startActivityForResult(new Intent(this, this.getChildClass(model)).putExtra(
                    CHOSEN_ID, model.getChildIdentifier()), 1);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
        }
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
}
