package com.door43.tools.reporting;

import android.content.Context;

import com.door43.tools.reporting.GithubReporter;
import com.door43.tools.reporting.GlobalExceptionHandler;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * This task submits the latest crash report to github
 */
public class UploadCrashReportTask extends ManagedTask {

    private static final String STACKTRACE_DIR = "stacktrace";
    private final String mMessage;
    private int mMaxProgress = 100;
    private Context context;

    public UploadCrashReportTask(Context context, String message) {
        mMessage = message;
        this.context = context;
    }

    @Override
    public void start() {
        File stacktraceDir = new File(context.getExternalCacheDir(), STACKTRACE_DIR);
        File logFile = new File(context.getExternalCacheDir(), "log.txt");
        String githubToken = context.getString(R.string.github_oauth2);
        String githubUrl = context.getResources().getString(R.string.github_bug_report_repo);

        // TRICKY: make sure the github_oauth2 token has been set
        if(!githubToken.equalsIgnoreCase("STRING_REPLACED_BY_BUILD_SERVER")) {
            GithubReporter reporter = new GithubReporter(context, githubUrl, githubToken);
            String[] stacktraces = GlobalExceptionHandler.getStacktraces(stacktraceDir);
            if (stacktraces.length > 0) {
                // upload most recent stacktrace
                reporter.reportCrash(mMessage, new File(stacktraces[0]), logFile);
                // empty the log
                try {
                    FileUtils.write(logFile, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // archive extra stacktraces
                File archiveDir = new File(stacktraceDir, "archive");
                archiveDir.mkdirs();
                for (String filePath:stacktraces) {
                    File traceFile = new File(filePath);
                    if (traceFile.exists()) {
                        FileUtilities.moveOrCopy(traceFile, new File(archiveDir, traceFile.getName()));
                        if(traceFile.exists()) {
                            traceFile.delete();
                        }
                    }
                }
            }
        }
    }

    @Override
    public int maxProgress() {
        return mMaxProgress;
    }
}
