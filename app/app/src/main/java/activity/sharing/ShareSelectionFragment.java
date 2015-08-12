package activity.sharing;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import java.util.Arrays;
import java.util.List;

import adapters.VersionShareAdapter;
import model.daoModels.Version;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ShareSelectionFragment extends ListFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VERSIONS_PARAM = "VERSIONS_PARAM";

    private Version[] versions;

    private OnFragmentInteractionListener mListener;

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
            versions = (Version[]) getArguments().getSerializable(VERSIONS_PARAM);
        }

        setListAdapter(new VersionShareAdapter(getActivity().getApplicationContext(), Arrays.asList(versions),
                (VersionShareAdapter.VersionAdapterListener) getActivity()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public Version getSelectedVersion(){

        return ((VersionShareAdapter) getListAdapter()).getSelectedVersion();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
