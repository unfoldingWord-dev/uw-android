/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package com.door43.tools.reporting;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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

/**
 * Created by Fechner on 12/31/14.
 */
public class FileUtil {

    private static final String TAG = "FileLoader";

    //region Out Methods
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

        file.mkdirs();
        try{
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
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + context.getString(R.string.app_name) + "/temp";

        return saveFile(bytes, directory, fileName);
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

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + context.getString(R.string.app_name) + "/temp");
        if(file.exists()){
            final File to = new File(file.getAbsolutePath());
            boolean success = file.renameTo(to);
            success = file.delete();
        }
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
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
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
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    public static String getStringFromFile(File file){

        try{
            FileInputStream fileStream = new FileInputStream(file);

            String resultString = getStringFromInputStream(fileStream, file.getName()).toString();
            return resultString;
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
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
