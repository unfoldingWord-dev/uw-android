package model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.unfoldingword.mobile.R;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.daoModels.Book;
import model.daoModels.Version;
import model.parsers.MediaType;
import utils.FileNameHelper;
import utils.FileUtil;

/**
 * Created by Fechner on 11/23/15.
 */
public class DataFileManager {

    private static final String TAG = "DataFileManager";

    private static final int FILES_PER_TEXT = 2;
    private static final int FILES_PER_AUDIO = 1;
    private static final int FILES_PER_VIDEO = 2;

    public static void saveDataForBook(Context context, Book book, byte[] data, MediaType type){
        saveDataForBook(context, book, data, type, book.getSourceUrl());
    }

    public static void saveDataForBook(Context context, Book book, byte[] data, MediaType type, String url){
        FileUtil.saveFile(getFileForDownload(context, type, book.getVersion(), FileNameHelper.getSaveFileNameFromUrl(url)), data);
    }

    public static void saveSignatureForBook(Context context, Book book, byte[] data, MediaType type){

        saveSignatureForBook(context, book, data, type, book.getSignatureUrl());
    }

    public static void saveSignatureForBook(Context context, Book book, byte[] data, MediaType type, String url){

        FileUtil.saveFile(getFileForDownload(context, type, book.getVersion(), FileNameHelper.getSaveFileNameFromUrl(url)), data);
    }

    public static DownloadState getStateOfContent(Context context, Version version, MediaType type){

        File mediaFolder = getFileForDownload(context, type, version);
        if(!mediaFolder.exists()){
            return DownloadState.DOWNLOAD_STATE_NONE;
        }
        else {
            return verifyStateForContent(version, type, mediaFolder);
        }
    }


    public static Uri getUri(Context context, Version version, MediaType type, String fileName){

        return Uri.fromFile(getFileForDownload(context, type, version, FileNameHelper.getSaveFileNameFromUrl(fileName)));
}

    public static int getDownloadedBitrate(Context context, Version version, MediaType type){

        File audioFolder = getFileForDownload(context, type, version);
        if(audioFolder.exists() && audioFolder.isDirectory()){
            File[]files = audioFolder.listFiles();
            for(File file : files){
                String fileName = file.getName();
                Pattern bitrateFinder = Pattern.compile("(\\d*)kbps");
                Matcher matcher = bitrateFinder.matcher(fileName);
                while (matcher.find()) {
                    String group = matcher.group(0);
                    String bitrate = group.substring(0, group.indexOf("k"));
                    if(isNumeric(bitrate)){
                        return Integer.parseInt(bitrate);
                    }
                }
            }
        }
        return -1;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            int d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static boolean deleteContentForBook(Context context, Version version, MediaType type){

        File desiredFolder = getFileForDownload(context, type, version);
        if(desiredFolder.exists()){
            if(deleteContents(desiredFolder)) {
                return desiredFolder.delete();
            }
        }
        return false;
    }

    public static boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    Log.d(TAG, "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }

    private static DownloadState verifyStateForContent(Version version, MediaType type, File folder){

        int expectedSize = getCountForMediaType(version, type);
        int numberOfFiles = folder.listFiles().length;
        if (expectedSize < 1) {
            return DownloadState.DOWNLOAD_STATE_NONE;
        }
        else if(expectedSize > numberOfFiles){
            return DownloadState.DOWNLOAD_STATE_DOWNLOADING;
        }
        else if (expectedSize == numberOfFiles) {
            return DownloadState.DOWNLOAD_STATE_DOWNLOADED;
        }
        else{
            return DownloadState.DOWNLOAD_STATE_ERROR;
        }
    }

    private static int getCountForMediaType(Version version, MediaType type){

        switch (type){
            case MEDIA_TYPE_TEXT:{
                return version.getBooks().size() * FILES_PER_TEXT;
            }
            case MEDIA_TYPE_AUDIO:{
                int finalCount = 0;
                for(Book book : version.getBooks()){
                    finalCount += book.getAudioBook().getAudioChapters().size();
                }
                return finalCount;
            }
            case MEDIA_TYPE_VIDEO:{
                return version.getBooks().size() * FILES_PER_VIDEO;
            }
            default: return -1;
        }
    }

    private static String getPath(Context context, MediaType mediaType, Version version){
        return context.getFilesDir() + "/" + version.getUniqueSlug() + "/" + mediaType.getPathForType();
    }

    private static File getFileForDownload(Context context, MediaType mediaType, Version version){
        return new File(getPath(context, mediaType, version));
    }

    private static File getFileForDownload(Context context, MediaType mediaType, Version version, String fileName){
        return new File(getPath(context, mediaType, version), fileName);
    }
}
