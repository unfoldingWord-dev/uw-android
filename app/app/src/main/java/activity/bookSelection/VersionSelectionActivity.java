package activity.bookSelection;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import fragments.VersionSelectionFragment;
import model.DaoDBHelper;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class VersionSelectionActivity extends UWBaseActivity implements VersionSelectionFragment.VersionSelectionFragmentListener {

    public static final String PROJECT_PARAM = "PROJECT_PARAM";

    private Project chosenProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selection_activity);

        Bundle extras = getIntent().getExtras();
            if (extras != null) {

                chosenProject = (Project) extras.getSerializable(PROJECT_PARAM);
                setupToolbar(false, chosenProject.getTitle(), false);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.versions_frame, VersionSelectionFragment.newInstance(chosenProject, false))
                        .commit();
            }
    }


    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    @Override
    public void versionWasSelected(Version version) {
        UWPreferenceManager.selectedVersion(getApplicationContext(), version);
        handleBack();
    }
}
