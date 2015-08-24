package peejweej.sideloading.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import ar.com.daidalos.afiledialog.FileChooserDialog;
import peejweej.sideloading.R;
import peejweej.sideloading.fragments.SideLoadTypeChoosingFragment;
import peejweej.sideloading.model.SideLoadInformation;
import peejweej.sideloading.model.SideLoadType;
import peejweej.sideloading.utilities.FileUtilities;
import peejweej.sideloading.wifiDirect.WiFiDirectActivity;

public class SideLoadActivity extends BaseActivity implements SideLoadTypeChoosingFragment.SideLoadTypeFragmentListener{

    public static final String FILE_TEXT_PARAM = "FILE_TEXT_PARAM";

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
            case SIDE_LOAD_TYPE_WIFI:
                startWIFILoadAction();
                break;
            case SIDE_LOAD_TYPE_STORAGE:
                startStorageLoadAction();
                break;
            case SIDE_LOAD_TYPE_SD_CARD:
                startSDCardLoadAction();
                break;
            case SIDE_LOAD_TYPE_AUTO_FIND:
                startAutoFindAction();
                break;
            default:
                break;
        }
    }

    private void startWIFILoadAction(){

        Intent intent = new Intent(getApplicationContext(), WiFiDirectActivity.class);
        startActivity(intent);
    }

    private void startAutoFindAction(){
        startActivityForResult(new Intent(getApplicationContext(), FileFinderActivity.class)
                .putExtra(FileFinderActivity.LOAD_INFO_PARAM, sideLoadInformation), 0);
        onBackPressed();
    }

    private void startStorageLoadAction(){
        loadStorage(null);
    }

    private void startSDCardLoadAction(){
        loadStorage("/" + getString(R.string.app_name));
    }

    private void loadStorage(String optionalDir){

        String finalDir = Environment.getExternalStorageDirectory().getPath();
        if(optionalDir != null && new File(finalDir + optionalDir).exists()){
            finalDir += optionalDir;
        }
        FileChooserDialog dialog = new FileChooserDialog(this, finalDir);
        dialog.setFilter(".*" + sideLoadInformation.fileExtension);
        dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {

            public void onFileSelected(Dialog source, File file) {
                finishWithFile(Uri.fromFile(file));
                source.dismiss();
            }

            public void onFileSelected(Dialog source, File folder, String name) {
                finishWithFile(Uri.fromFile(new File(folder.getAbsolutePath() + name)));
                source.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == resultCode && data != null && data.getData() != null){
            finishWithFile(data.getData());
        }
    }

    private void finishWithFile(Uri fileUri){

        File file = new File(fileUri.getPath());

        if(sideLoadInformation.fileVerifier != null && sideLoadInformation.fileVerifier.fileIsValid(FileUtilities.getBytesFromFile(file))){
            showFailureAlert();
        }
        else {
            setResult(0, new Intent(getApplicationContext(), SideLoadActivity.class).setData(Uri.fromFile(file)));
            handleBack();
        }
    }

    private void finishWithText(String text){

        if(sideLoadInformation.fileVerifier != null && sideLoadInformation.fileVerifier.fileIsValid(text)){
            showFailureAlert();
        }
        else {
            setResult(0, new Intent(getApplicationContext(), SideLoadActivity.class).putExtra(FILE_TEXT_PARAM, text));
            handleBack();
        }
    }

    private void showFailureAlert(){

        setLoadingFragmentVisibility(false, "", false);
        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Load Failure");
        new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setMessage("Error loading file. Please try again")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed(true);
                    }
                })
                .show();
    }

}
