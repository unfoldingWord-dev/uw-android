/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import model.AudioBitrate;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.AudioChapter;
import model.daoModels.Book;
import model.parsers.MediaType;
import services.UWUpdaterService;
import utils.FileNameHelper;

/**
 * Created by Fechner on 9/24/15.
 */
public class UpdateMediaRunnable implements Runnable {
    private static final String TAG = "UpdateMediaRunnable";

    private final Book book;
    private boolean isUpdatingVideo;
    private UWUpdaterService updater;
    private AudioBitrate bitrate;

    public UpdateMediaRunnable(boolean isUpdatingVideo, Book book, UWUpdaterService updater, AudioBitrate bitrate) {
        this.book = book;
        this.isUpdatingVideo = isUpdatingVideo;
        this.updater = updater;
        this.bitrate = bitrate;
    }

    @Override
    public void run() {
        updateBook();
    }

    private void updateBook(){

        if(!isUpdatingVideo) {

            if(!isUpdatingVideo){
                book.update();
            }
            int numberOfChapters = book.getAudioBook().getAudioChapters().size();
            int i = 1;
            for (AudioChapter chapter : book.getAudioBook().getAudioChapters()) {
//                for(AudioBitrate bitrate : chapter.getBitRates()){
                    downloadMedia(chapter.getAudioUrl(bitrate.getBitrate()), (i >= numberOfChapters));
//                }
//                downloadMedia(chapter.getAudioUrl(), (i >= numberOfChapters));
                i++;
            }
        }
    }

    private void downloadMedia(final String url, final boolean isLast){

        new BytesDownloadTask(new BytesDownloadTask.DownloadTaskListener(){
            @Override
            public void downloadFinishedWithJson(byte[] data) {
                Log.d(TAG, "Downloaded media: " + url);
                saveMediaFile(url, data, isLast);
            }
        }).execute(url);
    }

    private void saveMediaFile(String url, byte[] data, boolean isLast){

        DataFileManager.saveDataForBook(updater.getApplicationContext(), book, data, MediaType.MEDIA_TYPE_AUDIO, url);

//        try{
//            FileOutputStream fos = updater.getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
//            fos.write(data);
//            fos.close();
//            Log.i(TAG, "Media Saved: " + url);
//        }
//        catch (IOException e){
//            e.printStackTrace();
//            Log.e(TAG, "Error when saving media file");
//        }
        if(isLast) {
//            if(isUpdatingVideo){
//                book.setVideoSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());
//            }
//            else {
//                book.setAudioSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());
//            }
            book.update();
            updater.runnableFinished();
        }
    }
}