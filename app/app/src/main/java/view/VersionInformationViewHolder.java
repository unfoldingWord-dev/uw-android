package view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.DownloadState;
import model.daoModels.Version;
import utils.RowStatusHelper;

/**
 * Created by Fechner on 7/6/15.
 */
public class VersionInformationViewHolder {

    private TextView titleView;
    private Button statusImageButton;
    private TextView verificationTextView;
    private TextView checkingEntityTextView;
    private TextView checkingLevelTitle;

    private ImageView checkingLevelImage;
    private TextView checkingLevelTextView;
    private TextView versionTextView;
    private TextView publishDateTextView;


    public VersionInformationViewHolder(View view) {

        setupViews(view);
        titleView.setVisibility(View.GONE);
    }

    private void setupViews(View parent){

        this.titleView = (TextView) parent.findViewById(R.id.version_information_title_view);
        this.statusImageButton = (Button) parent.findViewById(R.id.verification_status);
        this.verificationTextView = (TextView) parent.findViewById(R.id.verification_text_view);
        this.checkingEntityTextView = (TextView) parent.findViewById(R.id.checking_entity_text_view);
        this.checkingLevelTitle = (TextView) parent.findViewById(R.id.version_verification_title);

        this.checkingLevelImage = (ImageView) parent.findViewById(R.id.checking_level_image);
        this.checkingLevelTextView = (TextView) parent.findViewById(R.id.checking_level_explanation_text);
        this.versionTextView = (TextView) parent.findViewById(R.id.version_text_view);
        this.publishDateTextView = (TextView) parent.findViewById(R.id.publish_date_text_view);
    }

    public void setInfoForVersion(Context context, Version version){

        checkingEntityTextView.setText(version.getStatusCheckingEntity());
        checkingLevelImage.setImageResource(ViewHelper.getDarkCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        versionTextView.setText(version.getStatusVersion());
        publishDateTextView.setText(version.getStatusPublishDate());
        verificationTextView.setText(version.getVerificationText());
        checkingLevelTextView.setText(ViewHelper.getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));
        versionTextView.setText(version.getName());

        int verificationStatus = version.getVerificationStatus();
        statusImageButton.setBackgroundResource(RowStatusHelper.getColorForStatus(verificationStatus));
        statusImageButton.setText(RowStatusHelper.getButtonTextForStatus(context, verificationStatus));
    }

    public void setRowState(Version version){

        switch (DownloadState.createState(version.getSaveState())){

            case DOWNLOAD_STATE_DOWNLOADED:{
                verificationTextView.setVisibility(View.VISIBLE);
                checkingLevelTitle.setVisibility(View.VISIBLE);
                break;
            }
            case DOWNLOAD_STATE_DOWNLOADING:{
                verificationTextView.setVisibility(View.GONE);
                checkingLevelTitle.setVisibility(View.GONE);
                break;
            }

            default:{
                verificationTextView.setVisibility(View.GONE);
                checkingLevelTitle.setVisibility(View.GONE);
                break;
            }
        }
    }

    public TextView getCheckingEntityTextView() {
        return checkingEntityTextView;
    }
    public void setCheckingEntityTextView(TextView checkingEntityTextView) {
        this.checkingEntityTextView = checkingEntityTextView;
    }

    public ImageView getCheckingLevelImage() {
        return checkingLevelImage;
    }
    public void setCheckingLevelImage(ImageView checkingLevelImage) {
        this.checkingLevelImage = checkingLevelImage;
    }

    public TextView getCheckingLevelTextView() {
        return checkingLevelTextView;
    }
    public void setCheckingLevelTextView(TextView checkingLevelTextView) {
        this.checkingLevelTextView = checkingLevelTextView;
    }

    public TextView getPublishDateTextView() {
        return publishDateTextView;
    }
    public void setPublishDateTextView(TextView publishDateTextView) {
        this.publishDateTextView = publishDateTextView;
    }

    public Button getStatusImageButton() {
        return statusImageButton;
    }
    public void setStatusImageButton(Button statusImageButton) {
        this.statusImageButton = statusImageButton;
    }

    public TextView getTitleView() {
        return titleView;
    }
    public void setTitleView(TextView titleView) {
        this.titleView = titleView;
    }

    public TextView getVerificationTextView() {
        return verificationTextView;
    }
    public void setVerificationTextView(TextView verificationTextView) {
        this.verificationTextView = verificationTextView;
    }

    public TextView getVersionTextView() {
        return versionTextView;
    }
    public void setVersionTextView(TextView versionTextView) {
        this.versionTextView = versionTextView;
    }
}
