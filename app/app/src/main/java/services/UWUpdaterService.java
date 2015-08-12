package services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import tasks.JsonDownloadTask;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWUpdaterService extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";
    public static final String PROJECTS_JSON_KEY = "cat";
    public static final String MODIFIED_JSON_KEY = "mod";
    private static final int MAX_NUMBER_THREADS = 20;

    private Looper mServiceLooper;

    private Handler mServiceHandler;


    private UpdaterThread[] threads;

    int numberRunning = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected UWUpdaterService getThis(){
        return this;
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("DataDownloadServiceThread", Process.THREAD_PRIORITY_DEFAULT);

        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        super.onCreate();
        threads = new UpdaterThread[MAX_NUMBER_THREADS];
    }

    public void addRunnable(Runnable runnable){

        numberRunning++;
        mServiceHandler.post(runnable);
    }

    synchronized public void addRunnable(Runnable runnable, int index){

        numberRunning++;
        for(int i = 0; i < MAX_NUMBER_THREADS; i++){

            if(threads[i] == null){
                threads[i] = new UpdaterThread("DataDownloadServiceThreadIndex" + i, Process.THREAD_PRIORITY_DEFAULT);
                threads[i].start();
            }
            if(threads[i].isIdle){
                boolean added = threads[i].post(runnable);
                if(added){
                    Log.i(TAG, "Added to thread: " + i);

                    return;
                }
            }
        }

        Log.i(TAG, "All threads were in use");

        //fall through condition
        Random r = new Random();
        int num = r.nextInt(MAX_NUMBER_THREADS);
        boolean added = threads[num].post(runnable);

        if(!added){
            Log.e(TAG, "Could not add to thread for some reason");
//            addRunnable(runnable);
        }
        else {
            Log.i(TAG, "Added to thread: " + num);
        }
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
        for(UpdaterThread thread : threads){
            thread.safeStop();
            thread.quit();
        }
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

    private static class UpdaterThread extends HandlerThread{

        private boolean keepGoing = true;
        private Handler handler;

        public boolean isIdle = false;
        public UpdaterThread(String name) {
            this(name, Process.THREAD_PRIORITY_DEFAULT);
        }

        public UpdaterThread(String name, int priority) {
            super(name, priority);
        }

        public void safeStop(){
            keepGoing = false;
        }

        @Override
        public synchronized void start() {
            super.start();
            handler = new Handler(getLooper());

            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    isIdle = true;
                    return keepGoing;
                }
            });
        }

        public boolean post(Runnable runnable){
            isIdle = false;
            return this.handler.post(runnable);
        }
    }
}
