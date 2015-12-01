/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.daoModels.Version;
import view.ViewContentHelper;

/**
 * Created by PJ Fechner
 * Fragment for showing the info of a Version
 */
public class VersionInfoFragment extends DialogFragment {

    private static final String VERSION_PARAM = "VERSION_PARAM";

    private Version version;

    //region setup

    /**
     * @param version Version that's desired to show
     * @return
     */
    public static VersionInfoFragment createFragment(Version version){

        VersionInfoFragment fragment = new VersionInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable(VERSION_PARAM, version);
        fragment.setArguments(args);

        return fragment;
    }

    public VersionInfoFragment() {
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

        setupViews(view);
        return view;
    }

    private void setupViews(View view){

        ((TextView) view.findViewById(R.id.checking_entity_text_view)).setText(version.getStatusCheckingEntity());
        ((TextView) view.findViewById(R.id.version_text_view)).setText(version.getStatusVersion());;
        ((TextView) view.findViewById(R.id.publish_date_text_view)).setText(version.getStatusPublishDate());
        ((TextView) view.findViewById(R.id.verification_text_view)).setText(version.getVerificationText());
        ((TextView) view.findViewById(R.id.checking_level_explanation_text)).setText(ViewContentHelper.getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));

        ((ImageView) view.findViewById(R.id.checking_level_image))
                .setImageResource(ViewContentHelper.getDarkCheckingLevelImageResource(Integer.parseInt(version.getStatusCheckingLevel())));

        int verificationStatus = version.getVerificationStatus();
        Button status = (Button) view.findViewById(R.id.verification_status);
        status.setBackgroundResource(ViewContentHelper.getDrawableForStatus(verificationStatus));
        status.setText(ViewContentHelper.getVerificationButtonTextForStatus(verificationStatus));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    //endregion
}