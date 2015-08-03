package tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import model.daoModels.Book;
import services.UWUpdater;
import signing.UWSigning;
import utils.URLDownloadUtil;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateVerificationRunnable implements Runnable{

    private static final String TAG = "UpdateVerificationRunnable";
    public static final String CHAPTERS_JSON_KEY = "chapters";
    private UWUpdater updater;
    private Book book;
    byte[] bookText;
    String sigText;

    public UpdateVerificationRunnable(Book book, UWUpdater updater, byte[] bookText, String sigText) {
        this.book = book;
        this.updater = updater;
        this.bookText = bookText;
        this.sigText = sigText;
    }


    @Override
    public void run() {
        updateVerification();
    }

    private void updateVerification(){

        try {
            UWSigning.updateVerification(updater.getApplicationContext(), book, bookText, sigText);
            parseText(bookText);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void parseText(byte[] text){
        if (text != null) {

            if(book.getSourceUrl().contains("usfm")){
                UpdateBibleChaptersRunnable runnable = new UpdateBibleChaptersRunnable(
                        text, updater, book);
                updater.addRunnable(runnable, 0);
                updater.runnableFinished();
            }
            else{
                try {
                    UpdateStoriesChaptersRunnable runnable = new UpdateStoriesChaptersRunnable(
                            new JSONObject(new String(text)).getJSONArray(UpdateBookContentRunnable.CHAPTERS_JSON_KEY), updater, book);
                    updater.addRunnable(runnable, 0);
                    updater.runnableFinished();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }


        }
    }

}
