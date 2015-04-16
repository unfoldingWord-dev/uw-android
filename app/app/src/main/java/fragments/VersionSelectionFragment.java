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

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import adapters.selectionAdapters.CollapsibleVersionAdapter;
import model.datasource.ProjectDataSource;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.ProjectModel;
import model.modelClasses.mainData.VersionModel;
import utils.UWPreferenceManager;

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
    private static final String CHOSEN_PROJECT_ID = "CHOSEN_PROJECT_ID";
    static final String STORIES_SLUG = "obs";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    private String chosenProjectId;
    protected ExpandableListView mListView = null;
    private View footerView = null;
    private ProjectModel chosenProject = null;
    CollapsibleVersionAdapter adapter;

    private boolean showTitle = false;
    private TextView titleTextView;

    private VersionSelectionFragmentListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VersionSelectionFragment.
     */
    public static VersionSelectionFragment newInstance(String projId, boolean showTitle) {
        VersionSelectionFragment fragment = new VersionSelectionFragment();
        Bundle args = new Bundle();
        args.putString(CHOSEN_PROJECT_ID, projId);
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
            chosenProjectId = getArguments().getString(CHOSEN_PROJECT_ID);
            showTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
        }
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

    private void setupViews(View view, LayoutInflater inflater){
        titleTextView = (TextView) view.findViewById(R.id.version_selection_text_view);
        if(!showTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            titleTextView.setVisibility(View.VISIBLE);
        }
        prepareListView(view, inflater);
    }


    protected void prepareListView(View view, LayoutInflater inflater){

        //getting instance of ExpandableListView
        mListView = (ExpandableListView) view.findViewById(R.id.versions_list);

        if (chosenProject == null) {
            addProject();
        }

        adapter = new CollapsibleVersionAdapter(this, this.chosenProject);
        if(footerView == null) {
            footerView = inflater.inflate(R.layout.version_footer, null);

            // change version number
            TextView tView = (TextView) footerView.findViewById(R.id.textView);
            String versionName = BuildConfig.VERSION_NAME;

            tView.setText(versionName);
            mListView.addFooterView(footerView);
        }
        mListView.setAdapter(adapter);

        int selectedIndex;
        if(chosenProject.slug.equalsIgnoreCase(STORIES_SLUG)){
            setupForStories();
        }
        else{
            selectedIndex = setupForBible();
            mListView.expandGroup(selectedIndex);
        }


    }

    private int setupForStories(){

        Context context = getContext();
        int selectedIndex = 0;
        String selectedVersion = UWPreferenceManager.getSelectedStoryVersion(context);
        if(Long.parseLong(selectedVersion) < 0){
            selectedIndex = 0;
        }
        else {
            VersionModel version = new VersionDataSource(context).getModel(selectedVersion);

            for(int i = 0; i < chosenProject.getChildModels(context).size(); i++){
                if(chosenProject.getChildModels(context).get(i).slug.equalsIgnoreCase(version.getParent(context).slug)){
                    selectedIndex = i;
                }
                mListView.expandGroup(i);
            }
        }
        for(int i = 0; i < adapter.getGroupCount(); i++){
            mListView.expandGroup(i);
        }
        return selectedIndex;
    }

    private int setupForBible(){

        Context context = getContext();
        int selectedIndex = -1;
        String selectedVersion = UWPreferenceManager.getSelectedBibleVersion(context);
        if(Long.parseLong(selectedVersion) < 0){
            selectedIndex = 0;
        }
        else {
            VersionModel version = new VersionDataSource(context).getModel(selectedVersion);

            for(int i = 0; i < chosenProject.getChildModels(context).size(); i++){
                if(chosenProject.getChildModels(context).get(i).slug.equalsIgnoreCase(version.getParent(context).slug)){
                    selectedIndex = i;
                    break;
                }
            }
        }
        return selectedIndex;
    }

    private void addProject(){

            this.chosenProject = new ProjectDataSource(getContext()).getModel(chosenProjectId);
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

        this.mListener.rowWasSelected();
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
        // TODO: Update argument type and name
        public void rowWasSelected();
    }

}
