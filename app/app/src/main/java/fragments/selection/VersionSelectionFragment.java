/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments.selection;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import activity.UWBaseActivity;
import adapters.versions.VersionViewModel;
import adapters.versions.VersionsAdapter;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Language;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;
import view.AnimatedExpandableListView;

/**
 * Fragment for users to select a new version
 */
public class VersionSelectionFragment extends DialogFragment {
    private static final String TAG = "VersionSelectionFragment";

    private static final String CHOSEN_PROJECT = "CHOSEN_PROJECT";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";
    private static final String IS_SECOND_VERSION_PARAM = "IS_SECOND_VERSION_PARAM";

private VersionSelectionFragmentListener listener;

    protected ExpandableListView listView = null;
    private VersionsAdapter adapter;
    private TextView titleTextView;

    private boolean isSecondVersion;
    private Project chosenProject = null;

    private boolean showProjectTitle = false;

    //region setup

    /**
     * @param project project for which the version will be showing
     * @param showTitle true if the title should be shown
     * @param isSecondVersion true if this is for the second version in the diglot view
     * @return a newly constructed VersionSelectionFragment
     */
    public static VersionSelectionFragment newInstance(Project project, boolean showTitle, boolean isSecondVersion) {

        VersionSelectionFragment fragment = new VersionSelectionFragment();

        Bundle args = new Bundle();
        args.putSerializable(CHOSEN_PROJECT, project);
        args.putBoolean(SHOW_TITLE_PARAM, showTitle);
        args.putBoolean(IS_SECOND_VERSION_PARAM, isSecondVersion);
        fragment.setArguments(args);

        return fragment;
    }

    public VersionSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (VersionSelectionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chosenProject = (Project) getArguments().getSerializable(CHOSEN_PROJECT);
        if(chosenProject != null) {
            chosenProject = Project.getProjectForId(chosenProject.getId(), DaoDBHelper.getDaoSession(getContext()));
            showProjectTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
            isSecondVersion = getArguments().getBoolean(IS_SECOND_VERSION_PARAM);
        }
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
        setupViews(view);
        return view;
    }

    private void setupViews(View view){

        titleTextView = (TextView) view.findViewById(R.id.version_selection_text_view);
        titleTextView.setText(chosenProject.getTitle());
        titleTextView.setVisibility((showProjectTitle)? View.VISIBLE : View.GONE);

        prepareListView(view);
    }

    protected void prepareListView(View view){

        listView = (ExpandableListView) view.findViewById(R.id.versions_list);
//        listView.setOnGroupClickListener(getOnGroupClickListener());

//        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                return false;
//            }
//        });
        Version version = getVersion();

        adapter = new VersionsAdapter(this, VersionViewModel.createModels(chosenProject), (version != null)? version.getId() : -1, getAdapterListener());
        listView.setAdapter(adapter);

//        if(version != null) {
//            Language language = version.getLanguage();
//            int expandedIndex = language.getProject().getLanguages().indexOf(language);
//            if(expandedIndex > -1){
//                listView.expandGroup(expandedIndex);
//            }
//        }
    }

    private Version getVersion(){

        if(chosenProject.isBibleStories()){
            long pageId = UWPreferenceManager.getSelectedStoryPage(getContext());
            if(pageId > -1) {
                return DaoDBHelper.getDaoSession(getContext()).getStoryPageDao()
                        .load(UWPreferenceManager.getSelectedStoryPage(getContext()))
                        .getStoriesChapter().getBook().getVersion();
            }
        }
        else{
            long chapterId = UWPreferenceManager.getSelectedBibleChapter(getContext());
            if(chapterId > -1) {
                return BibleChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getContext()))
                        .getBook().getVersion();
            }
        }

        return null;
    }

    //endregion

    //region listeners

//    private ExpandableListView.OnGroupClickListener getOnGroupClickListener(){
//
//        return new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
//                // We call collapseGroupWithAnimation(int) and
//                // expandGroupWithAnimation(int) to animate group
//                // expansion/collapse.
//                if(!showProjectTitle) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (listView.isGroupExpanded(groupPosition)) {
//                                listView.collapseGroupWithAnimation(groupPosition);
//                            } else {
//                                listView.expandGroupWithAnimation(groupPosition);
//                            }
//                        }
//                    });
//                    return true;
//                }
//                else {
//                    return false;
//                }
//            }
//        };
//    }

    private VersionsAdapter.VersionAdapterListener getAdapterListener(){

        return new VersionsAdapter.VersionAdapterListener(){
            @Override
            public void versionWasSelected(Version version) {
                if(listener != null){
                    listener.versionWasSelected(version, isSecondVersion);
                }
            }

            @Override
            public void isLoading(boolean loading) {
                ((UWBaseActivity) getActivity()).setLoadingFragmentVisibility(loading, "Deleting...", false);
            }
        };
    }

    //endregion

    //region detach

    //endregion

    public interface VersionSelectionFragmentListener {
        /**
         * Called when the user selects a version
         * @param version Version the was selected
         * @param isSecondVersion true if this is for the second version in the diglot view
         */
        void versionWasSelected(Version version, boolean isSecondVersion);
    }

}
