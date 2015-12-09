package fragments;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.AudioBitrate;

public class BitrateFragment extends DialogFragment {

    private static final String BITRATES_PARAM = "BITRATES_PARAM";
    private static final String TITLE_PARAM = "TITLE_PARAM";

    private String title;
    private AudioBitrate[] bitrates;

    private BitrateFragmentListener listener;

    public BitrateFragment() {
        // Required empty public constructor
    }

    public static BitrateFragment newInstance(AudioBitrate[] bitrates, String title, BitrateFragmentListener listener) {
        Bundle args = new Bundle();
        args.putSerializable(BITRATES_PARAM, bitrates);
        args.putString(TITLE_PARAM, title);
        BitrateFragment fragment = new BitrateFragment();
        fragment.setArguments(args);
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bitrates = (AudioBitrate[]) getArguments().getSerializable(BITRATES_PARAM);
            title = getArguments().getString(TITLE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bitrate, container, false);
        setupView(view);
        return view;
    }

    private void setupView(View view){

        ((TextView) view.findViewById(R.id.fragment_bitrate_title)).setText(title);
        view.findViewById(R.id.fragment_bitrate_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });

        final ListView listview = (ListView) view.findViewById(R.id.fragment_bitrate_list_view);
        listview.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(),
                R.layout.row_bitrate_choosing, R.id.row_bitrate_choosing_text, bitrates));

        final BitrateFragment fragment = this;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.bitrateChosen(fragment, bitrates[position]);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void cancelClicked() {
        listener.dismissed();
        dismiss();
    }

    public interface BitrateFragmentListener {
        void bitrateChosen(DialogFragment fragment, AudioBitrate bitrate);
        void dismissed();
    }
}
