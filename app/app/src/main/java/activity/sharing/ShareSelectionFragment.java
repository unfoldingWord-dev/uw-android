package activity.sharing;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import java.util.Arrays;

import adapters.VersionShareAdapter;
import model.daoModels.Version;

/**
 * A fragment representing a list of Items.
 */
public class ShareSelectionFragment extends ListFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VERSIONS_PARAM = "VERSIONS_PARAM";

    public static ShareSelectionFragment newInstance(Version[] versions) {
        ShareSelectionFragment fragment = new ShareSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(VERSIONS_PARAM, versions);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShareSelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Version[] versions = (Version[]) getArguments().getSerializable(VERSIONS_PARAM);
            if(versions != null) {
                setListAdapter(new VersionShareAdapter(getActivity().getApplicationContext(), Arrays.asList(versions),
                        (VersionShareAdapter.VersionAdapterListener) getActivity()));
            }
        }
    }

    public Version getSelectedVersion(){

        return ((VersionShareAdapter) getListAdapter()).getSelectedVersion();
    }
}
