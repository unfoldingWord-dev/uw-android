package services;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import model.DaoDBHelper;
import model.daoModels.AudioChapter;
import model.daoModels.Book;
import model.daoModels.Version;
import tasks.BytesDownloadTask;
import tasks.UpdateBookContentRunnable;
import utils.FileNameHelper;

/**
 * Created by PJ fechner
 * Service to download and add a Version to the DB
 */
public class UWMediaDownloaderService extends UWUpdaterService {

    public static final String STOP_DOWNLOAD_MEDIA_MESSAGE = "STOP_DOWNLOAD_MEDIA_MESSAGE";
    public static final String VERSION_PARAM = "VERSION_PARAM";
    public static final String IS_VIDEO_PARAM = "IS_VIDEO_PARAM";

    private static final String TAG = "MediaDownloaderService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null && intent.getExtras() != null) {
            long versionId = intent.getExtras().getLong(VERSION_PARAM);
            boolean isVideo = intent.getExtras().getBoolean(IS_VIDEO_PARAM);
            Version version = Version.getVersionForId(versionId, DaoDBHelper.getDaoSession(getApplicationContext()));

            int i = 1;
            for (Book book : version.getBooks()) {
                addRunnable(new UpdateMediaRunnable(false, book), i++);
            }
        }

        return START_STICKY;
    }

    private class UpdateMediaRunnable implements Runnable {

        private final Book book;
        private boolean isUpdatingVideo;

        public UpdateMediaRunnable(boolean isUpdatingVideo, Book book) {
            this.book = book;
            this.isUpdatingVideo = isUpdatingVideo;
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
            runnableFinished();
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
                FileOutputStream fos = getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
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
}
