package services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import tasks.DownloadTask;
import tasks.UpdateProjectsRunnable;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWUpdater extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";

    private Looper mServiceLooper;
    public Handler mServiceHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private UWUpdater getThis(){
        return this;
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("DataDownloadServiceThread", 2);

        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mServiceHandler.post(new UpdateRunnable());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {

            new DownloadTask(new DownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
                        JSONObject json = new JSONObject(jsonString);
                        long lastModified = json.getLong("mod");

                        long currentUpdated = UWPreferenceManager.getLastUpdatedDate(getApplicationContext());
                        if(true){//lastModified > currentUpdated) {
                            UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), lastModified);
                            mServiceHandler.post(new UpdateProjectsRunnable(json.getJSONArray("cat"), getThis()));
                        }

            }
            catch (JSONException e){
                e.printStackTrace();
            }
                }
            })
            .execute(UWPreferenceManager.getDataDownloadUrl(getApplicationContext()));
        }
    }
}
