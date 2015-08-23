package peejweej.sideloading.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import peejweej.sideloading.R;
import peejweej.sideloading.SideLoadingParams;

/**
 * A placeholder fragment containing a simple view.
 */
public class SideShareActivityFragment extends Fragment {

    String fileText;
    String fileExtension;

    public static SideShareActivityFragment constructFragment(String fileText, String fileName){

        Bundle extras = new Bundle();
        extras.putString(SideLoadingParams.PARAM_FILE_TEXT, fileText);
        extras.putString(SideLoadingParams.PARAM_FILE_NAME, fileName);

        SideShareActivityFragment fragment = new SideShareActivityFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    public SideShareActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileText = getArguments().getString(SideLoadingParams.PARAM_FILE_TEXT);
        fileExtension = getArguments().getString(SideLoadingParams.PARAM_FILE_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_side_share, container, false);
    }
}
