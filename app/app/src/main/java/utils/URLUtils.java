package utils;

/**
 * Created by Acts Media Inc on 3/12/14.
 */
public interface URLUtils {

    String LANGUAGE_INFO = "https://api.unfoldingword.org/obs/txt/1/obs-catalog.json";

    // This url will be append language ex:
    String CHAPTER_INFO = "https://api.unfoldingword.org/obs/txt/1/";


    // error strings

    String ERROR = "error";


    String TRUE = "true";
    String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";
    String BROAD_CAST_DOWN_ERROR = "org.unfoldingword.mobile.DOWNLOAD_WHILE_ERROR";
}
