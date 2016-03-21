/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.DataFileManager;
import model.daoModels.AudioChapter;
import model.daoModels.Book;
import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * Created by Fechner on 8/5/15.
 */
public class FileNameHelper {

    public static String getSaveFileNameFromUrl(String url){

        return url.replace(":", "#").replace("/", "*");
    }

    public static String getSaveFileName(Context context, Book book, MediaType type, String url){

        return book.getUniqueSlug() + FILE_SEPARATOR + getFileEnd(context, book, type, getFileExtension(url, type));
    }

    private static String getFileEnd(Context context, Book book, MediaType type, String extension){
        switch (type) {
            case MEDIA_TYPE_TEXT: {
                return extension;
            }
            case MEDIA_TYPE_AUDIO:{
                return extension;
            }
            default:{
                return "";
            }
        }
    }

    private static String getFileExtension(String url, MediaType type){

        if(type == MediaType.MEDIA_TYPE_AUDIO){
            return url.substring(url.lastIndexOf("/") + 1);
        }
        else {
            return url.substring(url.lastIndexOf("."));
        }
    }

    private static final String FILE_SEPARATOR = "|~|";

    private static final String TEXT_FILE_PREFIX = "text_";
    private static final String TEXT_FILE_FILE_TYPE = ".json";
    public static final String AUDIO_FILE_PREFIX = "audio_";
    public static final String VIDEO_FILE_PREFIX = "video_";
    public static final String AUDIO_FILE_FILE_TYPE = ".mp3";
    public static final String SIGNATURE_FILE_TYPE = ".sig";


    public static String getShareAudioFileName(AudioChapter audioChapter, int bitRate){

        String versionSlug = audioChapter.getAudioBook().getBook().getVersion().getSlug();
        String language = audioChapter.getAudioBook().getBook().getVersion().getLanguage().getSlug();
        String chapter = Integer.toString(audioChapter.getChapter());
        String bookSlug = audioChapter.getAudioBook().getBook().getSlug();

        return AUDIO_FILE_PREFIX + versionSlug + FILE_SEPARATOR + language + FILE_SEPARATOR
                + chapter + FILE_SEPARATOR + bookSlug + FILE_SEPARATOR + bitRate + AUDIO_FILE_FILE_TYPE;
    }

    public static String getShareAudioSignatureFileName(AudioChapter audioChapter, int bitRate){

        String versionSlug = audioChapter.getAudioBook().getBook().getVersion().getSlug();
        String language = audioChapter.getAudioBook().getBook().getVersion().getLanguage().getSlug();
        String chapter = Integer.toString(audioChapter.getChapter());
        String bookSlug = audioChapter.getAudioBook().getBook().getSlug();

        return AUDIO_FILE_PREFIX + versionSlug + FILE_SEPARATOR + language + FILE_SEPARATOR
                + chapter + FILE_SEPARATOR + bookSlug + FILE_SEPARATOR + bitRate + SIGNATURE_FILE_TYPE;
    }

    public static String getShareTextFileName(Version version){

        String versionSlug = version.getSlug();
        String language = version.getLanguage().getSlug();

        return TEXT_FILE_PREFIX + versionSlug + FILE_SEPARATOR + language + TEXT_FILE_FILE_TYPE;
    }

    public static int getBitrateFromFileName(String fileName){

        Pattern bitrateFinder = Pattern.compile("\\|~\\|\\d*\\.");
        Matcher matcher = bitrateFinder.matcher(fileName);
        while (matcher.find()) {
            String group = matcher.group(0);
            String bitrate = group.substring(3, group.indexOf("."));
            if(DataFileManager.isNumeric(bitrate)){
                return Integer.parseInt(bitrate);
            }
        }
        return -1;
    }

    public static String getLastBitFromUrl(String url) {
        String changedUrl = url.replaceFirst(".*/([^/?]+).*", "$1");
        return changedUrl;
    }
}
