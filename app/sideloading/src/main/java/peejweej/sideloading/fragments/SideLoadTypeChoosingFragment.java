package peejweej.sideloading.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import peejweej.sideloading.R;
import peejweej.sideloading.adapters.ShareAdapter;
import peejweej.sideloading.model.SideLoadInformation;
import peejweej.sideloading.model.SideLoadType;

/**
 * A placeholder fragment containing a simple view.
 */
public class SideLoadTypeChoosingFragment extends Fragment {

    private static final String INFO_PARAM = "INFO";

    private ListView listView;
    private ShareAdapter adapter;

    private SideLoadInformation info;

    SideLoadTypeFragmentListener listener;

    public static SideLoadTypeChoosingFragment constructFragment(SideLoadInformation info){

        SideLoadTypeChoosingFragment fragment = new SideLoadTypeChoosingFragment();

        if(info != null) {
            Bundle extras = new Bundle();
            extras.putSerializable(INFO_PARAM, info);
            fragment.setArguments(extras);
        }

        return fragment;
    }

    public SideLoadTypeChoosingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.info = (SideLoadInformation) getArguments().getSerializable(INFO_PARAM);
        }
        listener = (SideLoadTypeFragmentListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_side_load_type_choosing, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view){

        listView = (ListView) view.findViewById(R.id.side_load_list_view);

        boolean isSharing = (info == null || info.fileName != null);
        boolean canUseQrCode = (isSharing && info != null && info.file != null && info.file.length() < 1024);
        adapter = new ShareAdapter(getActivity(),
                SideLoadType.getListOfSideLoadTypes(getActivity().getApplicationContext(), !isSharing, canUseQrCode));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedType(adapter.getItem(position));
            }
        });
    }

    private void selectedType(SideLoadType type){
        listener.typeWasChosen(type);
    }

    public interface SideLoadTypeFragmentListener{
        void typeWasChosen(SideLoadType type);
    }

}
