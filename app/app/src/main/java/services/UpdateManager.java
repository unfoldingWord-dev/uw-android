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
import java.util.List;
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
    private final List<ThreadPoolExecutor> updateThreadPools;

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
     * @param index as-yet unimplemented possible multi-pool management param.
     */
    static public void addRunnable(Runnable runnable, int index){
//        Log.d(TAG, "Runnable will be added to index: " + index);
        getInstance().getPoolAtIndex(0).execute(runnable);
    }

    private static UpdateManager getInstance() {
        return ourInstance;
    }

    private UpdateManager() {
        updateThreadPools = new ArrayList<>();
        updateThreadPools.add(getNewThreadExecutor());
    }

    private ThreadPoolExecutor getPoolAtIndex(int index){

        if(index >= updateThreadPools.size()){
            updateThreadPools.add(getNewThreadExecutor());
            return getPoolAtIndex(index);
        }
        else{
            return updateThreadPools.get(index);
        }
    }
    private ThreadPoolExecutor getNewThreadExecutor(){
        return new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
    }
}
