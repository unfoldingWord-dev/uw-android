package services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.db.DBManager;
import model.db.ImageDatabaseHandler;
import model.modelClasses.BookModel;
import model.modelClasses.ChapterModel;
import model.modelClasses.LanguageModel;
import model.modelClasses.PageModel;
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
                Map<String, LanguageModel> newMap = JsonParser.getLanguagesInfo(json);
                boolean shouldUpdateImages = true;

                if(languages.size() > 0) {

                    for (LanguageModel currentModel : languages) {

                        LanguageModel newModel = newMap.get(currentModel.language);

                        if (currentModel.language.equals(newModel.language) && (currentModel.dateModified < newModel.dateModified)) {

                            Log.i(TAG, "Old date: " + currentModel.dateModified + " new Date: " + newModel.dateModified);
                            updateLanguage(newModel, shouldUpdateImages);
                            shouldUpdateImages = false;
                        }
                    }
                }
                else{
                    initializedDatabase(newMap);
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

    private void updateLanguage(LanguageModel newModel, boolean shouldUpdateImages) throws IOException, JSONException{

        dbManager.updateModel(newModel);

        String bookJson = URLDownloadUtil.downloadJson(URLUtils.getUrlForBookUpdate(newModel.language));
        JSONObject bookObj = new JSONObject(bookJson);

        BookModel bookModel = new BookModel();
        bookModel.initModelFromJsonObject(bookObj);
        dbManager.updateModel(bookModel);

        for(ChapterModel chapter : bookModel.getChildModels(getApplicationContext())){

            dbManager.updateModel(chapter);

            for (PageModel page : chapter.getChildModels(getApplicationContext())){
                if(shouldUpdateImages){
                    updateImageForPage(page);
                }
                dbManager.updateModel(page);

            }
        }
    }

    private void initializedDatabase(Map<String, LanguageModel> languages) throws JSONException, IOException{

        for ( String key : languages.keySet()) {

                LanguageModel model = languages.get(key);
                dbManager.updateModel(model);
                updateLanguage(model, false);
        }
    }

    /**
     * Compares the page's image url and updates if necessary
     * @param newModel
     * @return
     */
    private boolean updateImageForPage( PageModel newModel){

        boolean wasSuccessful = true;
        PageModel oldModel = dbManager.getPageForKey(newModel.languageChapterAndPage);

        String newPageUrl = newModel.getComparableImageUrl();
        String oldPageUrl = oldModel.getComparableImageUrl();

        if(!newPageUrl.equalsIgnoreCase(oldPageUrl) ){
            boolean wasSaved = downloadAndSaveImage(newPageUrl);

            if(!wasSaved){
                wasSuccessful = false;
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
