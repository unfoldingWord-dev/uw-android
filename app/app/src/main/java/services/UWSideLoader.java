package services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import model.database.UWDataParser;

/**
 * Created by Fechner on 3/14/15.
 */
public class UWSideLoader extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private String json;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 2);
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
                String json = extra.getString("json");
                this.json = json;
            }
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        } catch (Exception e) {

        }
        return START_STICKY;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            try {
                JSONArray jsonArray = new JSONArray(json);

                UWDataParser.getInstance(getApplicationContext()).updateProjects(jsonArray, true);

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void addContentToDatabase(String content){

        try {
            JSONArray jsonArray = new JSONArray(content);

            UWDataParser.getInstance(getApplicationContext()).updateProjects(jsonArray, true);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
