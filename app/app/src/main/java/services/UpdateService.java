package services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.ViewPagerAdapter;
import db.DBManager;
import db.ImageDatabaseHandler;
import models.ChapterModel;
import models.LanguageModel;
import models.PageModel;
import parser.JsonParser;
import utils.AsyncImageLoader;
import utils.URLDownloadUtil;
import utils.URLUtils;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UpdateService extends Service implements AsyncImageLoader.onProgressUpdateListener {

    private static final String TAG = "UpdateService";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    DBManager dbManager = null;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private String downloadUrl;
    private boolean serviceState = false;
    private boolean onComplete = true;

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

            List<LanguageModel> languages = dbManager.getAllLanguages();

            String json = null;
            try {
                json = URLDownloadUtil.downloadJson(URLUtils.getUrlForLanguageUpdate());
                ArrayList<LanguageModel> info = JsonParser.getInstance().getLanguagesInfo(json);
                boolean didUpdateImages = false;
                for (LanguageModel currentModel : languages) {
                    for (LanguageModel newModel : info) {
                        if (currentModel.language.equals(newModel.language) && (currentModel.dateModified < newModel.dateModified)) {

                            Log.i(TAG, "Old date: " + currentModel.dateModified + " new Date: "  + newModel.dateModified);
                            String bookJson = URLDownloadUtil.downloadJson(URLUtils.getUrlForBookUpdate(newModel.language));
                            newModel.addBooksFromJson(bookJson);

                            dbManager.updateLanguage(newModel);

                            if(!didUpdateImages) {
                                Map<String,  PageModel> oldChapters = currentModel.getAllPagesAsDictionary();
                                Map<String,  PageModel>  newChapters = newModel.getAllPagesAsDictionary();
                                didUpdateImages = updateImagesForChapters(oldChapters, newChapters);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (onComplete)
                getApplicationContext().sendBroadcast(new Intent(URLUtils.BROAD_CAST_DOWN_COMP));

        }
    }

    /**
     * Compares the page's image url and updates if necessary
     * @param oldPages
     * @param newPages
     * @return
     */
    private boolean updateImagesForChapters( Map<String,  PageModel>  oldPages,  Map<String,  PageModel> newPages){

        if(oldPages == null || newPages == null){
            return false;
        }
        Log.i(TAG, "Will Update Images");
        boolean wasSuccessful = true;

        for(String key : oldPages.keySet()){

            PageModel newPage = newPages.get(key);
            PageModel oldPage = oldPages.get(key);

            String newPageUrl = newPage.getComparableImageUrl();
            String oldPageUrl = oldPage.getComparableImageUrl();

            if(!newPageUrl.equalsIgnoreCase(oldPageUrl) ){
                boolean wasSaved = downloadAndSaveImage(newPage.getComparableImageUrl());
                if(!wasSaved){
                    wasSuccessful = false;
                }
            }
        }
        return wasSuccessful;
    }

    private boolean downloadAndSaveImage(final String imageURL){

        Log.i(TAG, "will download image url: " + imageURL);

        Bitmap image = AsyncImageLoader.downloadImage(imageURL);
        return ImageDatabaseHandler.storeImage(getApplicationContext(), image, URLUtils.getLastBitFromUrl(imageURL));
    }

    @Override
    public void doUpdateProgress(int progress) {

    }
}
