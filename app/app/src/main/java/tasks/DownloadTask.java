package tasks;

import android.content.Intent;
import android.os.AsyncTask;

import java.io.IOException;

import utils.URLDownloadUtil;

/**
 * Created by Fechner on 6/17/15.
 */
public class DownloadTask extends AsyncTask<String,Void, String> {

    private DownloadTaskListener listener;

    public DownloadTask(DownloadTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];
        try {
            String json = URLDownloadUtil.downloadString(url);
            return json;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
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
