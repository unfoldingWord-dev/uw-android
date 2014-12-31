package services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.List;

import adapter.ViewPagerAdapter;
import db.DBManager;
import models.ChaptersModel;
import models.LanguageModel;
import parser.JsonParser;
import utils.AsyncImageLoader;
import utils.URLDownloadUtil;
import utils.URLUtils;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class DownloadImagesService extends Service implements AsyncImageLoader.onProgressUpdateListener {

    private static final String TAG = "DownloadImagesService";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    DBManager dbManager = null;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private String downloadUrl;
    private boolean serviceState = false;
    private boolean oncomplete = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dbManager = DBManager.getInstance(getApplicationContext());
        serviceState = true;
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 1);
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
                String downloadUrl = extra.getString("downloadUrl");

                this.downloadUrl = downloadUrl;
            }
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        } catch (Exception e) {

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
//            String[] dates = dbManager.getAllDate();
            List<LanguageModel> languages = dbManager.getAllLanguages();
            String[] allDate = dbManager.getAllDate();

            String json = null;
            try {
                json = URLDownloadUtil.downloadJson(URLUtils.LANGUAGE_INFO);
                ArrayList<LanguageModel> info = JsonParser.getInstance().getLanguagesInfo(json);
                for (LanguageModel model : languages) {
                    for (int i = 0; i < info.size(); i++) {
                        if ( model.dateModified < info.get(i).dateModified && model.language.equals(info.get(i).language)) {
                            boolean value = dbManager.upDateLanguage(info.get(i));
                            String chapterJson = URLDownloadUtil.downloadJson(URLUtils.CHAPTER_INFO +
                                    info.get(i).language + "/obs-" + info.get(i).language + ".json");
                            ArrayList<ChaptersModel> chaptersModels = JsonParser.getInstance().getChapterFromLanguage(info.get(i).language, chapterJson);
                            for (ChaptersModel chaptersModel : chaptersModels) {
                                boolean valuea = dbManager.updateChapter(info.get(i).language, chaptersModel);
//                                downloadImage(chaptersModel.imgUrl);
                                Log.d("INSERT", "" + valuea);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }

            if (oncomplete)
                getApplicationContext().sendBroadcast(new Intent(URLUtils.BROAD_CAST_DOWN_COMP));

        }
    }

    private void downloadImage(final String imageURL){

        Log.i(TAG, "will download image url: " + imageURL);

        AsyncImageLoader loader = new AsyncImageLoader(
                new AsyncImageLoader.onImageLoaderListener() {

                    @Override
                    public void onImageLoaded(Bitmap image,
                                              String response) {
                        ViewPagerAdapter.storeImage(getApplicationContext() ,image, imageURL);
                    }

                }, false, false,
                this);
        if (Build.VERSION.SDK_INT >= 11)
            loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    imageURL);
        else
            loader.execute(imageURL);

    }

    @Override
    public void doUpdateProgress(int progress) {

    }
}
