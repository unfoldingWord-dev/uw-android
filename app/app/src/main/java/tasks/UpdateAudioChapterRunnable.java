/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import services.UWUpdaterService;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating a list of books
 */
public class UpdateAudioChapterRunnable implements Runnable{

    private static final String TAG = "UpdateBooksRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    private JSONArray jsonModels;
    private UWUpdaterService updater;
    private AudioBook parent;

    public UpdateAudioChapterRunnable(JSONArray jsonModels, UWUpdaterService updater, AudioBook parent) {
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

        new ModelCreator(new AudioChapter(), parent, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof AudioChapter) {

                    UWDatabaseModel shouldContinueUpdate = new AudioChapterSaveOrUpdater(updater.getApplicationContext()).start(model);

//                    if(shouldContinueUpdate != null){
//                        updateAudioChapters((AudioBook) shouldContinueUpdate);
//                    }
                    if(isLast){
                        updater.runnableFinished();
                    }
                }
            }
        }).execute(jsonObject);
    }

    private class AudioChapterSaveOrUpdater extends ModelSaveOrUpdater{

        public AudioChapterSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return AudioChapter.getModelForUniqueSlug(slug, session);
        }
    }

}
