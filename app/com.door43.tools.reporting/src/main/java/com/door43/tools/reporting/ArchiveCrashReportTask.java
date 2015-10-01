package com.door43.tools.reporting;

import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This task archives the latest crash report
 */
public class ArchiveCrashReportTask extends ManagedTask {

    private int mMaxProgress = 1;

    private Context context;
    private String stacktraceDir;

    public ArchiveCrashReportTask(Context context, String stacktraceDir) {
        this.context = context;
        this.stacktraceDir = stacktraceDir;
    }

    @Override
    public void start() {
        File dir = new File(context.getExternalCacheDir(), stacktraceDir);
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return !new File(file, s).isDirectory();
            }
        });
        if (files.length > 0) {
            File archiveDir =  new File(dir, "archive");
            archiveDir.mkdirs();
            for(int i = 0; i < files.length; i ++) {
                File traceFile = new File(dir, files[i]);
                // archive stack trace for later use
                FileUtilities.moveOrCopy(traceFile, new File(archiveDir, files[i]));
                // clean up traces
                if(traceFile.exists()) {
                    traceFile.delete();
                }
            }
        }
    }

    @Override
    public int maxProgress() {
        return mMaxProgress;
    }
}
