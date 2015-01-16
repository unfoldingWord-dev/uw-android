package utils;

/**
 * Created by Acts Media Inc on 3/12/14.
 */
public class URLUtils {

    private static final String BASE_URL = "https://api.unfoldingword.org/obs/txt/1/";
    private static final String LANGUAGE_INFO_TAG = "obs-catalog";
    public static final String LANGUAGE_INFO = "https://api.unfoldingword.org/obs/txt/1/obs-catalog.json";

    // This url will be append language ex:
    public static final String CHAPTER_INFO = "https://api.unfoldingword.org/obs/txt/1/";

    public static final String CHAPTER_POST_LANGUAGE_TAG = "/obs-";
    public static final String JSON_TAG = ".json";


    // error strings

    public static final String ERROR = "error";


    public static final String TRUE = "true";
    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";
    public static final String BROAD_CAST_DOWN_ERROR = "org.unfoldingword.mobile.DOWNLOAD_WHILE_ERROR";

    public static String getUrlForLanguageUpdate(){
        String url = BASE_URL + LANGUAGE_INFO_TAG + JSON_TAG;
        return url;
    }

    public static String getUrlForBookUpdate(String language){
        String url = BASE_URL + language + CHAPTER_POST_LANGUAGE_TAG + language + JSON_TAG;
        return url;
    }

    public static String getLastBitFromUrl(String url) {
        String changedUrl = url.replaceFirst(".*/([^/?]+).*", "$1");
        return changedUrl;
    }

}
