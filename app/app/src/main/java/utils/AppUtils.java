package utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Acts Media on 15/12/14.
 */
public interface AppUtils {
    String DIR_NAME = Environment.getExternalStorageDirectory().getPath() + File.separator + "unfoldingWord" + File.separator;
}
