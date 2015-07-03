package services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.Book;
import model.daoModels.Version;
import model.database.UWDataParser;
import model.datasource.LanguageLocaleDataSource;
import tasks.JsonDownloadTask;
import tasks.UpdateBookContentRunnable;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import utils.URLUtils;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWVersionDownloader extends UWUpdater {

    private static final String TAG = "UWVersionDownloader";

    public static final String VERSION_PARAM = "VERSION_PARAM";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getExtras() != null) {
            long versionId = intent.getExtras().getLong(VERSION_PARAM);
            Version version = Version.getVersionForId(versionId, DaoDBHelper.getDaoSession(getApplicationContext()));

            for (Book book : version.getBooks()) {
                addRunnable(new UpdateBookContentRunnable(book, this));
            }
            version.setSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());
            version.update();
        }

        return START_STICKY;
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            try {

                String jsonString = loadDbFile(getApplicationContext().getResources().getString(R.string.preloaded_catalog_file_name));

                JSONObject jsonObject = new JSONObject(jsonString);
                long modified = jsonObject.getLong(UWUpdater.MODIFIED_JSON_KEY);
                UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), modified);

                UWDataParser.getInstance(getApplicationContext()).updateProjects(jsonObject.getJSONArray(UWDataParser.PROJECTS_JSON_KEY), false);
                addRunnable(new UpdateProjectsRunnable(jsonObject.getJSONArray(UWUpdater.PROJECTS_JSON_KEY), getThis()));
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (JSONException e){
                e.printStackTrace();
            }


            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
//                        JSONObject jsonObj = new JSONObject(jsonString);
                        JSONArray locales = new JSONArray(jsonString);
                        addRunnable(new UpdateLanguageLocaleRunnable(locales, getThis()));

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }).execute(UWPreferenceManager.getLanguagesDownloadUrl(getApplicationContext()));

        }
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database (or overrides/deletes it if it exists) in the system folder, from where it can be accessed and
     * handled. This is done by transferring ByteStream.
     *
     * @throws IOException
     */
    private void copyDataBase() throws IOException {


    }

    private void copyLanguages() throws IOException {

        String jsonString = loadDbFile(getApplicationContext().getResources().getString(R.string.preloaded_locales_file_name));

        try {
            new LanguageLocaleDataSource(getApplicationContext()).fastLoadJson(jsonString);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String loadDbFile(String fileName) throws IOException{

        // Open your local model.db as the input stream
        InputStream inputStream = getApplicationContext().getAssets().open("preloaded_content/" + fileName);

        InputStreamReader reader = new InputStreamReader(inputStream);
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferReader = new BufferedReader(reader);
        String read = bufferReader.readLine();

        while(read != null) {
            //System.out.println(read);
            builder.append(read);
            read =bufferReader.readLine();
        }

        return builder.toString();
    }

}
