/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments.selection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.sharing.SharingAdapter;
import adapters.sharing.SharingLanguageViewModel;
import model.daoModels.Project;
import model.daoModels.Version;

/**
 * A fragment representing a list of Items.
 */
public class ShareSelectionFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECTS_PARAM = "PROJECTS_PARAM";

    private Version selectedVersion;

    private ExpandableListView listView;

    public static ShareSelectionFragment newInstance(Project[] versions) {
        ShareSelectionFragment fragment = new ShareSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROJECTS_PARAM, versions);
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
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_selection, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view){

        listView = (ExpandableListView) view.findViewById(R.id.share_selection_list_view);

        List<SharingLanguageViewModel> data = getData();

        if(data != null) {
            listView.setAdapter(new SharingAdapter(this, data, new SharingAdapter.SharingAdapterListener() {
                @Override
                public void versionChosen(Version version) {
                    selectedVersion = version;
                    listView.invalidateViews();
                }
            }));
        }
    }

    private List<SharingLanguageViewModel> getData(){

        Project[] projects = (Project[]) getArguments().getSerializable(PROJECTS_PARAM);

        return SharingLanguageViewModel.createViewModels(getActivity().getApplicationContext(), projects);
    }


    public Version getSelectedVersion(){

        return selectedVersion;
    }
}
