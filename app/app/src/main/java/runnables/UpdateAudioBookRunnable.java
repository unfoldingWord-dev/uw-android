/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.AudioBook;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.parsers.MediaType;
import services.UWUpdaterService;
import tasks.ModelCreator;
import tasks.ModelSaveOrUpdater;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating a list of books
 */
public class UpdateAudioBookRunnable implements Runnable{

    private static final String TAG = "UpdateBooksRunnable";
    public static final String SOURCES_JSON_KEY = "src_list";
    private JSONObject jsonModel;
    private UWUpdaterService updater;
    private Book parent;

    public UpdateAudioBookRunnable(JSONObject jsonModel, UWUpdaterService updater, Book parent) {
        this.jsonModel = jsonModel;
        this.updater = updater;
        this.parent = parent;
    }

    @Override
    public void run() {

        updateModel(jsonModel);
    }

    private void updateModel(final JSONObject jsonObject){

        if(!jsonObject.keys().hasNext()){
            updater.runnableFinished();
            return;
        }

        new ModelCreator(new AudioBook(), parent, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof AudioBook) {

                    UWDatabaseModel shouldContinueUpdate = new AudioBookSaveOrUpdater(updater.getApplicationContext()).start(model);

                    if(shouldContinueUpdate != null){
                        AudioBook audioBook = (AudioBook) shouldContinueUpdate;
                        parent.setAudioBookId(audioBook.getId());
                        parent.update();
                        parent.getAudioBook();
                        updateAudioChapters(jsonObject, audioBook);
                    }
                    updater.runnableFinished(parent.getVersion(), MediaType.MEDIA_TYPE_AUDIO);
                }
            }
        }).execute(jsonObject);
    }

    private void updateAudioChapters(JSONObject audioBook, AudioBook parent){

        try {
            updater.addRunnable(new UpdateAudioChapterRunnable(audioBook.getJSONArray(SOURCES_JSON_KEY), updater, parent), parent.getBook().getVersion(), MediaType.MEDIA_TYPE_AUDIO);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class AudioBookSaveOrUpdater extends ModelSaveOrUpdater {

        public AudioBookSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return AudioBook.getModelForUniqueSlug(slug, session);
        }
    }

}
