package activity.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import adapters.VersionShareAdapter;
import model.DaoDBHelper;
import model.SharingHelper;
import model.daoModels.Version;

public class ShareActivity extends UWBaseActivity implements VersionShareAdapter.VersionAdapterListener {

    private static final String TAG = "ShareActivity";
    private ShareSelectionFragment selectionFragment;

    Version[] versions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setupToolbar(false, getString(R.string.app_name), false);
        setupData();
        addFragment();
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    private void setupData(){
        List<Version> versionsList = Version.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
        versions = new Version[versionsList.size()];
        versionsList.toArray(versions);
    }

    private void addFragment(){

        selectionFragment = ShareSelectionFragment.newInstance(versions);

        getSupportFragmentManager().beginTransaction().add(R.id.share_fragment_frame, selectionFragment).commit();
    }

    public void shareClicked(View view) {

        Version version = selectionFragment.getSelectedVersion();
        if(version != null) {
            goToNextActivity(SharingHelper.getIntentForSharing(getApplicationContext(), version));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void rowSelectedOrDeselected() {
//        int numOfKeyboards = selectionFragment.getSelectedVersion().size();
    }
}
