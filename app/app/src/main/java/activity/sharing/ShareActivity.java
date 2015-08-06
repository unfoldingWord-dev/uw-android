package activity.sharing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import adapters.VersionAdapter;
import model.DaoDBHelper;
import model.daoModels.Version;
import sideloading.SideLoadType;
import sideloading.SideSharer;

public class ShareActivity extends UWBaseActivity implements VersionAdapter.VersionAdapterListener {

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

        SideSharer sharer = new SideSharer(this, new SideSharer.SideLoaderListener() {
            @Override
            public void sideLoadingSucceeded(String response) {

            }

            @Override
            public void sideLoadingFailed(String errorMessage) {

            }

            @Override
            public boolean confirmSideLoadingType(SideLoadType type) {

//                if(type == SideLoadType.SIDE_LOAD_TYPE_QR_CODE && selectionFragment.getSelectedVersions().size() > 1){
//                    showAmountAlert();
//                    return false;
//                }
//                else{
                    return true;
//                }
            }
        });

        if(selectionFragment.getSelectedVersions() != null && selectionFragment.getSelectedVersions().size() > 0) {
            sharer.startSharing(getData(), getFileName());
        }

    }

    private String getData() {

        Version version = selectionFragment.getSelectedVersions().get(0);

        JSONObject requestedKeyboardData = version.getAsPreloadJson(getApplicationContext());

        if (requestedKeyboardData != null){
            String data = requestedKeyboardData.toString();
            return data;
        }
        else
        {
            return null;
        }
    }

    private String getFileName(){

        return selectionFragment.getSelectedVersions().get(0).getName() + ".ufw";
    }

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
//        int numOfKeyboards = selectionFragment.getSelectedVersions().size();
    }
}
