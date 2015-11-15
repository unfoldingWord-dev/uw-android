/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package tasks;

import android.os.AsyncTask;

import utils.URLDownloadUtil;

/**
 * Created by PJ Fechner on 6/17/15.
 * AsyncTask for downloading JSON
 */
public class JsonDownloadTask extends AsyncTask<String,Void, String> {

    private DownloadTaskListener listener;

    public JsonDownloadTask(DownloadTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];
        return URLDownloadUtil.downloadString(url);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.downloadFinishedWithJson(s);
    }

    public interface DownloadTaskListener{

        void downloadFinishedWithJson(String jsonString);
    }
}
