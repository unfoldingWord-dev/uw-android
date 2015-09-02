package peejweej.sideloading.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import peejweej.sideloading.R;
import peejweej.sideloading.adapters.FilesListAdapter;
import peejweej.sideloading.model.SideLoadInformation;

public class FileFinderActivity extends BaseActivity {


    public static final String LOAD_INFO_PARAM = "LOAD_INFO_PARAM";

    private SideLoadInformation info;

    private Map<String, File> filesMap;

    private FilesListAdapter listAdapter;
    private ListView listView;

    private int numberLoading = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_finder);
        info = (SideLoadInformation) getIntent().getSerializableExtra(LOAD_INFO_PARAM);
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

        recursiveSearch(Environment.getExternalStorageDirectory(), info.fileExtension);
        recursiveSearch(Environment.getRootDirectory(), info.fileExtension);
    }

    private BaseActivity getSelf(){
        return this;
    }

    private void setupViews(){
        listView = (ListView) findViewById(R.id.file_finding_list_view);
        listAdapter = new FilesListAdapter(getApplicationContext(), new ArrayList<File>(filesMap.values()));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choseFile(listAdapter.getItem(position));
            }
        });
    }

    private void choseFile(File file){

        setResult(0, this.getIntent().setData(Uri.fromFile(file)));
        goBack();
    }

    @Override
    protected void handleBack() {
        setResult(1);
        super.handleBack();
    }

    protected void goBack(){
        super.handleBack();
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
