/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package services;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import model.daoModels.Version;
import model.parsers.MediaType;
import tasks.JsonDownloadTask;
import tasks.UpdateProjectsRunnable;
import utils.UWPreferenceManager;

/**
 * Created by PJ Fechner
 * Service for handling updates to the database
 */
public class UWUpdaterService extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWNLOAD_ENDED = "org.unfoldingword.mobile.BROAD_CAST_DOWNLOAD_ENDED";
    public static final String DOWNLOAD_RESULT_PARAM = "DOWNLOAD_RESULT_PARAM";
    public static final String DOWNLOAD_RESULT_VERSION_PARAM = "DOWNLOAD_RESULT_VERSION_PARAM";
    public static final String DOWNLOAD_RESULT_MEDIA_TYPE_PARAM = "DOWNLOAD_RESULT_MEDIA_TYPE_PARAM";
    public static final int DOWNLOAD_SUCCESS = 0;
    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_CANCELED = 1;
    public static final String PROJECTS_JSON_KEY = "cat";
    public static final String MODIFIED_JSON_KEY = "mod";

    int numberPending = 0;

    private Map<Long, Map<MediaType, AtomicInteger>> versionDownloadTracker = new HashMap<>();

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
        numberPending++;
        UpdateManager.addRunnable(runnable, index);
    }

    synchronized public void addRunnable(Runnable runnable, Version version, MediaType mediaType){

        if(!versionDownloadTracker.containsKey(version.getId())){
            versionDownloadTracker.put(version.getId(), new HashMap<MediaType, AtomicInteger>());
        }
        if(!versionDownloadTracker.get(version.getId()).containsKey(mediaType)){
            versionDownloadTracker.get(version.getId()).put(mediaType, new AtomicInteger(1));
        }
        int num = versionDownloadTracker.get(version.getId()).get(mediaType).incrementAndGet();
        UpdateManager.addRunnable(runnable, version.getId());

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

    /**
     *
     * @param version version of runnable
     * @param type media type of runnable
     * @return true if the process should continue
     */
    public boolean runnableFinished(Version version, MediaType type, boolean success){

        boolean result = false;
        if(!versionDownloadTracker.containsKey(version.getId())){
        }
        else if(!success){
            versionDownloadTracker.get(version.getId()).get(type).set(-1);
            int numLeft = versionDownloadTracker.get(version.getId()).get(type).decrementAndGet();
            if(numLeft == 0){
                Log.d(TAG, "Version runnable failed: " + numLeft + " finished for Version: "  + version.getSlug() + " Media Type: " + type.toString());
                downloadFinished(version, type);
            };
            Log.e(TAG, "Version download failed runnable number: " + " finished for Version: " + version.getSlug() + " Media Type: " + type.toString());
        }
        else{
            int numLeft = versionDownloadTracker.get(version.getId()).get(type).decrementAndGet();
            if(numLeft == 0){
                Log.d(TAG, "Version runnable number: " + numLeft + " finished for Version: "  + version.getSlug() + " Media Type: " + type.toString());
                downloadFinished(version, type);
            };
            Log.d(TAG, "Version runnable number: " + numLeft + " finished for Version: " + version.getSlug() + " Media Type: " + type.toString());
            result = true;
        }
        runnableFinished();
        return result;
    }

    public void downloadFinished(Version version, MediaType type, boolean success){
        versionDownloadTracker.get(version.getId()).remove(type);

        getApplicationContext().sendBroadcast(
                new Intent(BROAD_CAST_DOWNLOAD_ENDED)
                        .putExtra(DOWNLOAD_RESULT_PARAM, DOWNLOAD_SUCCESS)
                        .putExtra(DOWNLOAD_RESULT_VERSION_PARAM, version.getId())
                        .putExtra(DOWNLOAD_RESULT_MEDIA_TYPE_PARAM, type));
    }

    protected void stopService(){
        getApplicationContext().sendBroadcast(
                new Intent(BROAD_CAST_DOWNLOAD_ENDED)
                    .putExtra(DOWNLOAD_RESULT_PARAM, DOWNLOAD_SUCCESS));
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
                    if(lastModified > currentUpdated) {
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

//            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
//                @Override
//                public void downloadFinishedWithJson(String jsonString) {
//
//                try{
//                    JSONArray locales = new JSONArray(jsonString);
//                    addRunnable(new UpdateLanguageLocaleRunnable(locales, getThis()), 10);
//                    runnableFinished();
//
//                }catch (JSONException e){
//                    e.printStackTrace();
//                    runnableFinished();
//                }
//
//                }
//            }).execute(UWPreferenceManager.getLanguagesDownloadUrl(getApplicationContext()));
        }
    }


}
