package activity;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.distantshores.unfoldingword.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import db.DBManager;
import models.ChaptersModel;
import models.LanguageModel;
import parser.JsonParser;
import utils.URLDownloadUtil;
import utils.URLUtils;

/**
 * Created by Acts Media Inc. on 2/12/14.
 */
public class SplashScreenActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 2500;
    DBManager dbManager = null;

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
        new GetLanguageListAndFrames().execute(URLUtils.LANGUAGE_INFO, URLUtils.CHAPTER_INFO);
    }


    private class GetLanguageListAndFrames extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (dbManager.getDataCount() > 0) {
                try {
                    Thread.sleep(1000 * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                JsonParser parser = JsonParser.getInstance();
                try {
                    String languageJson = URLDownloadUtil.downloadJson(params[0]);

                    if (!languageJson.equals(URLUtils.ERROR)) {
                        ArrayList<LanguageModel> languageModels = parser.getLanguagesInfo(languageJson);
                        for (LanguageModel model : languageModels) {
                            dbManager.addLanguage(model);
                        }
                        for (int i = 0; i < languageModels.size(); i++) {
                            String chapterJson = URLDownloadUtil.downloadJson(params[1] +
                                    languageModels.get(i).language + "/obs-" + languageModels.get(i).language + ".json");
                            ArrayList<ChaptersModel> chaptersModels = parser.getChapterFromLanguage(languageModels.get(i).language, chapterJson);
                            for (ChaptersModel model : chaptersModels) {
                                boolean value = dbManager.addChapters(model);
                            }

                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return URLUtils.ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return URLUtils.ERROR;
                }


            }
            return URLUtils.TRUE;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals(URLUtils.TRUE)) {
                startActivity(new Intent(SplashScreenActivity.this, LanguageChooserActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
