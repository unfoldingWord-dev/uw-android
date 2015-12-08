/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import model.daoModels.AudioChapter;

/**
 * Created by Fechner on 8/5/15.
 */
public class FileNameHelper {

    public static String getSaveFileNameFromUrl(String url){

        return url.replace(":", "#").replace("/", "*");
    }

    private static final String AUDIO_FILE_SEPARATOR = "|~|";
    private static final String AUDIO_FILE_PREFIX = "audio_";
    private static final String AUDIO_FILE_FILE_TYPE = ".mp3";

    public static String getShareAudioFileName(AudioChapter audioChapter, int bitRate){

        String versionSlug = audioChapter.getAudioBook().getBook().getVersion().getSlug();
        String language = audioChapter.getAudioBook().getBook().getSlug();
        String chapter = Integer.toString(audioChapter.getChapter());
        String bookSlug = audioChapter.getAudioBook().getBook().getSlug();

        return AUDIO_FILE_PREFIX + versionSlug + AUDIO_FILE_SEPARATOR + language + AUDIO_FILE_SEPARATOR
                + chapter + AUDIO_FILE_SEPARATOR + bookSlug + AUDIO_FILE_SEPARATOR + bitRate + AUDIO_FILE_FILE_TYPE;
    }
}
