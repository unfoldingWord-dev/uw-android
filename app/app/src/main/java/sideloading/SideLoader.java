package sideloading;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import activity.UWBaseActivity;
import activity.sharing.BluetoothReceivingActivity;
import activity.sharing.FileFinderActivity;
import adapters.ShareAdapter;
import ar.com.daidalos.afiledialog.FileChooserDialog;
import model.parsers.LanguageParser;
import model.parsers.ProjectParser;
import model.parsers.VersionParser;
import services.UWSideLoaderService;
import utils.FileUtil;
import utils.Zipper;
import wifiDirect.WiFiDirectActivity;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideLoader {

    private static final String TAG = "SideLoader";

    private UWBaseActivity activity;
    private ListView optionsListView;

    public SideLoader(UWBaseActivity activity, ListView optionsListView) {
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
        activity.onBackPressed();
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
        dialog.setFilter(".*ufw");
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

        byte[] fileText = FileUtil.getbytesFromFile(file);
        fileText = unzipText(fileText);
        textWasFound(fileText);
    }

    private void showSuccessAlert(boolean success){

        activity.setLoadingViewVisibility(false, "", false);
        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Load Status");
        new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setMessage((success) ? "Loading was successful" : "Loading failed")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.onBackPressed();
                    }
                })
                .show();
    }

    public byte[] unzipText(byte[] text){
        return Zipper.getDecompressedBytes(text);
    }

    public void textWasFound(final byte[] json) {
        try{
            textWasFound(json, getNamesOfVersions(new String(json, "UTF-8")));
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    public void textWasFound(final byte[] json, List<String> names){

        String versionText = (names.size() == 1)? "Version" : "Versions";

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Import " + names.size() + " " + versionText + "?");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setMessage(versionText + ":\n\n" + getNames(names))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity.setLoadingViewVisibility(true, "Importing...", false);
                        Uri tempUri = FileUtil.createTemporaryFile(activity.getApplicationContext(), json, "temp_version.ufwtmp");
                        saveVersion(tempUri);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private List<String> getNamesOfVersions(String json){

        List<String> names = new ArrayList<String>();
        try {
            JSONObject project = new JSONObject(json).getJSONObject("top");

            JSONArray languages = project.getJSONArray(ProjectParser.LANGUAGES_JSON_KEY);

            for(int i = 0; i < languages.length(); i++){

                JSONObject language = languages.getJSONObject(i);
                JSONArray versions = language.getJSONArray(LanguageParser.VERSION_JSON_KEY);

                for(int j = 0; j < versions.length(); j++) {
                    JSONObject version = versions.getJSONObject(j);
                    String name = version.getString(VersionParser.NAME_JSON_KEY);
                    names.add(name);
                }
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

        if(finalString.length() > 1){
            finalString = finalString.substring(0, finalString.length() - 1);
            return finalString;
        }
        else{
            return "";
        }
    }

    private void saveVersion(Uri versionUri){

        registerPreloadReceiver();
        Intent intent = new Intent(activity.getApplicationContext(), UWSideLoaderService.class)
                .setData(versionUri);
        activity.startService(intent);
        Log.i(TAG, "Version Loading Started");
    }

    public static boolean sdCardIsPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void registerPreloadReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(UWSideLoaderService.BROAD_CAST_SIDE_LOAD_SUCCESSFUL);
        activity.registerReceiver(receiver, filter);
    }

    private void unRegisterPreloadReceiver(){

        activity.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unRegisterPreloadReceiver();
            showSuccessAlert(true);
        }
    };
}
