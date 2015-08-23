package peejweej.sideloading.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import peejweej.sideloading.R;
import peejweej.sideloading.SideLoadingParams;
import peejweej.sideloading.fragments.SideShareActivityFragment;

public class SideShareActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_fragment);

        Bundle extras = getIntent().getExtras();
        getSupportFragmentManager().beginTransaction()
            .add(R.id.fragment_container, SideShareActivityFragment
                .constructFragment(extras.getString(SideLoadingParams.PARAM_FILE_TEXT),
                        extras.getString(SideLoadingParams.PARAM_FILE_EXTENSION)))
            .commit();
    }

}
