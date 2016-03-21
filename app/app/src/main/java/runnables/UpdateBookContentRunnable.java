/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

import org.json.JSONException;
import org.json.JSONObject;

import model.DataFileManager;
import model.daoModels.Book;
import model.parsers.MediaType;
import services.UWUpdaterService;
import tasks.VerificationUpdater;
import utils.URLDownloadUtil;

/**
 * Created by PJ Fechner on 6/17/15.
 * Runnable for updating the content of a book
 */
public class UpdateBookContentRunnable implements Runnable{

    private static final String TAG = "UpdateBookCtntRunnable";
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

        //try again if it failed
        if(bookText == null || bookText.length < 1){
            bookText = URLDownloadUtil.downloadBytes(book.getSourceUrl());
        }

        if(bookText == null || bookText.length < 1){
            updater.runnableFinished(parent.getVersion(), MediaType.MEDIA_TYPE_TEXT);
            return;
        }
        else if(sigText == null){
            sigText = "";
        }

        if(bookText != null && bookText.length > 0 && sigText != null && sigText.length() > 0) {
            DataFileManager.saveDataForBook(updater.getApplicationContext(), book, bookText, MediaType.MEDIA_TYPE_TEXT);
            DataFileManager.saveSignatureForBook(updater.getApplicationContext(), book, sigText.getBytes(), MediaType.MEDIA_TYPE_TEXT);
//            saveFile(bookText, book.getSourceUrl());
//            try {
//                saveFile(sigText.getBytes("UTF-8"), book.getSignatureUrl());
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }

            UpdateAndVerifyBookRunnable runnable = new UpdateAndVerifyBookRunnable(parent, updater, bookText, sigText);
            updater.addRunnable(runnable, parent.getVersion(), MediaType.MEDIA_TYPE_TEXT);
        }
        updater.runnableFinished(parent.getVersion(), MediaType.MEDIA_TYPE_TEXT);
    }

    private void updateStories(final Book parent){

        new VerificationUpdater(updater.getApplicationContext(), new VerificationUpdater.VerificationTaskListener() {
            @Override
            public void verificationFinishedWithResult(byte[] text, String sigText) {

                if (text != null) {
                    DataFileManager.saveDataForBook(updater.getApplicationContext(), book, text, MediaType.MEDIA_TYPE_TEXT);
                    DataFileManager.saveSignatureForBook(updater.getApplicationContext(), book, sigText.getBytes(), MediaType.MEDIA_TYPE_TEXT);

                    try {
                        UpdateStoriesChaptersRunnable runnable = new UpdateStoriesChaptersRunnable(
                                new JSONObject(new String(text)).getJSONArray(CHAPTERS_JSON_KEY), updater, parent);
                        updater.addRunnable(runnable, parent.getVersion(), MediaType.MEDIA_TYPE_TEXT);
                        updater.runnableFinished(parent.getVersion(), MediaType.MEDIA_TYPE_TEXT);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute(parent);
    }
}
