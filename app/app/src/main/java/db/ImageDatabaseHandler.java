package db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import models.PageModel;

/**
 * Created by Fechner on 1/9/15.
 */
public class ImageDatabaseHandler {

    private static final String TAG = "ImageDatabaseHandler";

    public static boolean storeImage(Context currentContext, Bitmap imageData, String fileName) {
        //get path to external storage (SD card)

        if(fileName == null || imageData == null){
            return false;
        }
        Log.i(TAG, "Will Store Image: " + fileName);

        File prelimFile = new File(currentContext.getFilesDir(), fileName);

        try {

            File saveFile = new File(currentContext.getFilesDir(), fileName);
            saveFile.createNewFile();

            FileOutputStream fileOutputStream = currentContext.openFileOutput(fileName, Context.MODE_PRIVATE);

            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
    }

    public static Bitmap loadImageFrom(Context currentContext, String name)
    {
        Log.i(TAG, "trying to load image named: " + name);
        try {
            File saveFile = new File(currentContext.getFilesDir(), name);
            return BitmapFactory.decodeStream(new FileInputStream(saveFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean fileHasBeenSaved(Context currentContext, String fileName){

        File saveFile = new File(currentContext.getFilesDir(), fileName);

        if(saveFile.exists()){
            return true;
        }
        else{
            return false;
        }

    }
}
