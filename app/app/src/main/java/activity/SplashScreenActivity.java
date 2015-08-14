package activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.readingSelection.InitialScreenActivity;
import model.DaoDBHelper;
import model.daoModels.Project;
import services.UWPreLoaderService;

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
        filter.addAction(UWPreLoaderService.BROAD_CAST_PRELOAD_SUCCESSFUL);
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
//            goToInitialActivity();
            preLoadData();
        }
    }

    private void preLoadData(){
        startService(new Intent(getApplicationContext(), UWPreLoaderService.class));
    }

    private void goToInitialActivity(){
        startActivity(new Intent(SplashScreenActivity.this, InitialScreenActivity.class));
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
