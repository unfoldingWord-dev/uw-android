package activity.sharing;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import org.unfoldingword.mobile.R;

import java.io.File;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import sideloading.SideLoader;
import utils.FileUtil;

public class LoadActivity extends UWBaseActivity {

    private static final String TAG = "LoadActivity";
    private boolean hasFoundData = false;
    SideLoader loader;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        listView = (ListView) findViewById(R.id.side_load_list_view);
        loader = new SideLoader(this, listView);
        setupToolbar(false, getString(R.string.app_name), false);
        loader.startLoading();

        Uri uri = getIntent().getData();
        if(uri != null) {
            File keyboardFile = new File(uri.getPath());
            loader.textWasFound(loader.unzipText(FileUtil.getbytesFromFile(keyboardFile)));
        }
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }
}
