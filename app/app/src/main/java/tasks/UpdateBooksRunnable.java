package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.DownloadState;
import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.Version;
import services.UWUpdaterService;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating a list of books
 */
public class UpdateBooksRunnable implements Runnable{

    private static final String TAG = "UpdateBooksRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    public static final String MEDIA_JSON_KEY = "media";
    public static final String AUDIO_JSON_KEY = "audio";
    private JSONArray jsonModels;
    private UWUpdaterService updater;
    private Version parent;

    public UpdateBooksRunnable(JSONArray jsonModels, UWUpdaterService updater, Version parent) {
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

        new ModelCreator(new Book(), parent, new ModelCreator.ModelCreationListener() {
            @Override
            public void modelWasCreated(UWDatabaseModel model) {

                if(model instanceof Book) {

                    UWDatabaseModel shouldContinueUpdate = new BookSaveOrUpdater(updater.getApplicationContext()).start(model);
//                            Log.d(TAG, "Book created");
                    if(shouldContinueUpdate != null){
                        updateChapters((Book) shouldContinueUpdate);
                        updateMedia(jsonObject, (Book) shouldContinueUpdate);
                    }
                    if(isLast){
                        updater.runnableFinished();
                    }
                }
            }
        }).execute(jsonObject);
    }

    private void updateChapters(Book parent){

        if(parent.getVersion().getSaveState() == DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal()) {
            updater.addRunnable(new UpdateBookContentRunnable(parent, updater), 3);
        }
    }

    private void updateMedia(JSONObject bookJson, Book book){

        try {
            updater.addRunnable(new UpdateAudioBookRunnable(bookJson.getJSONObject(MEDIA_JSON_KEY).getJSONObject(AUDIO_JSON_KEY), updater, book));
        }
        catch (JSONException e ){
            e.printStackTrace();
        }
    }

    private class BookSaveOrUpdater extends ModelSaveOrUpdater{

        public BookSaveOrUpdater(Context context) {
            super(context);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return Book.getModelForUniqueSlug(slug, session);
        }
    }

}
