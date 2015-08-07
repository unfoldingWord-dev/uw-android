package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import model.daoModels.Book;
import services.UWUpdaterService;
import utils.FileNameHelper;
import utils.URLDownloadUtil;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateBookContentRunnable implements Runnable{

    private static final String TAG = "UpdateBookConttRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    private UWUpdaterService updater;
    private Book book;

    public UpdateBookContentRunnable(Book book, UWUpdaterService updater) {
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

        saveFile(bookText, book.getSourceUrl());
        try {
            saveFile(sigText.getBytes("UTF-8"), book.getSignatureUrl());
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        UpdateAndVerifyBookRunnable runnable = new UpdateAndVerifyBookRunnable(parent, updater, bookText, sigText);
        updater.addRunnable(runnable, 1);
        updater.runnableFinished();
    }


    private void saveFile(byte[] bytes, String url){

        try{
            FileOutputStream fos = updater.getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.close();
            Log.i(TAG, "File Saved");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving USFM");
        }
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
