package activity.readingSelection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import fragments.ChapterSelectionFragment;
import fragments.ChapterSelectionFragmentListener;
import fragments.StoryChaptersFragment;
import model.daoModels.Project;

/**
 * Created by Fechner on 2/27/15.
 */
public class BookSelectionActivity extends UWBaseActivity implements ChapterSelectionFragmentListener {

    public static final String PROJECT_PARAM = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_activity);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.versions_frame, getFragment())
                    .commit();
        }
        setUI();
    }

    private Fragment getFragment(){

        Project project = (Project) getIntent().getSerializableExtra(PROJECT_PARAM);

        boolean isStories = project.getUniqueSlug().contains("obs");
        if(isStories){
            return StoryChaptersFragment.newInstance(false);
        }
        else{
            return ChapterSelectionFragment.newInstance(false);
        }
    }

    protected void setUI() {

        setupToolbar(false, "Select Book", false);
    }

    public void closeButtonClicked(View view) {
        handleBack();
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == 1){
            finish();
        }
    }

    @Override
    public void chapterWasSelected() {
        handleBack();
    }
}
