package peejweej.sideloading.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import peejweej.sideloading.R;

/**
 * Created by PJ Fechner on 8/24/15.
 */
public class FileUtilities {

    private static final String TAG = "FileUtilities";

    public static String encodeToBase64ZippedString(String text){

        try {
            byte[] inputBytes = text.getBytes("UTF-8");
            byte[] outputBytes = new byte[inputBytes.length];

            Deflater compressor = new Deflater();
            compressor.setInput(inputBytes);
            compressor.finish();
            int compressedDataLength = compressor.deflate(outputBytes);
            compressor.end();

            return Base64.encodeToString(outputBytes, 0, compressedDataLength, Base64.DEFAULT);
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] compressText(String text){

        try {
            byte[] inputBytes = text.getBytes("UTF-8");
            byte[] outputBytes = new byte[inputBytes.length];

            Deflater compressor = new Deflater();
            compressor.setInput(inputBytes);
            compressor.finish();
            int compressedDataLength = compressor.deflate(outputBytes);
            compressor.end();

            return Arrays.copyOf(outputBytes, compressedDataLength);
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeFromBase64EncodedString(String encodedText){

        try {
            byte[] zipBytes = Base64.decode(encodedText, Base64.DEFAULT);
            byte[] result = getDecompressedBytes(zipBytes);

            if (result != null) {
                String outputString = new String(result, "UTF-8");
                outputString = outputString.trim();
                return outputString;
            }
            else{
                Log.e(TAG, "Error unzipping string");
                return null;
            }

        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }

    }

    public static byte[] getDecompressedBytes(byte[] compressedBytes){

        try {
            Inflater decompressor = new Inflater();
            decompressor.setInput(compressedBytes);

            byte[] result = new byte[0];
            byte[] buffer = new byte[8192];

            while (!decompressor.finished()) {
                decompressor.inflate(buffer, 0, buffer.length);
                byte[] currentResult = new byte[result.length + buffer.length];

                // adds current bytes to currentResult
                System.arraycopy(result, 0, currentResult, 0, result.length);
                // copies buffer content to currentResult
                System.arraycopy(buffer, 0, currentResult, result.length, buffer.length);

                // makes result current
                result = currentResult;
            }
            decompressor.end();
            return result;
        }
        catch (DataFormatException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Uri createTemporaryFile(Context context, byte[] bytes, String fileName){

        clearTemporaryFiles(context);
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + context.getString(R.string.app_name) + "/temp";

        return saveFile(bytes, directory, fileName);
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

    public static Uri saveFile(byte[] bytes, String dirName, String fileName){

        try{
            File dir = new File(dirName);
            dir.mkdirs();
            File file = new File(dirName, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            Log.i(TAG, "File Saved");
            return Uri.fromFile(file);
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving file");
            return null;
        }
    }

    public static void saveFileToSdCard(Context context, byte[] bytes, String fileName){
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + context.getString(R.string.app_name);
        saveFile(bytes, fileDir, fileName);
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
            return bos.toByteArray();
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }
}
