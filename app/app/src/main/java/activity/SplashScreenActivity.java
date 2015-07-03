package activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.io.IOException;
import java.util.List;

import activity.bookSelection.InitialPageActivity;
import model.DaoDBHelper;
import model.daoModels.Project;
import model.database.DBManager;
import services.UWPreLoader;
import utils.URLUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class SplashScreenActivity extends Activity {

    private static String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setUI();
        registerPreloadReceiver();
        initializeDB();
    }

    private void registerPreloadReceiver(){

        IntentFilter filter = new IntentFilter();
        filter.addAction(UWPreLoader.BROAD_CAST_PRELOAD_SUCCESSFUL);
        registerReceiver(receiver, filter);
    }

    private void unRegisterPreloadReceiver(){

        unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        unRegisterPreloadReceiver();
        super.onPause();
    }

    /**
     * Default Initialization of components
     */
    private void setUI() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
    }

    private void initializeDB(){

        List<Project> existingProjects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));
        boolean dataIsLoaded = (existingProjects != null && existingProjects.size() > 0);

        if(dataIsLoaded){
            goToInitialActivity();
        }
        else{
            preLoadData();
        }
    }

    private void preLoadData(){
        startService(new Intent(getApplicationContext(), UWPreLoader.class));
    }

    private void goToInitialActivity(){
        startActivity(new Intent(SplashScreenActivity.this, InitialPageActivity.class));
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        finish();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            goToInitialActivity();
        }
    };



//    private class DatabaseUpdater extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                dbManager.createDataBase(false);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            cancelValue = true;
//            return TRUE;
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (cancelValue) {
//                if (result.equals(TRUE)) {
//                    startActivity(new Intent(SplashScreenActivity.this, InitialPageActivity.class));
//                    finish();
//                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                finish();
//                startActivity(new Intent(SplashScreenActivity.this, SplashScreenActivity.class));
//            }
//
//        }
//    }
}
