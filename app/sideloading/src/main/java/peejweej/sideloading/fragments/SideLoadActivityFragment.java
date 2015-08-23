package peejweej.sideloading;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SideLoadActivityFragment extends Fragment {

    private String fileExtension;

    public static SideLoadActivityFragment constructFragment(String fileExtension){

        Bundle extras = new Bundle();
        extras.putString(SideLoadingParams.PARAM_FILE_EXTENSION, fileExtension);

        SideLoadActivityFragment fragment = new SideLoadActivityFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    public SideLoadActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fileExtension = getArguments().getString(SideLoadingParams.PARAM_FILE_EXTENSION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_side_load, container, false);
    }
}
