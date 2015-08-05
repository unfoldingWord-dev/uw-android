package sideloading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import activity.sharing.BluetoothReceivingActivity;
import activity.sharing.FileFinderActivity;
import adapters.ShareAdapter;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import utils.FileLoader;
import utils.Zipper;
import wifiDirect.WiFiDirectActivity;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideLoader {

    private static final String TAG = "SideLoader";

    private Activity activity;
    private ListView optionsListView;

    public SideLoader(Activity activity, ListView optionsListView) {
        this.activity = activity;
        this.optionsListView = optionsListView;

    }

    public void startLoading(){
        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        optionsListView.setAdapter(new ShareAdapter(activity.getApplicationContext(),
                SideLoaderTypeHandler.getListOfSideLoadTypes(activity, true)));

        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startSideLoading(SideLoaderTypeHandler.getTypeForIndex(activity, position, true));
            }
        });
    }

    private void startSideLoading(SideLoadType type){

        switch (type) {
            case SIDE_LOAD_TYPE_BLUETOOTH: {
                startBluetoothLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_NFC: {
                startNFCLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_WIFI: {
                startWIFILoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_STORAGE: {
                startStorageLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_SD_CARD:{
                startSDCardLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_AUTO_FIND:{
                startAutoFindAction();
                break;
            }
        }
    }

    private void startBluetoothLoadAction(){

        Intent intent = new Intent(activity.getApplicationContext(), BluetoothReceivingActivity.class);

        activity.startActivityForResult(intent, 0);
    }

    private void startNFCLoadAction(){

    }

    private void startWIFILoadAction(){

        Intent intent = new Intent(activity.getApplicationContext(), WiFiDirectActivity.class);
        activity.startActivity(intent);
    }


    private void startAutoFindAction(){
        activity.startActivity(new Intent(activity.getApplicationContext(), FileFinderActivity.class));
        activity.finish();
    }

    private void startStorageLoadAction(){
        loadStorage(null);
    }

    private void startSDCardLoadAction(){
        loadStorage("/" + activity.getString(R.string.app_name));
    }

    private void loadStorage(String optionalDir){

        String finalDir = Environment.getExternalStorageDirectory().getPath();
        if(optionalDir != null && new File(finalDir + optionalDir).exists()){
            finalDir += optionalDir;
        }
        FileChooserDialog dialog = new FileChooserDialog(activity, finalDir);
        dialog.setFilter(".*tk");
        dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
            @Override
            public void onFileSelected(Dialog source, File file) {
                loadFile(file);
                source.dismiss();
            }

            @Override
            public void onFileSelected(Dialog source, File folder, String name) {
                loadFile(new File(folder.getAbsolutePath() + name));
                source.dismiss();
            }
        });
        dialog.show();
    }

    public void loadFile(File file){

        String fileText = FileLoader.getStringFromFile(file);
        textWasFound(unzipText(fileText));
    }

    private void showSuccessAlert(boolean success){

        new AlertDialog.Builder(activity)
                .setTitle("Load Status")
                .setMessage((success)? "Loading was successful" : "Loading failed")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                })
                .show();
    }

    public String unzipText(String text){
        return Zipper.decodeFromBase64EncodedString(text);
    }

    public void textWasFound(final String json){

        List<String> names = getNamesOfKeyboards(json);
        String keyboardText = (names.size() == 1)? "Keyboard" : "Keyboards";

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Import " + names.size() + " " + keyboardText + "?");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setMessage(keyboardText + ":\n\n" + getNames(names))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveKeyboards(json);
                        showSuccessAlert(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private List<String> getNamesOfKeyboards(String json){

        List<String> names = new ArrayList<String>();
        try {
            JSONArray availableKeyboards = new JSONObject(json).getJSONArray("keyboards");

            for(int i = 0; i < availableKeyboards.length(); i++){
                JSONObject available = availableKeyboards.getJSONObject(i);
                JSONObject keyboards = available.getJSONObject("keyboards");

                String keyboardText = keyboards.getString("keyboard_name") + ": ";

                JSONArray variants = keyboards.getJSONArray("keyboard_variants");
                for(int j = 0; j < variants.length(); j++){

                    keyboardText += variants.getJSONObject(j).getString("name") + ", ";
                }

                names.add(keyboardText);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return names;
    }

    private String getNames(List<String> names){

        String finalString = "";

        for(String name : names){
            finalString += name + "\n";
        }

        if(finalString.length() > 0){
            finalString = finalString.substring(0, finalString.length() - 2);
            return finalString;
        }
        else{
            return "";
        }
    }

    private void saveKeyboards(String json){

//        KeyboardDataHandler.sideLoadKeyboards(activity.getApplicationContext(), json);
        Log.i(TAG, "keyboard Loaded");
    }

    public static boolean sdCardIsPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
