package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import adapters.selectionAdapters.CollapsibleVersionAdapter;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Project;
import model.daoModels.Version;
import services.UWPreLoader;
import utils.UWPreferenceManager;
import view.AnimatedExpandableListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.VersionSelectionFragment.VersionSelectionFragmentListener} interface
 * to handle interaction events.
 * Use the {@link VersionSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VersionSelectionFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHOSEN_PROJECT = "CHOSEN_PROJECT";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    protected AnimatedExpandableListView mListView = null;
    private Project chosenProject = null;
    private CollapsibleVersionAdapter adapter;

    private boolean showProjectTitle = false;
    private TextView titleTextView;

    private VersionSelectionFragmentListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionSelectionFragment.
     */
    public static VersionSelectionFragment newInstance(Project project, boolean showTitle) {

        VersionSelectionFragment fragment = new VersionSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHOSEN_PROJECT, project);
        args.putBoolean(SHOW_TITLE_PARAM, showTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public VersionSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chosenProject = (Project) getArguments().getSerializable(CHOSEN_PROJECT);
            chosenProject = Project.getProjectForId(chosenProject.getId(), DaoDBHelper.getDaoSession(getContext()));
            showProjectTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showProjectTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_version_selection, container, false);
        setupViews(view, inflater);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!showProjectTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            titleTextView.setText(chosenProject.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setupViews(View view, LayoutInflater inflater){
        titleTextView = (TextView) view.findViewById(R.id.version_selection_text_view);
        if(!showProjectTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            titleTextView.setText(chosenProject.getTitle());
            titleTextView.setVisibility(View.VISIBLE);
        }
        prepareListView(view);
    }


    protected void prepareListView(View view){

        //getting instance of ExpandableListView
        mListView = (AnimatedExpandableListView) view.findViewById(R.id.versions_list);

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if(!showProjectTitle) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mListView.isGroupExpanded(groupPosition)) {
                                mListView.collapseGroupWithAnimation(groupPosition);
                            } else {
                                mListView.expandGroupWithAnimation(groupPosition);
                            }
                        }
                    });
                    return true;
                }
                else {
                    return false;
                }
            }
        });


        long selectedVersionId;

        if(chosenProject.isBibleStories()){
            selectedVersionId = UWPreferenceManager.getSelectedStoryVersion(getContext());
        }
        else{
            selectedVersionId = UWPreferenceManager.getSelectedBibleChapter(getContext());
        }

        adapter = new CollapsibleVersionAdapter(this, this.chosenProject, selectedVersionId, new CollapsibleVersionAdapter.VersionAdapterListener(){
            @Override
            public void versionWasSelected(Version version) {
                selectedVersion(version);
            }
        });
        mListView.setAdapter(adapter);
        int selectedIndex;

    }

    private void selectedVersion(Version version){

        if(chosenProject.isBibleStories()){
            UWPreferenceManager.setSelectedStoryVersion(getContext(), version.getId());
        }
        else{
            UWPreferenceManager.setBib(getContext(),);
        }
    }

    private Context getContext(){
        return getActivity().getApplicationContext();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (VersionSelectionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(adapter != null) {
            adapter.willDestroy();
        }
    }

    public void rowSelected(){
        if(mListener != null) {
            this.mListener.rowWasSelected();
        }
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
    public interface VersionSelectionFragmentListener {
        public void rowWasSelected();
    }

}
