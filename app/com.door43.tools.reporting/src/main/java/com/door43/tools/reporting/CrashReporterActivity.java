package com.door43.tools.reporting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CrashReporterActivity extends ActionBarActivity implements ManagedTask.OnFinishedListener {

    public static final String STACKTRACE_DIR = "stacktrace";

    private Button mOkButton;
    private Button mCancelButton;
    private ProgressDialog mLoadingDialog;
    private EditText mCrashReportText;
    private static final String TASK_UPLOAD_CRASH_REPORT = "upload_crash_report";
    private static final String TASK_ARCHIVE_CRASH_REPORT = "archive_crash_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_reporter);

        mOkButton = (Button)findViewById(R.id.okButton);
        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mCrashReportText = (EditText)findViewById(R.id.crashDescriptioneditText);

        mLoadingDialog = new ProgressDialog(CrashReporterActivity.this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingDialog.setMessage(getResources().getString(R.string.uploading));
                mLoadingDialog.show();

                UploadCrashReportTask task = new UploadCrashReportTask(getApplicationContext(), mCrashReportText.getText().toString().trim());
                task.addOnFinishedListener(CrashReporterActivity.this);
                TaskManager.addTask(task, TASK_UPLOAD_CRASH_REPORT);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingDialog.setMessage(getResources().getString(R.string.loading));
                mLoadingDialog.show();

                ArchiveCrashReportTask task = new ArchiveCrashReportTask(getApplicationContext(), STACKTRACE_DIR);
                task.addOnFinishedListener(CrashReporterActivity.this);
                TaskManager.addTask(task, TASK_ARCHIVE_CRASH_REPORT);
            }
        });

        // connect to existing tasks
        UploadCrashReportTask uploadTask = (UploadCrashReportTask)TaskManager.getTask(TASK_UPLOAD_CRASH_REPORT);
        ArchiveCrashReportTask archiveTask = (ArchiveCrashReportTask)TaskManager.getTask(TASK_ARCHIVE_CRASH_REPORT);

        if(uploadTask != null) {
            mLoadingDialog.setMessage(getResources().getString(R.string.uploading));
            mLoadingDialog.show();
            uploadTask.addOnFinishedListener(this);
        } else if(archiveTask != null) {
            mLoadingDialog.setMessage(getResources().getString(R.string.loading));
            mLoadingDialog.show();
            archiveTask.addOnFinishedListener(this);
        }
    }

    @Override
    public void onFinished(ManagedTask task) {
        TaskManager.cancelTask(TaskManager.getTask(TASK_ARCHIVE_CRASH_REPORT));
        TaskManager.cancelTask(TaskManager.getTask(TASK_UPLOAD_CRASH_REPORT));

        Handler hand = new Handler(Looper.getMainLooper());
        hand.post(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            }
        });

        finish();
    }

    @Override
    public void onDestroy() {
        // disconnect listeners
        UploadCrashReportTask uploadTask = (UploadCrashReportTask)TaskManager.getTask(TASK_UPLOAD_CRASH_REPORT);
        if(uploadTask != null) {
            uploadTask.removeOnFinishedListener(this);
        }
        ArchiveCrashReportTask archiveTask = (ArchiveCrashReportTask)TaskManager.getTask(TASK_ARCHIVE_CRASH_REPORT);
        if(archiveTask != null) {
            archiveTask.removeOnFinishedListener(this);
        }
        super.onDestroy();
    }
}
