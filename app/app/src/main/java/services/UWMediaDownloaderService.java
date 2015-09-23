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

    public static final String STOP_DOWNLOAD_VERSION_MESSAGE = "STOP_DOWNLOAD_VERSION_MESSAGE";
    public static final String VERSION_ID_PARAM = "VERSION_ID_PARAM";
    public static final String IS_VIDEO_PARAM = "IS_VIDEO_PARAM";

    private static final String TAG = "UWVersionDownloader";
    public static final String VERSION_PARAM = "VERSION_PARAM";

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
                addRunnable(new UpdateBookContentRunnable(book, this), i++);
            }
        }

        return START_STICKY;
    }

    private class UpdateMediaRunnable implements Runnable {

        private Version updateVersion;
        private boolean isUpdatingVideo;

        public UpdateMediaRunnable(boolean isUpdatingVideo, Version updateVersion) {
            this.isUpdatingVideo = isUpdatingVideo;
            this.updateVersion = updateVersion;
        }

        @Override
        public void run() {

            updateVersion(updateVersion);

        }

        private void updateVersion(Version version ){

            for(Book book : version.getBooks()){

                for(AudioChapter chapter : book.getAudioBook().getAudioChapters()){
                    downloadMedia(chapter.getSource());
                }
            }
        }

        private void downloadMedia(final String url){

            new BytesDownloadTask(new BytesDownloadTask.DownloadTaskListener(){
                @Override
                public void downloadFinishedWithJson(byte[] data) {
                    saveMediaFile(url, data);
                }
            }).execute(url);
        }

        private void saveMediaFile(String url, byte[] data){
            try{
                FileOutputStream fos = getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
                fos.write(data);
                fos.close();
                Log.i(TAG, "File Saved");
            }
            catch (IOException e){
                e.printStackTrace();
                Log.e(TAG, "Error when saving USFM");
            }
        }
    }
}
