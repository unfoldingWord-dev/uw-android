package services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tasks.JsonDownloadTask;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import utils.URLUtils;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWUpdater extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";
    public static final String PROJECTS_JSON_KEY = "cat";
    public static final String MODIFIED_JSON_KEY = "mod";

    private Looper mServiceLooper;
    private Handler mServiceHandler;

    private SparseArray<Handler> threads;


    int numberRunning = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected UWUpdater getThis(){
        return this;
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("DataDownloadServiceThread", Process.THREAD_PRIORITY_BACKGROUND);

        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        super.onCreate();
        threads = new SparseArray<Handler>();
    }

    public void addRunnable(Runnable runnable){

        numberRunning++;
        mServiceHandler.post(runnable);
    }

    synchronized public void addRunnable(Runnable runnable, int index){

        numberRunning++;
        if(threads.indexOfKey(index) < 0 || threads.get(index) == null){
            HandlerThread thread = new HandlerThread("DataDownloadServiceThreadIndex" + index, Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            Looper looper = thread.getLooper();
            Handler handler = new Handler(looper);
            threads.put(index, handler);
        }
        threads.get(index).post(runnable);
    }

    public void runnableFinished(){

        numberRunning--;
        Log.d(TAG, "a runnable was finished. current Number: " + numberRunning);
        if(numberRunning == 0){
            stopService();
        }
    }

    protected void stopService(){
        getApplicationContext().sendBroadcast(new Intent(BROAD_CAST_DOWN_COMP));
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        addRunnable(new UpdateRunnable());
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {

            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
                        JSONObject json = new JSONObject(jsonString);
                        long lastModified = json.getLong(MODIFIED_JSON_KEY);

                        long currentUpdated = UWPreferenceManager.getLastUpdatedDate(getApplicationContext());
                        if(true){//lastModified > currentUpdated) {
                            UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), lastModified);
                            addRunnable(new UpdateProjectsRunnable(json.getJSONArray(PROJECTS_JSON_KEY), getThis()));
                        }
                        runnableFinished();
                    } catch (JSONException e){
                        e.printStackTrace();
                        runnableFinished();
                    }
                }
            }).execute(UWPreferenceManager.getDataDownloadUrl(getApplicationContext()));

            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
//                        JSONObject jsonObj = new JSONObject(jsonString);
                        JSONArray locales = new JSONArray(jsonString);
                        addRunnable(new UpdateLanguageLocaleRunnable(locales, getThis()), 10);
                        runnableFinished();

                    }catch (JSONException e){
                        e.printStackTrace();
                        runnableFinished();
                    }

                }
            }).execute(UWPreferenceManager.getLanguagesDownloadUrl(getApplicationContext()));

        }
    }
}
