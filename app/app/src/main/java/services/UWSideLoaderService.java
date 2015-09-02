package services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.Book;
import model.daoModels.Language;
import model.daoModels.Project;
import model.daoModels.Version;
import model.parsers.LanguageParser;
import model.parsers.ProjectParser;
import model.parsers.VersionParser;
import peejweej.sideloading.utilities.FileUtilities;
import tasks.UpdateAndVerifyBookRunnable;
import utils.FileNameHelper;
import utils.FileUtil;
import utils.UWPreferenceDataManager;

/**
 * Created by PJ Fechner
 * Service for adding a version through SideLoading
 */
public class UWSideLoaderService extends UWUpdaterService {

    private static final String TAG = "UWPreLoader";
    public static final String SIDE_LOAD_TEXT_PARAM = "SIDE_LOAD_TEXT_PARAM";

    public static final String BROAD_CAST_SIDE_LOAD_SUCCESSFUL = "org.unfoldingword.mobile.BROAD_CAST_SIDE_LOAD_SUCCESSFUL";

    private String sideLoadText;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Uri fileUri = intent.getData();
        File file = new File(fileUri.getPath());
        byte[] decompressedBytes = FileUtilities.getDecompressedBytes(FileUtil.getbytesFromFile(file));

        if(decompressedBytes != null) {
            try {
                sideLoadText = new String(decompressedBytes, "UTF-8");
                addRunnable(new SideVersionsRunnable());
                return START_STICKY;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                stopService();

            }
        }
        return START_FLAG_RETRY;
    }

    @Override
    protected void stopService() {
        getApplicationContext().sendBroadcast(new Intent(BROAD_CAST_SIDE_LOAD_SUCCESSFUL));
        this.stopSelf();
    }

    class SideVersionsRunnable implements Runnable {

        @Override
        public void run() {

            loadVersion();
            runnableFinished();
        }

        private void loadVersion(){

            try {
                JSONObject jsonObject = new JSONObject(sideLoadText);
                List<Book> books = sideLoadProject(jsonObject.getJSONObject("top"));
                JSONObject sources = jsonObject.getJSONObject("sources");

                for(Book book : books) {

                    try {
                        String signature = sources.getString(book.getSignatureUrl());
                        byte[] text = sources.getString(book.getSourceUrl()).getBytes("UTF-8");
                        saveFile(text, book.getSourceUrl());
                        saveFile(signature.getBytes("UTF-8"), book.getSignatureUrl());

                        UpdateAndVerifyBookRunnable runnable = new UpdateAndVerifyBookRunnable(book, getThis(), text, signature);
                        addRunnable(runnable, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private List<Book> sideLoadProject(JSONObject json){

        Project model = new Project();
        model = (Project) model.setupModelFromJson(json);
        Project oldModel = Project.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));
        if(oldModel != null){
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }

        List<Book> books = new ArrayList<Book>();
        try {
            JSONArray versionJson = json.getJSONArray(ProjectParser.LANGUAGES_JSON_KEY);
            for(int i = 0; i < versionJson.length(); i++){
                JSONObject bookJson = versionJson.optJSONObject(i);
                books.addAll(sideLoadLanguage(bookJson, model));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return books;
    }

    private List<Book> sideLoadLanguage(JSONObject json, Project parent){

        Language model = new Language();
        model = (Language) model.setupModelFromJson(json, parent);
        Language oldModel = Language.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));
        if(oldModel != null){
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }

        List<Book> books = new ArrayList<Book>();
        try {
            JSONArray versionJson = json.getJSONArray(LanguageParser.VERSION_JSON_KEY);
            for(int i = 0; i < versionJson.length(); i++){
                JSONObject bookJson = versionJson.optJSONObject(i);
                books.addAll(sideLoadVersion(bookJson, model));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return books;
    }

    private List<Book> sideLoadVersion(JSONObject json, Language parent){

        Version model = new Version();
        model = (Version) model.setupModelFromJson(json, parent);
        model.setSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());

        Version oldModel = Version.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));

        if(oldModel != null){
            oldModel.updateWithModel(model);
            UWPreferenceDataManager.willDeleteVersion(getApplicationContext(), oldModel);
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }

        List<Book> books = new ArrayList<Book>();
        try {
            JSONArray booksJson = json.getJSONArray(VersionParser.BOOKS_JSON_KEY);
            for(int i = 0; i < booksJson.length(); i++){
                JSONObject bookJson = booksJson.optJSONObject(i);
                books.add(sideLoadBook(bookJson, model));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return books;
    }

    private Book sideLoadBook(JSONObject json, Version parent){

        Book model = new Book();
        model = (Book) model.setupModelFromJson(json, parent);

        Book oldModel = Book.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));

        if(oldModel != null){
            oldModel.updateWithModel(model);
            oldModel.deleteBookContent();
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }
        return model;
    }

    private void saveFile(byte[] bytes, String url){

        try{
            FileOutputStream fos = getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.close();
            Log.i(TAG, "USFM File Saved");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving USFM");
        }
    }
}
