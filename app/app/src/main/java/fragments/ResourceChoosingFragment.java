/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.ResourceChoosingAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResourceChoosingListener} interface
 * to handle interaction events.
 * Use the {@link ResourceChoosingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourceChoosingFragment extends DialogFragment {

    private static final String VERSION_PARAM = "VERSION_PARAM";

    @Bind(R.id.resource_choosing_list_view) ListView listView;
    @Bind(R.id.resource_choosing_title_view) TextView titleView;
    @Bind(R.id.resource_choosing_sub_title_view) TextView subTitleView;
    @Bind(R.id.resource_choosing_cancel_button) Button cancelButton;
    @Bind(R.id.resource_choosing_continue_button) Button continueButton;

    private Version[] versions;
    private ResourceChoosingAdapter adapter;
    private ResourceChoosingListener listener;

    public static ResourceChoosingFragment newInstance(Version[] versions) {
        ResourceChoosingFragment fragment = new ResourceChoosingFragment();
        Bundle args = new Bundle();
        args.putSerializable(VERSION_PARAM, versions);
        fragment.setArguments(args);
        return fragment;
    }

    public static ResourceChoosingFragment newInstance(Version version, ResourceChoosingListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(VERSION_PARAM, version);
        ResourceChoosingFragment fragment = new ResourceChoosingFragment();
        fragment.setArguments(args);
        fragment.listener = listener;
        return fragment;
    }

    public static ResourceChoosingFragment newInstance(Version[] versions, ResourceChoosingListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(VERSION_PARAM, versions);
        ResourceChoosingFragment fragment = new ResourceChoosingFragment();
        fragment.setArguments(args);
        fragment.listener = listener;
        return fragment;
    }

    public ResourceChoosingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Object[] baseObjects = (Object[]) getArguments().getSerializable(VERSION_PARAM);
            if (baseObjects != null) {
                Version[] parsedVersions = new Version[baseObjects.length];
                for(int i = 0; i < baseObjects.length; i++) {
                    parsedVersions[i] = (Version) baseObjects[i];
                }
                versions = parsedVersions;
            }
            else{
                versions = new Version[0];
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resource_choosing, container, false);
        ButterKnife.bind(this, view);
        setupView();
        return view;
    }

    private void setupView(){

        String text = (versions.length == 1) ? versions[0].getName() : "Versions";
        titleView.setText("Sharing " + text);
        subTitleView.setText("Choose Optional Resources");

        adapter = new ResourceChoosingAdapter(getContext(), getData());
        listView.setAdapter(adapter);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDialog();
            }
        });
    }

    @OnClick(R.id.resource_choosing_cancel_button) void cancel(){
        dismiss();
    }

    @OnClick(R.id.resource_choosing_continue_button) protected void endDialog(){
        sendChoiceInformation();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(listener == null) {
            try {
                listener = (ResourceChoosingListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private List<ResourceChoosingAdapter.ResourceChoosingAdapterProtocol> getData(){

        List<ResourceChoosingAdapter.ResourceChoosingAdapterProtocol> data = new ArrayList<>();

        for(Version version : versions) {
            if (version.hasAudio()) {
                MediaType type = MediaType.MEDIA_TYPE_AUDIO;
                data.add(new SimpleResourceChoosingObject(getActivity().getApplicationContext(), version, version.getTitle() + " " + type.getTitle(), type));
            }
            if (version.hasVideo()) {
                MediaType type = MediaType.MEDIA_TYPE_VIDEO;
                data.add(new SimpleResourceChoosingObject(getActivity().getApplicationContext(), version, version.getTitle() + " " + type.getTitle(), type));
            }
        }
        return data;
    }

    private class SimpleResourceChoosingObject implements ResourceChoosingAdapter.ResourceChoosingAdapterProtocol{

        private Context context;
        private Version version;
        private String name;
        private MediaType type;


        public SimpleResourceChoosingObject(Context context, Version version, String name, MediaType type) {
            this.version = version;
            this.context = context;
            this.name = name;
            this.type = type;
        }

        @Override
        public Drawable getImage() {
            return context.getResources().getDrawable(MediaType.getImageResourceForType(type));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Version getVersion() {
            return version;
        }

        @Override
        public MediaType getType() {
            return type;
        }
    }

    private void sendChoiceInformation() {

        Map<Version, List<MediaType>> types = new HashMap<>();

        for(Version version: versions){
            types.put(version, Arrays.asList(MediaType.MEDIA_TYPE_TEXT));
        }
        int i = 0;
        for (ResourceChoosingAdapter.ResourceChoosingAdapterProtocol item : adapter.getObjects()){

            if(adapter.hasSelectedIndex(i)){
                ArrayList<MediaType> specifiedTypes = new ArrayList<>();
                List<MediaType> currentTypes = types.get(item.getVersion());
                specifiedTypes.addAll(currentTypes);
                specifiedTypes.add(item.getType());
                types.put(item.getVersion(), specifiedTypes);
            }
            i++;
        }
        listener.resourcesChosen(this, types);
    }

    public interface ResourceChoosingListener {
        void resourcesChosen(DialogFragment dialogFragment, Map<Version, List<MediaType>> sharingChoices);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
