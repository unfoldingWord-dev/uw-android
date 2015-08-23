package peejweej.sideloading.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import peejweej.sideloading.R;
import peejweej.sideloading.SideLoadActivityFragment;
import peejweej.sideloading.SideLoadingParams;

public class SideLoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_fragment);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, SideLoadActivityFragment
                        .constructFragment(getIntent().getStringExtra(SideLoadingParams.PARAM_FILE_EXTENSION)))
                .commit();
    }
}
