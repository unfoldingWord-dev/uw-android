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
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.unfoldingword.mobile.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import model.database.DBManager;
import model.database.ImageDatabaseHandler;
import model.database.UWDataParser;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.PageModel;
import model.modelClasses.mainData.StoriesChapterModel;
import utils.AsyncImageLoader;
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

    private boolean hasUpdatedImages;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dbManager = DBManager.getInstance(getApplicationContext());
        serviceState = true;
        HandlerThread thread = new HandlerThread("UpdateServiceThread", 1);
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
            try {
                hasUpdatedImages = false;
                String url = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("base_url",  getResources().getString(R.string.pref_default_base_url));
                UWDataParser.getInstance(getApplicationContext()).updateProjects(url, false);

//                String languagesUrl = getResources().getString(R.string.languages_json_url);
//                UWDataParser.getInstance(getApplicationContext()).downloadAndUpdateLanguageLocales(languagesUrl);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (onComplete)
                getApplicationContext().sendBroadcast(new Intent(URLUtils.BROAD_CAST_DOWN_COMP));

        }
    }

//    private void updateProjects(boolean forceUpdate) throws JSONException, IOException{
//
////        Log.i(TAG, " Updating Projects");
//        String url = PreferenceManager.getDefaultSharedPreferences(this).getString("base_url",  getResources().getString(R.string.pref_default_base_url));
//        String json = URLDownloadUtil.downloadJson(url);
//
//        JSONArray jsonArray = new JSONArray(json);
//        ProjectDataSource dataSource = new ProjectDataSource(this.getApplicationContext());
//
//        if(jsonArray.length() > 0) {
//
//            // Iterate through the current Models
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                ProjectModel newModel = new ProjectModel(jsonArray.getJSONObject(i));
//                ProjectModel currentModel = dataSource.getModelForUniqueSlug(newModel.slug);
//
//                if(currentModel != null) {
//                    newModel.uid = currentModel.uid;
//                }
//
//                if (currentModel == null || (currentModel.dateModified < newModel.dateModified) || forceUpdate) {
//                    dataSource.saveModel(newModel);
//                    updateLanguages(dataSource.getModelForUniqueSlug(newModel.slug), forceUpdate);
//                }
//            }
//        }
//    }
//
//    private void updateLanguages(ProjectModel parent, boolean forceUpdate) throws JSONException, IOException{
//
////        Log.i(TAG, " Updating Languages");
//        String json = URLDownloadUtil.downloadJson(parent.languageUrl);
//
//        JSONArray jsonArray = new JSONArray(json);
//        LanguageDataSource dataSource = new LanguageDataSource(this.getApplicationContext());
//
//        if(jsonArray.length() > 0) {
//
//            // Iterate through the current Models
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                LanguageModel newModel = new LanguageModel(jsonArray.getJSONObject(i), parent);
//                LanguageModel currentModel = dataSource.getModelForUniqueSlug(newModel.slug);
//
//                if(currentModel != null) {
//                    newModel.uid = currentModel.uid;
//                }
//
//                if (currentModel == null || (currentModel.dateModified < newModel.dateModified) || forceUpdate) {
//                    dataSource.saveModel(newModel);
//                    updateVersions(dataSource.getModelForUniqueSlug(newModel.slug), forceUpdate);
//                }
//            }
//        }
//    }
//
//    private void updateVersions(LanguageModel parent, boolean forceUpdate) throws JSONException, IOException{
//
////        Log.i(TAG, " Updating Versions");
//        String json = URLDownloadUtil.downloadJson(parent.resourceUrl);
//
//        JSONArray jsonArray = new JSONArray(json);
//        VersionDataSource dataSource = new VersionDataSource(this.getApplicationContext());
//
//        if(jsonArray.length() > 0) {
//
//            // Iterate through the current Models
//            for (int i = 0; i < jsonArray.length(); i++) {
//
//                VersionModel newModel = new VersionModel(jsonArray.getJSONObject(i), parent);
//                VersionModel currentModel = dataSource.getModelForUniqueSlug(newModel.slug);
//
//                if(currentModel != null) {
//                    newModel.uid = currentModel.uid;
//                }
//
//                if (currentModel == null ||  (currentModel.dateModified < newModel.dateModified) || forceUpdate) {
//
//                    dataSource.saveModel(newModel);
//
//                    if(newModel.usfmUrl.length() > 1){
//                        this.parseUSFMForVersion(dataSource.getModelForUniqueSlug(newModel.slug));
//                    }
//                    else {
//                        updateBooks(dataSource.getModelForUniqueSlug(newModel.slug), forceUpdate);
//                    }
//                }
//            }
//        }
//    }
//
//    private void parseUSFMForVersion(VersionModel version) throws IOException{
//
////        Log.i(TAG, " parsing usfm");
//
//        byte[] usfmText = URLDownloadUtil.downloadUsfm(version.usfmUrl);
//
//        Map<String, String> usfmMap = USFMParser.parseUsfm(usfmText);
//
//        ArrayList<BibleChapterModel> chapters = version.getBibleChildModels(getApplicationContext());
//
//        Context context = getApplicationContext();
//        for (Map.Entry<String, String> entry : usfmMap.entrySet()){
//            BibleChapterModel chapter = new BibleChapterModel();
//            chapter.parentId = version.uid;
//            chapter.number = entry.getKey();
//            chapter.text = entry.getValue();
//
//            for(BibleChapterModel oldChapter : chapters){
//                if(Long.parseLong(oldChapter.number.replaceAll("[^0-9]", "")) == Long.parseLong(chapter.number.replaceAll("[^0-9]", ""))){
//                    chapter.uid = oldChapter.uid;
//                }
//            }
//
//            new BibleChapterDataSource(context).saveModel(chapter);
//        }
//    }
//
//
//    private void updateBooks(VersionModel parent, boolean forceUpdate) throws JSONException, IOException{
//
////        Log.i(TAG, " Updating Books");
//
//        String json = URLDownloadUtil.downloadJson(parent.sourceUrl);
//
//        JSONObject jsonObject = new JSONObject(json);
//        BookDataSource dataSource = new BookDataSource(this.getApplicationContext());
//
//        BookModel newModel = new BookModel(jsonObject, parent);
//        BookModel currentModel = dataSource.getModelForLanguage(newModel.language);
//
//        if(currentModel != null) {
//            newModel.uid = currentModel.uid;
//        }
//
//        if (currentModel == null || (currentModel.dateModified < newModel.dateModified) || forceUpdate) {
//
//            dataSource.saveModel(newModel);
//            updateStoryChapters(dataSource.getModelForUniqueSlug(newModel.slug) , jsonObject.getJSONArray("chapters"));
//
//            hasUpdatedImages = true;
//        }
//    }
//
//    private void updateStoryChapters(BookModel parent, JSONArray jsonArray) throws JSONException, IOException{
//
////        Log.i(TAG, " Updating Chapters");
//
//
//        StoriesChapterDataSource dataSource = new StoriesChapterDataSource(this.getApplicationContext());
//
////        // Iterate through the current Models
//        for (int i = 0; i < jsonArray.length(); i++) {
//
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//            StoriesChapterModel newModel = new StoriesChapterModel(jsonObject, parent);
//            StoriesChapterModel currentModel = dataSource.getModelForUniqueSlug(newModel.slug);
//
//            if(currentModel != null) {
//                newModel.uid = currentModel.uid;
//            }
//
//                dataSource.saveModel(newModel);
//                updateStoryPage(dataSource.getModelForUniqueSlug(newModel.slug), jsonObject.getJSONArray("frames"));
//        }
//    }
//
//    private void updateStoryPage(StoriesChapterModel parent, JSONArray jsonArray) throws JSONException, IOException{
//
////        Log.i(TAG, " Updating Page");
//
//
//        PageDataSource dataSource = new PageDataSource(this.getApplicationContext());
//
////        // Iterate through the current Models
//        for (int i = 0; i < jsonArray.length(); i++) {
//
//            PageModel newModel = new PageModel(jsonArray.getJSONObject(i), parent);
//            PageModel currentModel = dataSource.getModelForUniqueSlug(newModel.slug);
//
//            if(currentModel != null) {
//                newModel.uid = currentModel.uid;
//            }
//
//            if(!hasUpdatedImages){
//                updateImageForPages(currentModel, newModel );
//            }
//
//
//            dataSource.saveModel(newModel);
//    }
//}

    private void updateImages(ArrayList<StoriesChapterModel> currentChapters, ArrayList<StoriesChapterModel> newChapters){
//        Log.i(TAG, " Updating Images");

        if(currentChapters == null || newChapters == null){
            return;
        }

//        Log.i(TAG, " Updating Images");
//        Map<Long, PageModel> currentPages = currentBook.getPages(this.getApplicationContext());
//        Map<Long, PageModel> newPages = newBook.getPages(this.getApplicationContext());
//
//        for (Map.Entry<Long, PageModel> entry : currentPages.entrySet()){
//
//            updateImageForPages(entry.getValue(), newPages.get(entry.getKey()));
//        }
    }

    /**
     * Compares the page's image url to what currently exists in the DB and updates if necessary
     * @param newModel
     * @return
     */
    private boolean updateImageForPages(PageModel oldModel, PageModel newModel){

        boolean wasSuccessful = true;

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

//        Log.i(TAG, "will download image url: " + imageURL);

        Bitmap image = AsyncImageLoader.downloadImage(imageURL);
        return ImageDatabaseHandler.storeImage(getApplicationContext(), image, AsyncImageLoader.getLastBitFromUrl(imageURL));
    }

    @Override
    public void doUpdateProgress(int progress) {

    }
}
