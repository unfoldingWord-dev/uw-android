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
import model.daoModels.Project;
import services.UWUpdaterService;
import tasks.ModelCreator;
import tasks.ModelSaveOrUpdater;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating Projects
 */
public class UpdateProjectsRunnable implements Runnable{

    private static final String TAG = "UpdateProjectsRunnable";
    public static final String LANGUAGES_JSON_KEY = "langs";
    private JSONArray jsonModels;
    private UWUpdaterService updater;

    public UpdateProjectsRunnable(JSONArray jsonModels, UWUpdaterService updater) {
        this.jsonModels = jsonModels;
        this.updater = updater;

    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }

    private void parseModels(JSONArray models){

        for(int i = 0; i < models.length(); i++){

            try {
                updateModel(models.getJSONObject(i), (i == (models.length() - 1)));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void updateModel(final JSONObject jsonObject, final boolean lastModel){

        new ModelCreator(new Project(), null, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Project) {

                    UWDatabaseModel shouldContinueUpdate = new ProjectSaveOrUpdater(updater.getApplicationContext()).start(model);

                    Log.d(TAG, "project created");

                    if(shouldContinueUpdate != null){
                        updateLanguages(jsonObject, (Project) shouldContinueUpdate);
                    }
                    if(lastModel){
                        updater.runnableFinished();
                    }
                }
            }
        }).execute(jsonObject);
    }

    private void updateLanguages(JSONObject project, Project parentProject){

        try{
            JSONArray languages = project.getJSONArray(LANGUAGES_JSON_KEY);
            UpdateLanguagesRunnable runnable = new UpdateLanguagesRunnable(languages, updater, parentProject);
            updater.addRunnable(runnable);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    private class ProjectSaveOrUpdater extends ModelSaveOrUpdater {

        public ProjectSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Project.getModelForUniqueSlug(slug, session);
        }
    }

}
