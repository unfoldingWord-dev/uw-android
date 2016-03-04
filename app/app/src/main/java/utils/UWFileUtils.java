/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Fechner on 9/25/15.
 */
public class UWFileUtils {

    private static final String TAG = "UWFileUtils";

    public static boolean deleteSource(String url, Context context){

        return context.deleteFile(FileNameHelper.getSaveFileNameFromUrl(url));
    }

    /**
     * @param url URL of the desired source
     * @param context Context ot use
     * @return The desired Source file as a String.
     */
    public static String loadSource(String url, Context context){

        try{
            FileInputStream fos = context.openFileInput(FileNameHelper.getSaveFileNameFromUrl(url));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = fos.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byte[] bytes = bos.toByteArray();

            return new String(bytes, "UTF-8");
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving USFM");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving USFM");
        }
        return null;
    }

    public static File loadSourceFile(String url, Context context){

        return context.getFileStreamPath(FileNameHelper.getSaveFileNameFromUrl(url));
    }
}
