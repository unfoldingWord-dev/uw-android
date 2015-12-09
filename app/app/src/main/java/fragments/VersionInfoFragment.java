/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments;

import android.app.Dialog;
import android.content.Context;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.Version;
import model.parsers.MediaType;
import signing.Status;
import view.ViewContentHelper;

/**
 * Created by PJ Fechner
 * Fragment for showing the info of a Version
 */
public class VersionInfoFragment extends DialogFragment {

    private static final String VERSION_PARAM = "VERSION_PARAM";
    private static final String MEDIA_TYPE_PARAM = "MEDIA_TYPE_PARAM";

    private Version version;
    private MediaType type;



    @Bind(R.id.version_information_checking_level_image)
    ImageView checkingLevelImage;

    @Bind(R.id.version_information_checking_level_text)
    TextView checkingLevelText;

    @Bind(R.id.version_information_resource_type_image)
    ImageView resourceTypeImage;

    @Bind(R.id.version_information_verification_information_status)
    Button versionVerificationButton;

    @Bind(R.id.version_information_verification_text_view)
    TextView versionVerificationTextView;

    @Bind(R.id.version_information_main_info_text_view)
    TextView versionAuthenticationMainTextView;
    //region setup

    /**
     * @param version Version that's desired to show
     * @return
     */
    public static VersionInfoFragment createFragment(Version version, MediaType type){

        Bundle args = new Bundle();
        args.putSerializable(VERSION_PARAM, version);
        args.putSerializable(MEDIA_TYPE_PARAM, type);

        VersionInfoFragment fragment = new VersionInfoFragment();
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
            type = (MediaType) getArguments().getSerializable(MEDIA_TYPE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.version_information_view, container, false);

        new VersionInfoViewHolder(getActivity().getApplicationContext(), view, version, type);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

     //endregion

    public static class VersionInfoViewHolder{

        @Bind(R.id.version_information_checking_level_image)
        ImageView checkingLevelImage;

        @Bind(R.id.version_information_checking_level_text)
        TextView checkingLevelText;

        @Bind(R.id.version_information_resource_type_image)
        ImageView resourceTypeImage;

        @Bind(R.id.version_information_verification_information_status)
        Button versionVerificationButton;

        @Bind(R.id.version_information_verification_text_view)
        TextView versionVerificationTextView;

        @Bind(R.id.version_information_main_info_text_view)
        TextView versionAuthenticationMainTextView;

        public VersionInfoViewHolder(Context context, View view, Version version,MediaType type){
            ButterKnife.bind(this, view);
            updateViews(context, version, type);
        }

        private void updateViews(Context context, Version version, MediaType type){

            boolean notText = type != MediaType.MEDIA_TYPE_TEXT;
            final int verificationStatus;
            if(notText){
                verificationStatus = Status.ERROR.ordinal();
            }
            else{
                verificationStatus = version.getVerificationStatus();
            }

            versionVerificationTextView.setText((notText) ? "Cryptographic verification has not yet been added to media" : version.getVerificationText());
            checkingLevelText.setText(ViewContentHelper.getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));
            checkingLevelImage.setImageResource(ViewContentHelper.getDarkCheckingLevelImageResource(Integer.parseInt(version.getStatusCheckingLevel())));
            resourceTypeImage.setImageResource(MediaType.getImageResourceForType((notText) ? MediaType.MEDIA_TYPE_AUDIO : MediaType.MEDIA_TYPE_TEXT));

            versionAuthenticationMainTextView.setText(getMainVerificationText(version));
            versionVerificationButton.setVisibility(View.INVISIBLE);

            DataFileManager.getStateOfContent(context, version, type, new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    if (state == DownloadState.DOWNLOAD_STATE_DOWNLOADED) {
                        versionVerificationButton.setText(ViewContentHelper.getVerificationButtonTextForStatus(verificationStatus));
                        versionVerificationButton.setBackgroundResource(ViewContentHelper.getDrawableForStatus(verificationStatus));
                        versionVerificationButton.setVisibility(View.VISIBLE);
                    }
                }
            });

        }

        private static String getMainVerificationText(Version version){

            return "Version: " + version.getStatusVersion() + "\n\nPublished: " + version.getStatusPublishDate()
                    + "\n\nAuthentication: " + version.getStatusCheckingEntity();
        }
    }
}