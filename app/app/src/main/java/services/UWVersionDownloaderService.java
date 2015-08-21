package services;

import android.content.Intent;
import android.os.IBinder;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.Book;
import model.daoModels.Version;
import tasks.UpdateBookContentRunnable;

/**
 * Created by PJ fechner
 * Service to download and add a Version to the DB
 */
public class UWVersionDownloaderService extends UWUpdaterService {

    public static final String STOP_DOWNLOAD_VERSION_MESSAGE = "STOP_DOWNLOAD_VERSION_MESSAGE";
    public static final String VERSION_ID = "VERSION_ID";

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
            Version version = Version.getVersionForId(versionId, DaoDBHelper.getDaoSession(getApplicationContext()));

            int i = 1;
            for (Book book : version.getBooks()) {
                addRunnable(new UpdateBookContentRunnable(book, this), i++);
            }
        }

        return START_STICKY;
    }
}
