package activity.sharing;

import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;


import org.unfoldingword.mobile.R;

import java.io.File;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import sideloading.SideLoader;
import utils.FileLoader;

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

//        File keyboardFile = new File(uri.getPath());
//        loader.textWasFound(loader.unzipText(FileLoader.getStringFromFile(keyboardFile)));
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }
}
