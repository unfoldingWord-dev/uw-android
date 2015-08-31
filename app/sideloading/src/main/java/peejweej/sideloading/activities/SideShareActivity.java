package peejweej.sideloading.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;

import ar.com.daidalos.afiledialog.FileChooserDialog;
import peejweej.sideloading.R;
import peejweej.sideloading.fragments.SideLoadTypeChoosingFragment;
import peejweej.sideloading.model.SideLoadInformation;
import peejweej.sideloading.model.SideLoadType;
import peejweej.sideloading.utilities.FileUtilities;
import peejweej.sideloading.wifiDirect.WiFiDirectActivity;

public class SideShareActivity extends BaseActivity implements SideLoadTypeChoosingFragment.SideLoadTypeFragmentListener{

    public static final String SIDE_LOAD_INFORMATION_PARAM = "SIDE_LOAD_INFORMATION_PARAM";
    private SideLoadInformation sideLoadInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_fragment);

        sideLoadInformation = (SideLoadInformation) getIntent().getSerializableExtra(SIDE_LOAD_INFORMATION_PARAM);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, SideLoadTypeChoosingFragment
                        .constructFragment(sideLoadInformation))
                .commit();
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_FORWARD_RIGHT_BACK_DOWN;
    }

    public void typeWasChosen(SideLoadType type) {

        switch (type){
            case SIDE_LOAD_TYPE_BLUETOOTH:
                startBluetoothShareAction();
                break;
            case SIDE_LOAD_TYPE_NFC:
                break;
            case SIDE_LOAD_TYPE_WIFI:
                startWIFIShareAction();
                break;
            case SIDE_LOAD_TYPE_STORAGE:
                startStorageShareAction();
                break;
            case SIDE_LOAD_TYPE_QR_CODE:
                break;
            case SIDE_LOAD_TYPE_SD_CARD:
                startSDCardShareAction();
                break;
            case SIDE_LOAD_TYPE_AUTO_FIND:
                break;
            case SIDE_LOAD_TYPE_FILE:
                break;
            case SIDE_LOAD_TYPE_OTHER:
                startShareOtherAction();
                break;
            default:
                break;
        }
    }

    private Uri getFileUri(){

        if(sideLoadInformation.getUri() != null){
            return sideLoadInformation.getUri();
        }
        else{
            try {
                return FileUtilities.createTemporaryFile(getApplicationContext(), sideLoadInformation.file.getBytes("UTF-8"), sideLoadInformation.fileName);
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                return null;
            }
        }
    }

    private void startShareOtherAction(){

        Uri fileUri = getFileUri();

        Intent sharingIntent = new Intent(
                Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(sharingIntent);
    }

    private void startBluetoothShareAction(){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Start Bluetooth Sharing");

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage("Before starting, make sure the device you're sharing with has bluetooth available by going to it's Settings app, selecting bluetooth and confirming it is on and available to pair.")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openBluetoothSharing();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialogue.show();
    }



    private void openBluetoothSharing(){

        Uri fileUri = getFileUri();

        Intent sharingIntent = new Intent(
                Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .setComponent(new ComponentName(
                        "com.android.bluetooth",
                        "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivityForResult(sharingIntent, 0);
    }

    private void startNFCShareAction(){

        Uri fileUri = getFileUri();

        Intent sharingIntent = new Intent(
                Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent
//                .setComponent(new ComponentName(
//                        "com.android.nfchip" +
//                                "",
//                        "com.android.nfc.opp.BeamShareActivity"));
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(sharingIntent);

    }

    private void startWIFIShareAction(){

        Uri fileUri = getFileUri();

        Intent intent = new Intent(getApplicationContext(), WiFiDirectActivity.class)
                .setData(fileUri);
        startActivity(intent);
    }

    private void startStorageShareAction(){

        final FileChooserDialog dialog = new FileChooserDialog(this, Environment.getExternalStorageDirectory().getAbsolutePath());
        dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
            public void onFileSelected(Dialog source, File file) {
                saveToFile(file);
                dialog.dismiss();
            }

            public void onFileSelected(Dialog source, File folder, String name) {
                saveToFile(new File(folder.getAbsolutePath() + "/" + name));
                dialog.dismiss();
            }
        });
        dialog.setFolderMode(true);
        dialog.setShowConfirmation(true, false);
        dialog.show();
    }

    private void saveToFile(File folder){

        FileUtilities.saveFile(getFileBytes(), folder.getAbsolutePath(), sideLoadInformation.fileName);
    }

    private byte[] getFileBytes(){

        if(sideLoadInformation.file != null){

            try {
                return sideLoadInformation.file.getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                return null;
            }
        }
        else{
            return FileUtilities.getBytesFromFile(new File(sideLoadInformation.getUri().getPath()));
        }
    }

    private void startSDCardShareAction(){

        setLoadingFragmentVisibility(true, "Saving", false);
        FileUtilities.saveFileToSdCard(getApplicationContext(), getFileBytes(), sideLoadInformation.fileName);
        showSuccessAlert(true);
    }

    private void showSuccessAlert(boolean success){

        setLoadingFragmentVisibility(false, "", false);
        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Share Status");
        new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage((success)? "Sharing was successful" : "Sharing failed")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed(true);
                    }
                })
                .show();
    }
}
