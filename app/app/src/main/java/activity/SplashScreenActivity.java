package activity;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.unfoldingword.mobile.R;

import java.io.IOException;

import activity.bookSelection.InitialPageActivity;
import model.database.DBManager;
import utils.URLUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class SplashScreenActivity extends Activity {

    public static final String TRUE = "true";
    private static String TAG = "SplashScreenActivity";

    DBManager dbManager = null;
    AsyncTask<String, Void, String> execute = null;
    boolean cancelValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setUI();
    }

    /**
     * Default Initialization of components
     */
    private void setUI() {
        dbManager = DBManager.getInstance(this);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        execute = new DatabaseUpdater().execute();
    }

    @Override
    protected void onPause() {
        if (execute != null) {
            cancelValue = false;
            execute.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (execute != null) {
            cancelValue = false;
            execute.cancel(true);
        }
        super.onBackPressed();
    }

    private class DatabaseUpdater extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                dbManager.createDataBase(false);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cancelValue = true;
            return TRUE;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            if (cancelValue) {
                if (result.equals(TRUE)) {
                    startActivity(new Intent(SplashScreenActivity.this, InitialPageActivity.class));
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                finish();
                startActivity(new Intent(SplashScreenActivity.this, SplashScreenActivity.class));
            }

        }
    }
}
