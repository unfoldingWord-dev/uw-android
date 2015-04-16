package services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import model.database.UWDataParser;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.VersionModel;
import signing.Status;
import signing.UWSigning;
import utils.URLUtils;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class VersionDownloadService extends Service{

    private static final String TAG = "UpdateService";

    public static final String VERSION_ID = "VERSION_ID";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    String versionId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("VersionDownloadThread", 1);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String itemId = extra.getString(VERSION_ID);
                this.versionId = itemId;
            }
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        mServiceHandler.shouldStop = true;
        super.onDestroy();
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public boolean shouldStop = false;
        @Override
        public void handleMessage(Message msg) {

            // Get current list of languages
            try {
                VersionModel desiredVersion = new VersionDataSource(getApplicationContext()).getModel(versionId);

                ArrayList<BookModel> books = desiredVersion.getChildModels(getApplicationContext());

                for(BookModel book : books){

                    if(shouldStop){
                        Log.i(TAG, "download Stopped");
                        getApplicationContext().sendBroadcast(new Intent(URLUtils.VERSION_BROADCAST_DOWN_STOPPED).putExtra(VERSION_ID, versionId));
                        return;
                    }

                    if(book.sourceUrl.contains("usfm")){
                        UWDataParser.getInstance(getApplicationContext()).updateUSFMForBook(book);
                    }
                    else{
                        UWDataParser.getInstance(getApplicationContext()).updateStoryChapters(book, false);
                    }
                }
                if(shouldStop){
                    return;
                }
                desiredVersion = UWDataParser.getInstance(getApplicationContext()).updateVersionVerificationStatus(desiredVersion);
                desiredVersion.downloadState = VersionModel.DOWNLOAD_STATE.DOWNLOAD_STATE_DOWNLOADED;
                desiredVersion.getDataSource(getApplicationContext()).createOrUpdateDatabaseModel(desiredVersion);
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (onComplete)
                getApplicationContext().sendBroadcast(new Intent(URLUtils.VERSION_BROADCAST_DOWN_COMP).putExtra(VERSION_ID, versionId));

        }


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "removed");
    }
}
