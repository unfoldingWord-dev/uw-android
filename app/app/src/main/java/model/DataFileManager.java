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

        String filePath = getPath(type, book.getVersion()) + FileNameHelper.getSaveFileNameFromUrl(book.getSourceUrl());
        FileUtil.saveFile(new File(context.getFilesDir(), filePath), data);
    }

    private static String getPath(MediaType mediaType, Version version){
        return version.getUniqueSlug() + File.pathSeparator + mediaType.getPathForType() + File.pathSeparator;
    }

    public static DownloadState getStateOfContent(Context context, Version version, MediaType type){

        File mediaFolder = new File(context.getFilesDir(), getPath(type, version));
        if(!mediaFolder.exists()){
            return DownloadState.DOWNLOAD_STATE_NONE;
        }
        else {
            return verifyStateForContent(context, version, type, mediaFolder);
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
}
