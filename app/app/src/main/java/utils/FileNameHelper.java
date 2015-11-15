/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

/**
 * Created by Fechner on 8/5/15.
 */
public class FileNameHelper {

    public static String getSaveFileNameFromUrl(String url){

        return url.replace(":", "#").replace("/", "*");
    }
}
