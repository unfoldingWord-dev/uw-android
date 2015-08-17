package activity.sharing;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import org.unfoldingword.mobile.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import adapters.FilesListAdapter;
import sideloading.SideLoader;

public class FileFinderActivity extends UWBaseActivity {

    private Map<String, File> filesMap;

    private FilesListAdapter listAdapter;
    private ListView listView;

    private int numberLoading = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_finder);
        setupToolbar(false, getString(R.string.app_name), false);
        filesMap = new HashMap<String, File>();
        setupViews();
        searchFiles();
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }

    private void searchFiles(){

        recursiveSearch(Environment.getExternalStorageDirectory(), ".ufw");
        recursiveSearch(Environment.getRootDirectory(), ".ufw");

    }

    private UWBaseActivity getSelf(){
        return this;
    }

    private void setupViews(){
        listView = (ListView) findViewById(R.id.file_finding_list_view);
        listAdapter = new FilesListAdapter(getApplicationContext(), new ArrayList<File>(filesMap.values()));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new SideLoader(getSelf(), null).loadFile(listAdapter.getItem(position));
            }
        });
    }

    private void addNumberLoading(){
        numberLoading++;
    }

    private void removeNumberLoading(){
        numberLoading--;

        if(numberLoading < 1){
            findViewById(R.id.file_finding_progress_bar).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.file_finding_progress_bar).setVisibility(View.VISIBLE);
        }
    }

    private void updateList(){
        listAdapter.updateData(new ArrayList<File>(filesMap.values()));
    }

    private void addFile(final File file){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filesMap.put(file.getName(), file);
                updateList();
            }
        });
    }

    private void recursiveSearch(final File searchDirectory, final String searchString){

        addNumberLoading();
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                File[] files = searchDirectory.listFiles();
                if(files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            recursiveSearch(file, searchString);
                        }
                        if (file.getName().contains(searchString)) {
                            addFile(file);

                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                removeNumberLoading();
            }
        }.execute();
    }
}
