package utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by Fechner on 7/12/15.
 */
public class Zipper {

    private static final String TAG = "Zipper";

    public static String encodeToBase64ZippedString(String text){

        try {
            byte[] inputBytes = text.getBytes("UTF-8");
            byte[] outputBytes = new byte[inputBytes.length];

            Deflater compressor = new Deflater();
            compressor.setInput(inputBytes);
            compressor.finish();
            int compressedDataLength = compressor.deflate(outputBytes);
            compressor.end();

            String encodedText = Base64.encodeToString(outputBytes, 0, compressedDataLength, Base64.DEFAULT);
            return encodedText;
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    public byte[] compressText(String text){

        try {
            byte[] inputBytes = text.getBytes("UTF-8");
            byte[] outputBytes = new byte[inputBytes.length];

            Deflater compressor = new Deflater();
            compressor.setInput(inputBytes);
            compressor.finish();
            int compressedDataLength = compressor.deflate(outputBytes);
            compressor.end();

            byte[] finalBytes = Arrays.copyOf(outputBytes, compressedDataLength);

            return finalBytes;
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

    private static byte[] getDecompressedBytes(byte[] compressedBytes){

        try {
            Inflater decompressor = new Inflater();
            decompressor.setInput(compressedBytes);

            byte[] result = new byte[0];
            byte[] buffer = new byte[8192];

            while (!decompressor.finished()) {
                int byteCount = decompressor.inflate(buffer, 0, buffer.length);
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
}
