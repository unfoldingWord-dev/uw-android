package activity.textSelection;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import fragments.StoryChaptersFragment;

/**
 * Created by Fechner on 2/27/15.
 */
public class StoryChapterSelectionActivity extends GeneralSelectionActivity implements StoryChaptersFragment.StoryChaptersFragmentListener{

    public static String CHAPTERS_INDEX_STRING = "CHAPTERS_INDEX_STRING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_activity);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.versions_frame, StoryChaptersFragment.newInstance(false))
                    .commit();

        }
        setUI();
    }

    @Override
    protected int getContentView() {
        return -1;
    }

    @Override
    protected String getIndexStorageString() {
        return CHAPTERS_INDEX_STRING;
    }

    @Override
    protected Class getChildClass() {
        return null;
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

        actionbarTextView = (TextView) view.findViewById(R.id.actionbar_text_view);
        actionbarTextView.setText("Select Chapter");
    }

    private void setupCloseButton(View view){
        FrameLayout closeButton = (FrameLayout) view.findViewById(R.id.close_image_view);
        closeButton.setVisibility(View.VISIBLE);
    }

    public void closeButtonClicked(View view) {
        handleBack();
    }

    @Override
    protected void prepareListView() {

    }

    @Override
    protected void handleBack(){

        finish();
        overridePendingTransition(R.anim.enter_center, R.anim.exit_on_bottom);
    }


    @Override
    public void chapterWasSelected() {
        handleBack();
    }
}

