/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.Language;
import model.daoModels.Version;
import model.parsers.MediaType;
import services.UWUpdaterService;
import tasks.ModelCreator;
import tasks.ModelSaveOrUpdater;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating Versions
 */
public class UpdateVersionsRunnable implements Runnable{

    private static final String TAG = "UpdateVersionsRunnable";
    public static final String BOOKS_JSON_KEY = "toc";

    private JSONArray jsonModels;
    private UWUpdaterService updater;
    private Language parent;

    public UpdateVersionsRunnable(JSONArray jsonModels, UWUpdaterService updater, Language parent) {
        this.jsonModels = jsonModels;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }
    private void parseModels(JSONArray models){

        for(int i = 0; i < models.length(); i++){

            try {
                updateModel(models.getJSONObject(i), i == (models.length() - 1));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(final JSONObject jsonObject, final boolean isLast){

        new ModelCreator(new Version(), parent, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Version) {

                    UWDatabaseModel shouldContinueUpdate = new VersionSaveOrUpdater(updater.getApplicationContext()).start(model);

                    Log.d(TAG, "version created");
                    if(shouldContinueUpdate != null){
                        updateBooks(jsonObject, (Version) shouldContinueUpdate);
                    }
                    if(isLast){
                        updater.runnableFinished();
                    }
                }
            }
        }).execute(jsonObject);
    }

    private void updateBooks(JSONObject project, Version parent){

        try{
            JSONArray languages = project.getJSONArray(BOOKS_JSON_KEY);
            UpdateBooksRunnable runnable = new UpdateBooksRunnable(languages, updater, parent);
            updater.addRunnable(runnable, parent, MediaType.MEDIA_TYPE_TEXT);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class VersionSaveOrUpdater extends ModelSaveOrUpdater {

        public VersionSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Version.getModelForUniqueSlug(slug, session);
        }
    }
}
