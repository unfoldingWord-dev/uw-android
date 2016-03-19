/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package services;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import model.parsers.MediaType;

/**
 * Created by PJ Fechner on 8/21/15.
 * Class to manage a thread pool in order to make full use of all cores during a data update.
 */
public class UpdateManager {

    private static final String TAG = "UpdateManager";

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 100;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    }
    // Sets the initial thread pool size to 8
    private static final int CORE_POOL_SIZE = 2;

    // Sets the maximum thread pool size to 8
    private static final int MAXIMUM_POOL_SIZE = 2;

    // A managed pool of background decoder threads
    private final Map<Long, Map<MediaType, ThreadPoolExecutor>> updateThreadPools;
    private static final int unRelatedQueueId = -1;
    private static final MediaType unrelatedMediaType = MediaType.MEDIA_TYPE_NONE;


    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = 2;// Runtime.getRuntime().availableProcessors();

    private static UpdateManager ourInstance = new UpdateManager();


    //region adding

    /**
     * Will add the passed runnable to the thread pool
     * @param runnable runnable to add
     * @param id id associate with this threadpool
     */
    synchronized static public void addRunnable(Runnable runnable, long id, MediaType type){
//        Log.d(TAG, "Runnable will be added to index: " + index);
        getInstance().getPool(id, type).execute(runnable);
    }

    /**
     * Will add the passed runnable to the thread pool
     * @param runnable runnable to add
     */
    static public void addRunnable(Runnable runnable){
//        Log.d(TAG, "Runnable will be added to index: " + index);
        addRunnable(runnable, unRelatedQueueId, unrelatedMediaType);
    }

    //endregion

    static public void haltQueue(){

        getInstance().forceHaltQueue(unRelatedQueueId, unrelatedMediaType);
    }

    static synchronized public void haltQueue(long id, MediaType type){

        getInstance().forceHaltQueue(id, type);
    }

    static public boolean queueIsActive(){

        return queueIsActive(unRelatedQueueId, unrelatedMediaType);
    }

    static public boolean queueIsActive(long id, MediaType type){

        return getInstance().isQueueActive(id, type);
    }

    private static UpdateManager getInstance() {
        if(ourInstance == null){
            ourInstance = new UpdateManager();
        }

        return ourInstance;
    }

    private UpdateManager() {
        updateThreadPools = new HashMap<>();
    }

    private boolean isQueueActive(long id, MediaType type){
        if(!updateThreadPools.containsKey(id) || !updateThreadPools.get(id).containsKey(type)){
//            Log.d(TAG, "queue isn't active because there it doesn't exist in the pool");
            return false;
        }
        int active = updateThreadPools.get(id).get(type).getActiveCount();
//        Log.d(TAG, "queue with number active: " + active + " for id: " + id + " and type: " + type.toString());
        return  active > 1;
    }

    private int forceHaltQueue(long id, MediaType type){

        if(!updateThreadPools.containsKey(id) || !updateThreadPools.get(id).containsKey(type)){
            return -1;
        }
        else{
            int numberHalted = updateThreadPools.get(id).get(type).shutdownNow().size();
            updateThreadPools.get(id).remove(type);
            return numberHalted;
        }
    }

    public static long getActiveNumber(){

        long total = 0;
        for (Map.Entry<Long, Map<MediaType, ThreadPoolExecutor>> versionEntry : getInstance().updateThreadPools.entrySet()) {

            for(Map.Entry<MediaType, ThreadPoolExecutor> typeEntry : versionEntry.getValue().entrySet()){
                total += typeEntry.getValue().getTaskCount();
            }
        }
        return total;
    }

    public static void haltAllThreads(){

        for (Map.Entry<Long, Map<MediaType, ThreadPoolExecutor>> versionEntry : getInstance().updateThreadPools.entrySet()) {

            for(Map.Entry<MediaType, ThreadPoolExecutor> typeEntry : versionEntry.getValue().entrySet()){
                typeEntry.getValue().shutdownNow();
            }
        }
        ourInstance = null;
    }

    /**
     * @param id id of the ThreadPool to use
     * @return ThreadPool matching the passed id
     */
    private synchronized ThreadPoolExecutor getPool(long id, MediaType type){

        if(!updateThreadPools.containsKey(id)){
            updateThreadPools.put(id, new HashMap<MediaType, ThreadPoolExecutor>());
        }
        if(!updateThreadPools.get(id).containsKey(type)){
            updateThreadPools.get(id).put(type, getNewThreadExecutor());
        }
        return updateThreadPools.get(id).get(type);
    }

    private ThreadPoolExecutor getNewThreadExecutor(){
        return new ThreadPoolExecutor((NUMBER_OF_CORES < 2)? 2 : NUMBER_OF_CORES, (NUMBER_OF_CORES < 2)? 2 : NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
    }
}
