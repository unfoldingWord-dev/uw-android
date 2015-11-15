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
 * AsyncTask for downloading bytes
 */
public class BytesDownloadTask extends AsyncTask<String,Void, byte[]> {

    private DownloadTaskListener listener;

    public BytesDownloadTask(DownloadTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected byte[] doInBackground(String... params) {

        String url = params[0];
        byte[] json = URLDownloadUtil.downloadBytes(url);
        return json;
    }

    @Override
    protected void onPostExecute(byte[] s) {
        super.onPostExecute(s);
        listener.downloadFinishedWithJson(s);
    }

    public interface DownloadTaskListener{

        void downloadFinishedWithJson(byte[] data);
    }
}
