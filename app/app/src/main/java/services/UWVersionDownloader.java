package services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.daoModels.Book;
import model.daoModels.Version;
import tasks.JsonDownloadTask;
import tasks.UpdateBookContentRunnable;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import utils.URLUtils;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWVersionDownloader extends UWUpdater {

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

        Version version = (Version) intent.getSerializableExtra(VERSION_PARAM);

        for(Book book : version.getBooks()) {
            addRunnable(new UpdateBookContentRunnable(book, this));
        }

        return START_STICKY;
    }
}
