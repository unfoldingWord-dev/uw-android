package utils;

/**
 * Created by Fechner on 8/5/15.
 */
public class FileNameHelper {

    public static String getSaveFileNameFromUrl(String url){

        return url.replace(":", "#").replace("/", "*");
    }
}
