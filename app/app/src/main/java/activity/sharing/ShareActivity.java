package activity.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.unfoldingword.mobile.R;
import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import adapters.VersionShareAdapter;
import model.DaoDBHelper;
import model.SharingHelper;
import model.daoModels.Version;
import sideloading.SideSharer;

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
            goToNewActivity(SharingHelper.getIntentForSharing(getApplicationContext(), version));
        }
    }

//    private String getData(Version version) {
//
//        JSONObject requestedKeyboardData = version.getAsPreloadJson(getApplicationContext());
//
//        if (requestedKeyboardData != null){
//            return requestedKeyboardData.toString();
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//    private String getFileName(Version version){
//
//        return version.getName() + getString(R.string.save_file_extension);
//    }

//    private void showAmountAlert(){
//
//        new AlertDialog.Builder(this)
//                .setTitle("Keyboard Selection")
//                .setMessage("Please select only 1 keyboard for QR sharing")
//                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        new SideSharer(this, null).showBluetoothDirectionsDialog();
        Log.i(TAG, "activity result");
    }

    @Override
    public void rowSelectedOrDeselected() {
//        int numOfKeyboards = selectionFragment.getSelectedVersion().size();
    }
}
