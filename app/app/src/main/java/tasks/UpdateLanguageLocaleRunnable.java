/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.daoModels.LanguageLocale;
import services.UWUpdaterService;
import unfoldingword.DaoHelperMethods;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating a LanguageLocale
 */
public class UpdateLanguageLocaleRunnable implements Runnable{

    private static final String TAG = "UpdateLangLocaleRunble";

    private JSONArray jsonModels;
    private UWUpdaterService updater;

    public UpdateLanguageLocaleRunnable(JSONArray jsonModels, UWUpdaterService updater) {
        this.jsonModels = jsonModels;
        this.updater = updater;

        DaoDBHelper.getDaoSession(updater.getApplicationContext()).getLanguageLocaleDao().deleteAll();
    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }
    private void parseModels(JSONArray models){

        List<LanguageLocale> locales = new ArrayList<LanguageLocale>();
        Log.i(TAG, "Started Locales");
        for(int i = 0; i < models.length(); i++){

            try {
                locales.add((LanguageLocale) new LanguageLocale().setupModelFromJson(models.getJSONObject(i)));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        addAllLocales(locales);
    }

    private void addAllLocales(List<LanguageLocale> locales){

        Log.d(TAG, "Will add all locales to DB");
        DaoDBHelper.getDaoSession(updater.getApplicationContext()).getLanguageLocaleDao().insertOrReplaceInTx(locales);
        Log.d(TAG, "Added all locales to DB");

        updater.runnableFinished();
    }
}
