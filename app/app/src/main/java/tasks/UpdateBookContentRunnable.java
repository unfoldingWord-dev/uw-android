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
import utils.URLDownloadUtil;

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
        byte[] bookText = URLDownloadUtil.downloadBytes(book.getSourceUrl());
        String sigText = URLDownloadUtil.downloadString(book.getSignatureUrl());

        UpdateVerificationRunnable runnable = new UpdateVerificationRunnable(parent, updater, bookText, sigText);
        updater.addRunnable(runnable, 1);
        updater.runnableFinished();
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
                        updater.runnableFinished();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(parent);
    }
}
