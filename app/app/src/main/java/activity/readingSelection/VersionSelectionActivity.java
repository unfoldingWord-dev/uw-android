package activity.readingSelection;

import android.os.Bundle;

import org.unfoldingword.mobile.R;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import fragments.VersionSelectionFragment;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;

/**
 * Created by Fechner on 2/27/15.
 */
public class VersionSelectionActivity extends UWBaseActivity implements VersionSelectionFragment.VersionSelectionFragmentListener {

    public static final String PROJECT_PARAM = "PROJECT_PARAM";
    public static final String IS_SECOND_VERSION_PARAM = "IS_SECOND_VERSION_PARAM";

    private boolean isSecondVersion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selection_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            Project chosenProject = (Project) extras.getSerializable(PROJECT_PARAM);
            setupToolbar(false, chosenProject.getTitle(), false);

            if(extras.containsKey(IS_SECOND_VERSION_PARAM)){
                isSecondVersion = extras.getBoolean(IS_SECOND_VERSION_PARAM);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.versions_frame, VersionSelectionFragment.newInstance(chosenProject, false, isSecondVersion))
                    .commit();
        }
    }


    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    @Override
    public void versionWasSelected(Version version, boolean secondVersion) {
        UWPreferenceManager.selectedVersion(getApplicationContext(), version, secondVersion);
        handleBack();
    }
}
