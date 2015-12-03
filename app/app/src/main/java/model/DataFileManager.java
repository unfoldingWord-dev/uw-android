package model;

import android.content.Context;

import org.unfoldingword.mobile.R;

import java.io.File;

import model.daoModels.Book;
import model.daoModels.Version;
import model.parsers.MediaType;
import utils.FileNameHelper;
import utils.FileUtil;

/**
 * Created by Fechner on 11/23/15.
 */
public class DataFileManager {

    private static final int FILES_PER_TEXT = 2;
    private static final int FILES_PER_AUDIO = 1;
    private static final int FILES_PER_VIDEO = 2;

    public static void saveDataForBook(Context context, Book book, byte[] data, MediaType type){

        String fileName =  FileNameHelper.getSaveFileNameFromUrl(book.getSourceUrl());
        FileUtil.saveFile(new File(context.getFilesDir() + getPath(type, book.getVersion()), fileName), data);
    }

    public static void saveSignatureForBook(Context context, Book book, byte[] data, MediaType type){

        String fileName =  FileNameHelper.getSaveFileNameFromUrl(book.getSignatureUrl());
        FileUtil.saveFile(new File(context.getFilesDir() + getPath(type, book.getVersion()), fileName), data);
    }

    public static DownloadState getStateOfContent(Context context, Version version, MediaType type){

        File mediaFolder = new File(context.getFilesDir() + getPath(type, version));
        if(!mediaFolder.exists()){
            return DownloadState.DOWNLOAD_STATE_NONE;
        }
        else {
            return verifyStateForContent(context, version, type, mediaFolder);
        }
    }

    public static boolean deleteContentForBook(Context context, Version version, MediaType type){

        File desiredFolder = new File(context.getFilesDir(), getPath(type, version));
        if(desiredFolder.exists()){
            return desiredFolder.delete();
        }
        else{
            return true;
        }
    }

    private static DownloadState verifyStateForContent(Context context, Version version, MediaType type, File folder){

        int expectedSize = getCountForMediaType(context, version, type);
        int numberOfFiles = folder.listFiles().length;
        if (expectedSize < 1) {
            return DownloadState.DOWNLOAD_STATE_NONE;
        }
        else if(expectedSize < numberOfFiles){
            return DownloadState.DOWNLOAD_STATE_DOWNLOADING;
        }
        else if (expectedSize == numberOfFiles) {
            return DownloadState.DOWNLOAD_STATE_DOWNLOADED;
        }
        else{
            return DownloadState.DOWNLOAD_STATE_ERROR;
        }
    }

    private static int getCountForMediaType(Context context, Version version, MediaType type){

        switch (type){
            case MEDIA_TYPE_TEXT:{
                return version.getBooks().size() * FILES_PER_TEXT;
            }
            case MEDIA_TYPE_AUDIO:{
                return version.getBooks().size() * FILES_PER_AUDIO;
            }
            case MEDIA_TYPE_VIDEO:{
                return version.getBooks().size() * FILES_PER_VIDEO;
            }
            default: return -1;
        }
    }

    private static String getPath(MediaType mediaType, Version version){
        String type = mediaType.getPathForType();
        return version.getUniqueSlug() + "/" + type;
    }
}
