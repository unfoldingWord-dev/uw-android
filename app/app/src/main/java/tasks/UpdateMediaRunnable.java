package tasks;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import model.daoModels.AudioChapter;
import model.daoModels.Book;
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

    public UpdateMediaRunnable(boolean isUpdatingVideo, Book book, UWUpdaterService updater) {
        this.book = book;
        this.isUpdatingVideo = isUpdatingVideo;
        this.updater = updater;

    }

    @Override
    public void run() {

        updateBook();

    }

    private void updateBook(){

        if(isUpdatingVideo && !book.getVideoIsDownloaded() || !isUpdatingVideo && !book.getAudioIsDownloaded()) {

            for (AudioChapter chapter : book.getAudioBook().getAudioChapters()) {
                downloadMedia(chapter.getSource());
            }
            if(isUpdatingVideo){
                book.setVideoIsDownloaded(true);
            }
            else {
                book.setAudioIsDownloaded(true);
            }
            book.update();
        }
        updater.runnableFinished();
    }

    private void downloadMedia(final String url){

        new BytesDownloadTask(new BytesDownloadTask.DownloadTaskListener(){
            @Override
            public void downloadFinishedWithJson(byte[] data) {
                Log.d(TAG, "Downloaded media: " + url);
                saveMediaFile(url, data);
            }
        }).execute(url);
    }

    private void saveMediaFile(String url, byte[] data){
        try{
            FileOutputStream fos = updater.getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
            fos.write(data);
            fos.close();
            Log.i(TAG, "Media Saved: " + url);
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving media file");
        }
    }
}