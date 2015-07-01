package tasks;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.DaoSession;
import model.daoModels.Version;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateBookContentRunnable implements Runnable{

    private static final String TAG = "UpdateBookContentRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    private UWUpdater updater;
    private Book book;

    public UpdateBookContentRunnable(Book book, UWUpdater updater) {
        this.book = book;
        this.updater = updater;
    }

    @Override
    public void run() {

        updateChapters(book);
    }

    private void updateChapters(Book parent){

        boolean isUsfm = parent.getSourceUrl().contains("usfm");

        if (isUsfm){
            updateUsfm(parent);
        }
        else{
            updateStories(parent);
        }
    }

    private void updateUsfm(final Book parent){

        new UpdateVerificationTask(updater.getApplicationContext(), new UpdateVerificationTask.VerificationTaskListener() {
                @Override
                public void verificationFinishedWithResult(byte[] text) {
                if (text != null){
                    UpdateBibleChaptersRunnable runnable = new UpdateBibleChaptersRunnable(text, updater, parent);
                    updater.addRunnable(runnable);
                }
            }
        }).execute(parent);
    }

    private void updateStories(final Book parent){


        new UpdateVerificationTask(updater.getApplicationContext(), new UpdateVerificationTask.VerificationTaskListener() {
            @Override
            public void verificationFinishedWithResult(byte[] text) {

                if (text != null) {

                    try {
                        UpdateStoriesChaptersRunnable runnable = new UpdateStoriesChaptersRunnable(
                                new JSONObject(new String(text)).getJSONArray(CHAPTERS_JSON_KEY), updater, parent);
                        updater.addRunnable(runnable);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(parent);
    }
}
