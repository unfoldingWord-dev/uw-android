/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package runnables;

import android.util.Log;

import model.DataFileManager;
import model.daoModels.Book;
import model.parsers.MediaType;
import services.UWUpdaterService;
import tasks.BytesDownloadTask;

/**
 * Created by Fechner on 9/24/15.
 */
public class DownloadAudioRunnable implements Runnable {
    private static final String TAG = "UpdateMediaRunnable";

    private final Book book;
    private UWUpdaterService updater;
    private String audioUrl;

    public DownloadAudioRunnable(Book book, UWUpdaterService updater, String audioUrl) {
        this.book = book;
        this.updater = updater;
        this.audioUrl = audioUrl;
    }

    @Override
    public void run() {
        downloadMedia();
    }

    private void downloadMedia(){

        new BytesDownloadTask(new BytesDownloadTask.DownloadTaskListener(){
            @Override
            public void downloadFinishedWithJson(byte[] data) {
                Log.d(TAG, "Downloaded media: " + audioUrl);
                saveMediaFile(audioUrl, data);
            }
        }).execute(audioUrl);
    }

    private void saveMediaFile(String url, byte[] data){

        DataFileManager.saveDataForBook(updater.getApplicationContext(), book, data, MediaType.MEDIA_TYPE_AUDIO, url);
        book.update();
        updater.runnableFinished(book.getVersion(), MediaType.MEDIA_TYPE_AUDIO);
    }
}