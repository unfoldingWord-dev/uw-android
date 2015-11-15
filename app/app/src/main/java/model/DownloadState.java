/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

/**
 * Created by Fechner on 6/30/15.
 */
public enum DownloadState {

    DOWNLOAD_STATE_ERROR(0),
    DOWNLOAD_STATE_NONE(1),
    DOWNLOAD_STATE_DOWNLOADING(2),
    DOWNLOAD_STATE_DOWNLOADED(3);

    DownloadState(int i) {
    }

    public static DownloadState createState(int value) {

        switch (value) {
            case 1:{
                return DOWNLOAD_STATE_NONE;
            }
            case 2:{
                return DOWNLOAD_STATE_DOWNLOADING;
            }
            case 3:{
                return DOWNLOAD_STATE_DOWNLOADED;
            }
            default:{
                return DOWNLOAD_STATE_ERROR;
            }
        }
    }
}
