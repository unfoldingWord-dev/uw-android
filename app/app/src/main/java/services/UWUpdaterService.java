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

import de.greenrobot.event.EventBus;
import eventbusmodels.DownloadCancelEvent;
import eventbusmodels.DownloadResult;
import eventbusmodels.DownloadingVersionsEvent;
import model.daoModels.Version;
import model.parsers.MediaType;
import tasks.JsonDownloadTask;
import runnables.UpdateProjectsRunnable;
import utils.UWPreferenceManager;

/**
 * Created by PJ Fechner
 * Service for handling updates to the database
 */
public class UWUpdaterService extends Service {

    private static final String TAG = "UpdateService";

    public static final String PROJECTS_JSON_KEY = "cat";
    public static final String MODIFIED_JSON_KEY = "mod";

//    int numberPending = 0;
//
    private Map<Long, Map<MediaType, AtomicInteger>> downloadTracker = new HashMap<>();

    private static final int unRelatedId = -1;
    private static final MediaType unrelatedMediaType = MediaType.MEDIA_TYPE_NONE;

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

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    /**
     * Adds a runnable to the Update Manager pool
     * @param runnable runnable to add
     */
    synchronized public void addRunnable(Runnable runnable){

        UpdateManager.addRunnable(runnable);
        addToDownloadTracker(-1, MediaType.MEDIA_TYPE_NONE);
//        Log.d(TAG, "added runnable");
    }

    synchronized public void addRunnable(Runnable runnable, Version version, MediaType type){

        UpdateManager.addRunnable(runnable, version.getId(), type);
        addToDownloadTracker(version.getId(), type);
//        Log.d(TAG, "added runnable for version: " + version.getId() + " And mediaType: " + type.toString());

        sendEvent(DownloadingVersionsEvent.getEventAdding(version, type));
    }

    private void sendEvent(DownloadingVersionsEvent event){
        if(event != null){
            EventBus.getDefault().postSticky(event);
        }
    }

    private void addToDownloadTracker(long id, MediaType type){

        if(!downloadTracker.containsKey(id)){
            downloadTracker.put(id, new HashMap<MediaType, AtomicInteger>());
        }
        if(!downloadTracker.get(id).containsKey(type)){
            downloadTracker.get(id).put(type, new AtomicInteger(0));
        }
        int runnableNumber = downloadTracker.get(id).get(type).incrementAndGet();
//        Log.d(TAG, "added runnable for version: " + id + " And mediaType: " + type.toString());
    }

    /**
     * @param id
     * @param type
     * @return true if remaining is > 0
     */
    private boolean decrementDownloadTracker(long id, MediaType type){

        if(!downloadTracker.containsKey(id) || !downloadTracker.get(id).containsKey(type)){
//            Log.d(TAG, "tracker number doesn't exist for: " + id + " And mediaType: " + type.toString());
            return false;
        }
        else{
            int numberLeft = downloadTracker.get(id).get(type).decrementAndGet();
//            Log.d(TAG, "tracker decremented to: " + numberLeft + " for version: " + id + " And mediaType: " + type.toString());
            return numberLeft > 0;
        }
    }

    private void resetDownloadTracker(long id, MediaType type){
        if(downloadTracker.containsKey(id)){
            downloadTracker.remove(id);
        }
    }

    private boolean isActive(){

        int total = 0;
        for (Map.Entry<Long, Map<MediaType, AtomicInteger>> versionEntry : downloadTracker.entrySet()) {

            for(Map.Entry<MediaType, AtomicInteger> typeEntry : versionEntry.getValue().entrySet()){
                total += typeEntry.getValue().get();
            }
        }
        return total > 0;
    }

    /**
     * Should be called when a runnable finishes running.
     */
    public void runnableFinished(){

        if(!decrementDownloadTracker(-1, MediaType.MEDIA_TYPE_NONE)){
            downloadFinished(DownloadResult.DOWNLOAD_RESULT_SUCCESS);

            if(UpdateManager.getActiveNumber() < 1){
                stopService();
            }
        }
//        Log.d(TAG, "runnable finished");
    }

    /**
     *
     * @param version version of runnable
     * @param type media type of runnable
     */
    public void runnableFinished(Version version, MediaType type){

        if(!decrementDownloadTracker(version.getId(), type)){
            downloadFinished(version, type, DownloadResult.DOWNLOAD_RESULT_SUCCESS);

            if(!isActive()){
                stopService();
            }
        }

//        Log.d(TAG, "runnable finished for version: " + version.getId() + " And mediaType: " + type.toString());
    }

    public void runnableFailed(){
        downloadFinished(DownloadResult.DOWNLOAD_RESULT_FAILED);
    }

    public void runnableFailed(Version version, MediaType type){
        downloadFinished(version, type, DownloadResult.DOWNLOAD_RESULT_FAILED);
    }

    public void downloadFinished(DownloadResult success){
        UpdateManager.haltQueue();
        resetDownloadTracker(-1, MediaType.MEDIA_TYPE_NONE);
        EventBus.getDefault().post(success);
    }

    public void downloadFinished(Version version, MediaType type, DownloadResult success){
        UpdateManager.haltQueue(version.getId(), type);
        resetDownloadTracker(version.getId(), type);
        sendEvent(DownloadingVersionsEvent.getEventRemoving(version, type));
        EventBus.getDefault().post(success);
    }

    private void haltDownload(Version version, MediaType type){
        UpdateManager.haltQueue(version.getId(), type);
        downloadFinished(DownloadResult.DOWNLOAD_RESULT_CANCELED);
        if(!isActive()){
            stopService();
        }
    }

    private void haltDownload(){
        UpdateManager.haltQueue();
        downloadFinished(DownloadResult.DOWNLOAD_RESULT_CANCELED);
        if(!isActive()){
            stopService();
        }
    }

    private void haltAllDownloads(){
        downloadFinished(DownloadResult.DOWNLOAD_RESULT_CANCELED);
        stopService();
    }

    protected void stopService(){
        EventBus.getDefault().unregister(this);
        UpdateManager.haltAllThreads();
        this.stopSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        EventBus.getDefault().unregister(this);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        addRunnable(new UpdateRunnable());
        EventBus.getDefault().register(this);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public void onEventBackgroundThread(DownloadCancelEvent event){

        if(event.type == null || event.version == null){
            if(event.haltAll){
                haltAllDownloads();
            }
            else {
                haltDownload();
            }
        }
        else{
            haltDownload(event.version, event.type);
        }
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
        }
    }
}
