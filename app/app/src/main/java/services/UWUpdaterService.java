package services;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tasks.JsonDownloadTask;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import utils.UWPreferenceManager;

/**
 * Created by PJ Fechner
 * Service for handling updates to the database
 */
public class UWUpdaterService extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";
    public static final String PROJECTS_JSON_KEY = "cat";
    public static final String MODIFIED_JSON_KEY = "mod";


    int numberPending = 0;

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

        super.onCreate();
    }

    /**
     * Adds a runnable to the Update Manager pool
     * @param runnable runnable to add
     */
    synchronized public void addRunnable(Runnable runnable){

        numberPending++;
        UpdateManager.addRunnable(runnable, 0);
    }

    /**
     * Adds a runnable to the Update Manager pool
     * @param runnable runnable to add
     * @param index as-yet unimplemented possibility for multiple thread pools.
     */
    synchronized public void addRunnable(Runnable runnable, int index){
        UpdateManager.addRunnable(runnable, index);
        numberPending++;
    }

    /**
     * Should be called when a runnable finishes running.
     */
    public void runnableFinished(){

        numberPending--;
//        Log.d(TAG, "a runnable was finished. current Number: " + numberPending);
        if(numberPending == 0){
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
//                    if(lastModified > currentUpdated) {
                        UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), lastModified);
                        addRunnable(new UpdateProjectsRunnable(json.getJSONArray(PROJECTS_JSON_KEY), getThis()));
//                    }
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
