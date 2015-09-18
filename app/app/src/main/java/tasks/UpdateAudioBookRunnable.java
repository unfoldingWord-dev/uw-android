package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.AudioBook;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.Version;
import services.UWUpdaterService;

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
//                            Log.d(TAG, "Book created");
                    if(shouldContinueUpdate != null){
                        updateAudioChapters(jsonObject, (AudioBook) shouldContinueUpdate);
                    }
                    updater.runnableFinished();
                }
            }
        }).execute(jsonObject);
    }

    private void updateAudioChapters(JSONObject audioBook, AudioBook parent){

        try {
            updater.addRunnable(new UpdateAudioChapterRunnable(audioBook.getJSONArray(SOURCES_JSON_KEY), updater, parent), 3);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class AudioBookSaveOrUpdater extends ModelSaveOrUpdater{

        public AudioBookSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return AudioBook.getModelForUniqueSlug(slug, session);
        }
    }

}
