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
    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A managed pool of background decoder threads
    private final Map<Long, ThreadPoolExecutor> updateThreadPools;
    private static final int unRelatedQueueId = -1;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static UpdateManager ourInstance = new UpdateManager();

    /**
     * Will add the passed runnable to the thread pool
     * @param runnable runnable to add
     * @param id id associate with this threadpool
     */
    static public void addRunnable(Runnable runnable, long id){
//        Log.d(TAG, "Runnable will be added to index: " + index);
        getInstance().getPoolForId(id).execute(runnable);
    }

    /**
     * Will add the passed runnable to the thread pool
     * @param runnable runnable to add
     */
    static public void addRunnable(Runnable runnable){
//        Log.d(TAG, "Runnable will be added to index: " + index);
        addRunnable(runnable, unRelatedQueueId);
    }

    static public void haltQueue(long id){

        getInstance().haltQueueForId(id);
    }

    static public boolean queueIsActive(long id){

        return getInstance().isQueueActive(id);
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

    private boolean isQueueActive(long id){
        return updateThreadPools.get(id).getQueue().size() > 0;
    }

    private int haltQueueForId(long id){

        if(!updateThreadPools.containsKey(id)){
            return 0;
        }
        else{
            return updateThreadPools.get(id).shutdownNow().size();
        }
    }
        /**
         * @param id id of the ThreadPool to use
         * @return ThreadPool matching the passed id
         */
        private ThreadPoolExecutor getPoolForId(long id){

        if(!updateThreadPools.containsKey(id)){
            updateThreadPools.put(id, getNewThreadExecutor());
            return updateThreadPools.get(id);
        }
        else{
            return updateThreadPools.get(id);
        }
    }

    private ThreadPoolExecutor getNewThreadExecutor(){
        return new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
    }
}
