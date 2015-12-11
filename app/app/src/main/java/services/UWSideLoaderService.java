/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.github.peejweej.androidsideloading.utilities.FileUtilities;

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
import model.DataFileManager;
import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.Book;
import model.daoModels.Language;
import model.daoModels.Project;
import model.daoModels.Version;
import model.parsers.AudioBookParser;
import model.parsers.BookParser;
import model.parsers.LanguageParser;
import model.parsers.MediaType;
import model.parsers.ProjectParser;
import model.parsers.VersionParser;
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
    private Uri filesDir;

    private boolean isLoadingAudio = false;
    private boolean isLoadingVideo = false;
    private int bitrate = -1;

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
        filesDir = DataFileManager.uncompressSideLoadedFiles(getApplicationContext(), new File(fileUri.getPath()));
        startWithFilesDir(filesDir);
        return START_STICKY;
    }

    private void startWithFilesDir(Uri dir){

        File textFile = null;
        File directory = new File(dir.getPath());
        for(File file : directory.listFiles()){

            String fileName = file.getName();
            if(fileName.contains("json")){
                textFile = file;
            }
            else if(fileName.contains(FileNameHelper.AUDIO_FILE_PREFIX)){
                isLoadingAudio = true;
                bitrate = FileNameHelper.getBitrateFromFileName(file.getName());
            }
            else if(fileName.contains(FileNameHelper.VIDEO_FILE_PREFIX)){
                isLoadingVideo = true;
            }
        }
        if(textFile != null){
            loadVersion(textFile);
        }
    }

    private void loadVersion(File versionFile){

        byte[] decompressedBytes = FileUtilities.getBytesFromFile(versionFile);

        if(decompressedBytes != null) {
            try {
                sideLoadText = new String(decompressedBytes, "UTF-8");
                addRunnable(new SideVersionsRunnable());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                stopService();

            }
        }
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
                        DataFileManager.saveDataForBook(getApplicationContext(), book, text, MediaType.MEDIA_TYPE_TEXT, book.getSourceUrl());
                        DataFileManager.saveSignatureForBook(getApplicationContext(), book, signature.getBytes(), MediaType.MEDIA_TYPE_TEXT, book.getSignatureUrl());

//                        saveFile(text, book.getSourceUrl());
//                        saveFile(signature.getBytes("UTF-8"), book.getSignatureUrl());

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
                books.add(sideLoadBook(getApplicationContext(), bookJson, model));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return books;
    }

    private Book sideLoadBook(Context context, JSONObject json, Version parent){

        Book model = new Book();
        model = (Book) model.setupModelFromJson(json, parent);

        Book oldModel = Book.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));

        if(oldModel != null){
            oldModel.updateWithModel(model);
            oldModel.deleteBookContent(context);
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }

        try {
            JSONObject mediaJson = json.getJSONObject(BookParser.MEDIA_JSON_KEY);
            JSONObject audioJson = mediaJson.getJSONObject(BookParser.AUDIO_JSON_KEY);
            sideLoadAudioBook(audioJson, model);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return model;
    }

    private AudioBook sideLoadAudioBook(JSONObject json, Book parent){

        AudioBook model = new AudioBook();
        model = (AudioBook) model.setupModelFromJson(json, parent);

        AudioBook oldModel = AudioBook.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));

        if(oldModel != null){
            oldModel.updateWithModel(model);
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }

        try {
            JSONArray chapters = json.getJSONArray(AudioBookParser.SOURCE_LIST_JSON_KEY);
            for(int i = 0; i < chapters.length(); i++){
                JSONObject bookJson = chapters.optJSONObject(i);
                sideLoadAudioChapter(bookJson, model);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return model;
    }

    private AudioChapter sideLoadAudioChapter(JSONObject json, AudioBook parent){

        AudioChapter model = new AudioChapter();
        model = (AudioChapter) model.setupModelFromJson(json, parent);

        AudioChapter oldModel = AudioChapter.getModelForUniqueSlug(model.getUniqueSlug(),
                DaoDBHelper.getDaoSession(getApplicationContext()));

        if(oldModel != null){
            oldModel.updateWithModel(model);
            model = oldModel;
        }
        else {
            model.insertModel(DaoDBHelper.getDaoSession(getApplicationContext()));
        }
        if(isLoadingAudio)
            saveAudioFiles(model);
        return model;
    }

    private void saveAudioFiles(AudioChapter audioChapter){

        File sourceFile = new File(filesDir.getPath(), FileNameHelper.getShareAudioFileName(audioChapter, bitrate));
//        File signatureFile = new File(filesDir.getPath(), FileNameHelper.getShareAudioSignatureFileName(audioChapter, bitrate));

        if(sourceFile.exists()) {
            DataFileManager.saveDataForBook(getApplicationContext(), audioChapter.getAudioBook().getBook(),
                    FileUtil.getBytesFromFile(sourceFile), MediaType.MEDIA_TYPE_AUDIO, audioChapter.getAudioUrl(bitrate));

//            DataFileManager.saveSignatureForBook(getApplicationContext(), audioChapter.getAudioBook().getBook(),
//                    FileUtil.getBytesFromFile(signatureFile), MediaType.MEDIA_TYPE_AUDIO, audioChapter.getSignatureUrl(bitrate));
        }
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
