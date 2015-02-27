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
import java.util.HashMap;
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

            // Get current list of languages
            HashMap<String, LanguageModel> languages = dbManager.getLanguagesAsHashMap();

            String json = null;
            try {

                // Download current LanguageJSON
                json = URLDownloadUtil.downloadJson(URLUtils.getUrlForLanguageUpdate());
                Map<String, LanguageModel> newMap = JsonParser.getLanguagesInfo(json);
                boolean shouldUpdateImages = true;

                if(languages.size() > 0) {

                    // Iterate through the current Models
                    for (LanguageModel newModel : newMap.values()) {

                        LanguageModel currentModel = (languages.containsKey(newModel.language))? languages.get(newModel.language) : null;

                        // Check if current
                        if (currentModel == null || (currentModel.dateModified < newModel.dateModified)) {

                            String dateModified = (currentModel == null)? "null" : Long.toString(currentModel.dateModified);
                            if(currentModel == null){
                                shouldUpdateImages = false;
                            }
                            // Update
                            Log.i(TAG, "Old date: " + dateModified + " new Date: " + newModel.dateModified);
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

    /**
     * Updates the passed model, will update the images by comparing the PageModel image urls if
     * shouldUpdateImages == true
     * @param newModel
     * @param shouldUpdateImages
     * @throws IOException
     * @throws JSONException
     */
    private void updateLanguage(LanguageModel newModel, boolean shouldUpdateImages) throws IOException, JSONException{

        //update the Language Model
        dbManager.updateModel(newModel);

        //download Language's companion JSON
        String bookJson = URLDownloadUtil.downloadJson(URLUtils.getUrlForBookUpdate(newModel.language));
        JSONObject bookObj = new JSONObject(bookJson);

        BookModel bookModel = new BookModel();
        bookModel.initModelFromJsonObject(bookObj);

        // Update the Book
        dbManager.updateModel(bookModel);

        // Iterate through book's chapters
        for(ChapterModel chapter : bookModel.getChildModels(getApplicationContext())){

            // Update the chapter
            dbManager.updateModel(chapter);

            // iterate through Pages
            for (PageModel page : chapter.getChildModels(getApplicationContext())){

                // Optionally update images
                if(shouldUpdateImages){
                    updateImageForPage(page);
                }
                // Update page
                dbManager.updateModel(page);

            }
        }
    }

    /**
     * Method for initializing the Database from JSON if no database exists.
     * @param languages
     * @throws JSONException
     * @throws IOException
     */
    private void initializedDatabase(Map<String, LanguageModel> languages) throws JSONException, IOException{

        for ( String key : languages.keySet()) {

                LanguageModel model = languages.get(key);
                dbManager.updateModel(model);
                updateLanguage(model, false);
        }
    }

    /**
     * Compares the page's image url to what currently exists in the DB and updates if necessary
     * @param newModel
     * @return
     */
    private boolean updateImageForPage( PageModel newModel){

        boolean wasSuccessful = true;
        // get the old Page
        PageModel oldModel = dbManager.getPageModelForKey(newModel.languageChapterAndPage);

        if(oldModel == null){
            return false;
        }

        String newPageUrl = newModel.getComparableImageUrl();
        String oldPageUrl = oldModel.getComparableImageUrl();

        // compare the urls
        if(!newPageUrl.equalsIgnoreCase(oldPageUrl) ){

            // download/save updated image
            boolean wasSaved = downloadAndSaveImage(newPageUrl);

            if(!wasSaved){
                wasSuccessful = false;
            }
        }

        return wasSuccessful;
    }

    /**
     * Downloads and saves the image located at the passed url String
     * @param imageURL
     * @return
     */
    private boolean downloadAndSaveImage(final String imageURL){

        Log.i(TAG, "will download image url: " + imageURL);

        Bitmap image = AsyncImageLoader.downloadImage(imageURL);
        return ImageDatabaseHandler.storeImage(getApplicationContext(), image, URLUtils.getLastBitFromUrl(imageURL));
    }

    @Override
    public void doUpdateProgress(int progress) {

    }
}
