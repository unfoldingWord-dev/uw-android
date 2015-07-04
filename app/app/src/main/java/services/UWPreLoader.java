package services;

import android.content.Intent;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import model.DaoDBHelper;
import model.DownloadState;
import model.daoModels.Book;
import model.daoModels.Version;
import model.database.UWDataParser;
import signing.UWSigning;
import tasks.UpdateBibleChaptersRunnable;
import tasks.UpdateBookContentRunnable;
import tasks.UpdateLanguageLocaleRunnable;
import tasks.UpdateProjectsRunnable;
import tasks.UpdateStoriesChaptersRunnable;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UWPreLoader extends UWUpdater {

    private static final String TAG = "UWPreLoader";

    public static final String BROAD_CAST_PRELOAD_SUCCESSFUL = "org.unfoldingword.mobile.PRELOAD_SUCCESSFUL";

    private boolean hasUpdatedVersions = false;

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

        addRunnable(new PreloadVersionsRunnable());

        return START_STICKY;
    }

    @Override
    protected void stopService() {
        // This is to cause the book text to only be added after all other content
        if(!hasUpdatedVersions) {
            addRunnable(new PreloadTextRunnable());
            hasUpdatedVersions = true;
        }
        else {
            getApplicationContext().sendBroadcast(new Intent(BROAD_CAST_PRELOAD_SUCCESSFUL));
            this.stopSelf();
        }
    }

    class PreloadVersionsRunnable implements Runnable {

        @Override
        public void run() {

            updateVersions();
            updateLocales();
            runnableFinished();
        }

        private void updateVersions(){

            try {
                String jsonString = loadDbFile(getApplicationContext().getResources().getString(R.string.preloaded_catalog_file_name));

                JSONObject jsonObject = new JSONObject(jsonString);
                long modified = jsonObject.getLong(UWUpdater.MODIFIED_JSON_KEY);
                UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), modified);

                UWDataParser.getInstance(getApplicationContext()).updateProjects(jsonObject.getJSONArray(UWDataParser.PROJECTS_JSON_KEY), false);
                addRunnable(new UpdateProjectsRunnable(jsonObject.getJSONArray(UWUpdater.PROJECTS_JSON_KEY), getThis()));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        private void updateLocales(){

            try {
                String jsonString = loadDbFile(getApplicationContext().getResources().getString(R.string.preloaded_locales_file_name));
                JSONArray locales = new JSONArray(jsonString);
                UWPreferenceManager.setHasDownloadedLocales(getApplicationContext(), true);
                addRunnable(new UpdateLanguageLocaleRunnable(locales, getThis()));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class PreloadTextRunnable implements Runnable{

        @Override
        public void run() {

            List<Book> books = DaoDBHelper.getDaoSession(getApplicationContext())
                    .getBookDao().queryBuilder().list();

            for(Book book : books){

                try {
                    String signature = loadDbFile(getSavedUrl(book.getSignatureUrl()));
                    byte[] text = loadDbFile(getSavedUrl(book.getSourceUrl())).getBytes();

                    UWSigning.updateVerification(getApplicationContext(), book, text, signature);

                    if(book.getSourceUrl().contains("usfm")){
                        addRunnable(new UpdateBibleChaptersRunnable(text, getThis(), book));
                    }
                    else{
                        UpdateStoriesChaptersRunnable runnable = new UpdateStoriesChaptersRunnable(
                                new JSONObject(new String(text)).getJSONArray(UpdateBookContentRunnable.CHAPTERS_JSON_KEY), getThis(), book);
                        addRunnable(runnable);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            List<Version> versions = DaoDBHelper.getDaoSession(getApplicationContext())
                    .getVersionDao().queryBuilder().list();

            for(Version version : versions){
                version.setSaveState(DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());
                version.update();
            }

            runnableFinished();
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

    private static String getSavedUrl(String url){

        return url.replace(":", "#").replace("/", "*");
    }
}
