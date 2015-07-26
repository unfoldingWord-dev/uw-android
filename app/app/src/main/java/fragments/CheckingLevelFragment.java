package fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.daoModels.Version;
import view.ViewHelper;

public class CheckingLevelFragment extends DialogFragment {

    private static final String VERSION_PARAM = "VERSION_PARAM";

    private Version version;

    public static CheckingLevelFragment createFragment(Version version){

        Bundle args = new Bundle();
        args.putSerializable(VERSION_PARAM, version);

        CheckingLevelFragment fragment = new CheckingLevelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CheckingLevelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            version = (Version) getArguments().getSerializable(VERSION_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.version_information_view, container, false);
        TextView checkingEntityTextView = (TextView) view.findViewById(R.id.checking_entity_text_view);
        ImageView checkingLevelImage = (ImageView) view.findViewById(R.id.checking_level_image);
        TextView versionTextView = (TextView) view.findViewById(R.id.version_text_view);
        TextView publishDateTextView = (TextView) view.findViewById(R.id.publish_date_text_view);
        TextView verificationTextView = (TextView) view.findViewById(R.id.verification_text_view);
        Button status = (Button) view.findViewById(R.id.status);
        TextView checkingLevelExplanationTextView = (TextView) view.findViewById(R.id.checking_level_explanation_text);


        checkingEntityTextView.setText(version.getStatusCheckingEntity());
        checkingLevelImage.setImageResource(ViewHelper.getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        versionTextView.setText(version.getStatusVersion());
        publishDateTextView.setText(version.getStatusPublishDate());
        verificationTextView.setText(ViewHelper.getVerificationText(version));
        checkingLevelExplanationTextView.setText(ViewHelper.getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));

        int verificationStatus = 1;//version.getVerificationStatus(getActivity().getApplicationContext());
        status.setBackgroundResource(ViewHelper.getColorForStatus(verificationStatus));
        status.setText(ViewHelper.getButtonTextForStatus(verificationStatus, getActivity().getApplicationContext()));

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}