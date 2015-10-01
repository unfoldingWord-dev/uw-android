package com.door43.tools.reporting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by joel on 1/14/2015.
 */
public class BugReporterActivity extends ActionBarActivity {
    private Button mOkButton;
    private Button mCancelButton;
    private ProgressDialog mDialog;
    private EditText mCrashReportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_reporter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final UploadReportsTask task = new UploadReportsTask();

        mOkButton = (Button)findViewById(R.id.okButton);
        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mCrashReportText = (EditText)findViewById(R.id.crashDescriptioneditText);
        mDialog = new ProgressDialog(BugReporterActivity.this);

        mCrashReportText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if(mCrashReportText.getText().toString().isEmpty()) {
//                    mOkButton.setBackgroundResource(R.color.gray);
//                } else {
//                    mOkButton.setBackgroundResource(R.color.blue);
//                }
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCrashReportText.getText().toString().length() > 0) {
                    showLoading();
                    task.execute(true);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showLoading() {
        // hide buttons
        mOkButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mCrashReportText.setVisibility(View.GONE);
    }

    private class UploadReportsTask extends AsyncTask<Boolean, String, Void> {

        @Override
        protected Void doInBackground(Boolean... bools) {
            Boolean upload = bools[0];
            String notes = mCrashReportText.getText().toString().trim();
            Handler handle = new Handler(getMainLooper());
            if(upload) {
                mDialog.setMessage(getResources().getString(R.string.uploading));
            } else {
                mDialog.setMessage(getResources().getString(R.string.loading));
            }
            // show the dialog
            handle.post(new Runnable() {
                @Override
                public void run() {
                    mDialog.show();
                }
            });

            File logFile = new File(getExternalCacheDir(), "log.txt");

            // TRICKY: make sure the github_oauth2 token has been set
            int githubTokenIdentifier = getApplicationContext().getResources().getIdentifier("github_oauth2", "string", getApplicationContext().getPackageName());
            String githubUrl = getApplicationContext().getResources().getString(R.string.github_bug_report_repo);

            if(upload && githubTokenIdentifier != 0) {
                GithubReporter reporter = new GithubReporter(getApplicationContext(), githubUrl, getString(githubTokenIdentifier));
                reporter.reportBug(notes, logFile);
                // empty the log
                try {
                    FileUtils.write(logFile, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.i(BugReporterActivity.class.getName(), "Submitted bug report");
            } else if(githubTokenIdentifier == 0) {
                Logger.w(BugReporterActivity.class.getName(), "the github oauth2 token is missing");
            }
            return null;
        }

        protected void onProgressUpdate(String... items) {
        }

        protected void onPostExecute(Void item) {
            Handler handle = new Handler(getMainLooper());
            handle.post(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                }
            });
            Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }
}