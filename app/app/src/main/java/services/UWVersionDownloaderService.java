package services;

import android.content.Intent;
import android.os.IBinder;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.Book;
import model.daoModels.Version;
import tasks.UpdateBookContentRunnable;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWVersionDownloaderService extends UWUpdaterService {

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

        if(intent.getExtras() != null) {
            long versionId = intent.getExtras().getLong(VERSION_PARAM);
            Version version = Version.getVersionForId(versionId, DaoDBHelper.getDaoSession(getApplicationContext()));

            int i = 1;
            for (Book book : version.getBooks()) {
                addRunnable(new UpdateBookContentRunnable(book, this), i++);
            }
            version.setSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());
            version.update();
        }

        return START_STICKY;
    }
}
