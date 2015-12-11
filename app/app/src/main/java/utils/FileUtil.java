/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.unfoldingword.mobile.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

/**
 * Created by Fechner on 12/31/14.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    //region Out Methodsœ
    /**
     *
     * @param fileSequence
     * @param fileName
     * @param context
     */
    protected static void saveFileToApplicationFiles(Context context, CharSequence fileSequence, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(context.getFilesDir(), fileName);

            if (!file.exists()) {
                boolean success = file.createNewFile();
            }
            String fileString = fileSequence.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
//            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            outputStream.write(fileString.getBytes());
//            outputStream.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "saveFileToApplicationFiles FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "saveFileToApplicationFiles IOException: " + e.toString());
        }

        Log.i(TAG, "File saving was successful.");
    }

    public static void saveFileToSdCard(Context context, byte[] bytes, String fileName){
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + context.getString(R.string.app_name);
        saveFile(bytes, fileDir, fileName);
    }

    public static void saveFileToApplicationFiles(Context context, byte[] bytes, String fileName){
        String fileDir = context.getFilesDir().getAbsolutePath() + "/"
                + context.getString(R.string.app_name);
        saveFile(bytes, fileDir, fileName);
    }

    public static Uri saveFile(byte[] bytes, String dirName, String fileName){

        File dir = new File(dirName, fileName);
        return saveFile(dir, bytes);

//        try{
//            dir.mkdirs();
//            File file = new File(dirName, fileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(bytes);
//            fos.close();
//            Log.i(TAG, "USFM File Saved");
//            return Uri.fromFile(file);
//        }
//        catch (IOException e){
//            e.printStackTrace();
//            Log.e(TAG, "Error when saving file");
//            return null;
//        }
    }

    public static Uri saveFile(File file, byte[] bytes){

        Log.d(TAG, "Saving file: " + file.getPath());
        file.getParentFile().mkdirs();

        try{
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
//            Log.i(TAG, "File Saved");
            return Uri.fromFile(file);
        }
        catch (IOException e){
            e.printStackTrace();
//            Log.e(TAG, "Error when saving file");
            return null;

        }
    }

    public static void saveFile(CharSequence fileSequence, String dirName, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(dirName, fileName);

            if (!file.exists()) {
                file.createNewFile();
            }
            String fileString = fileSequence.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "saveFileToApplicationFiles FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "saveFileToApplicationFiles IOException: " + e.toString());
        }

        Log.i(TAG, "File saving was successful.");
    }

    /**
     *
     * @param fileSequence
     * @param fileName
     * @param context
     */
    public static void saveFileToSDCard(Context context, CharSequence fileSequence, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name), fileName);

            if (!file.exists()) {
                boolean madeDirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name)).mkdirs();
                boolean madeFile = file.createNewFile();
            }
            String fileString = fileSequence.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();

//            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            outputStream.write(fileString.getBytes());
//            outputStream.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "saveFileToApplicationFiles FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "saveFileToApplicationFiles IOException: " + e.toString());
        }

        Log.i(TAG, "File saving was successful.");
    }

    public static Uri createTemporaryFile(Context context, byte[] bytes, String fileName){

        clearTemporaryFiles(context);
        String directory = context.getFilesDir()
                + "/" + context.getString(R.string.app_name) + "/temp";

        return saveFile(bytes, directory, fileName);
    }

    public static Uri createTemporaryFile(Context context, byte[] bytes, String folderName, String fileName){

        String directory = context.getFilesDir()
                + "/" + context.getString(R.string.app_name) + "/temp/" + folderName;

        return saveFile(bytes, directory, fileName);
    }

    public static Uri getUriForTempDir(Context context, String folderName){

        String directory = context.getFilesDir()
                + "/" + context.getString(R.string.app_name) + "/temp/" + folderName;
        return Uri.fromFile(new File(directory));
    }

    public static void copyFile(Uri originalDir, Uri newDir){

        try {
            File source = new File(originalDir.getPath());
            FileChannel src = new FileInputStream(source).getChannel();
            FileChannel dst = new FileOutputStream(new File(newDir.getPath())).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Uri createTemporaryFile(Context context, CharSequence fileSequence, String fileName){

        clearTemporaryFiles(context);
//        Log.i(TAG, "Attempting to save temporary file named:" + fileName);

        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + context.getString(R.string.app_name) + "/temp", fileName);

            if (!file.exists()) {
                boolean madeDirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + context.getString(R.string.app_name) + "/temp").mkdirs();
                boolean madeFile = file.createNewFile();
            }
            String fileString = fileSequence.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
//            Log.i(TAG, "createTemporaryFile saving was successful.");
            return Uri.fromFile(file);

//            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            outputStream.write(fileString.getBytes());
//            outputStream.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "createTemporaryFile FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "createTemporaryFile IOException: " + e.toString());
        }
        Log.i(TAG, "createTemporaryFile saving was unsuccessful.");
        return null;
    }

    public static void clearTemporaryFiles(Context context){

        File file = new File(context.getFilesDir().getAbsolutePath()
                + "/" + context.getString(R.string.app_name) + "/temp");
        if(file.exists()){
            deleteContents(file);
            file.delete();
        }
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

    //endregion


    //region In Method


    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    protected static String getJSONStringFromApplicationFiles(Context context, String fileName){

        if(context == null || fileName == null){
            return null;
        }
        try{

            InputStream fileStream = context.openFileInput(fileName);

            String resultString =  getStringFromInputStream(fileStream, fileName).toString();
            return resultString;
        }
        catch (IOException e){
            Log.e(TAG, "getJSONStringFromApplicationFiles IOException: " + e.toString());
            return null;
        }
    }

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    protected static String getJSONStringFromAssets(Context context, String fileName){

        try{
            InputStream fileStream = context.getAssets().open(fileName);

            String resultString = getStringFromInputStream(fileStream, fileName).toString();
            return resultString;
        }
        catch (IOException e){
            Log.e(TAG, "getJSONStringFromAssets IOException: " + e.toString());
            return null;
        }
    }

    public static String getStringFromFile(File file){

        byte[] bytes = getBytesFromFile(file);
        if(bytes != null) {
            return new String(bytes);
        }
        else{
            return null;
        }
    }

    public static byte[] getBytesFromFile(File file){

        try{
            FileInputStream fileStream = new FileInputStream(file);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byte[] bytes = bos.toByteArray();
            return bytes;
        }
        catch (IOException e){
            Log.e(TAG, "getBytesFromFile IOException: " + e.toString());
            return null;
        }
    }

    /**
     *
     * @param fileStream
     * @param fileName
     * @return The String from the Stream or null if there's an error.
     */
    private static CharSequence getStringFromInputStream(InputStream fileStream, String fileName){

        String resultString = "";

        try{

            BufferedReader in = new BufferedReader(new InputStreamReader(fileStream, "utf-8"));
            String str;

            while ((str = in.readLine()) != null) {
                resultString += str;
            }

            in.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "getStringFromInputStream file name: " + fileName + " FileNotFoundException: " + e.toString());
            return null;
        }
        catch (IOException e){
            Log.e(TAG, "getStringFromInputStream file name: " + fileName + " IOException: " + e.toString());
            return null;
        }

        return resultString;
    }


    protected static void deleteFile(Context context, String fileName){

        context.deleteFile(fileName);
    }


    //endregion
}
