package model.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

            File saveFile = new File(currentContext.getPackageResourcePath(), fileName);
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
}
