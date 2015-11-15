/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package application;

import android.app.Application;

import com.door43.tools.reporting.GlobalExceptionHandler;
import com.door43.tools.reporting.Logger;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.File;

/**
 * Created by Fechner on 7/22/15.
 */
public class UWApplication extends Application {

    public static final String STACKTRACE_DIR = "stacktrace";

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());

        File dir = new File(getExternalCacheDir(), STACKTRACE_DIR);
        GlobalExceptionHandler.register(dir);

        // configure logger
        int minLogLevel = 1;
        Logger.configure(new File(getExternalCacheDir(), "log.txt"), Logger.Level.getLevel(minLogLevel));
    }
}
