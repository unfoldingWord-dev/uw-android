package tasks;

import android.os.AsyncTask;

import java.io.IOException;

import utils.URLDownloadUtil;

/**
 * Created by Fechner on 6/17/15.
 */
public class BytesDownloadTask extends AsyncTask<String,Void, byte[]> {

    private DownloadTaskListener listener;

    public BytesDownloadTask(DownloadTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected byte[] doInBackground(String... params) {

        String url = params[0];
        try {
            byte[] json = URLDownloadUtil.downloadBytes(url);
            return json;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
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
