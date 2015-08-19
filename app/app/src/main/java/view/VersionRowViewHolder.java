package view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.DownloadState;
import model.daoModels.Version;
import utils.CustomSlideAnimationRelativeLayout;
import utils.RowStatusHelper;

/**
 * Created by Fechner on 8/19/15.
 */
public class VersionRowViewHolder {

    private View baseView;
    private VersionInformationViewHolder versionInformationHolder;

    private TextView languageNameTextView;
    private ImageView languageTypeImageView;
    private LinearLayout versionInfoLayout;
    private LinearLayout clickableLayout;
    private FrameLayout infoFrame;

    private Button status;
    private ImageView downloadButton;

    private FrameLayout downloadFrame;
    private ProgressBar downloadProgressBar;
    private Button deleteButton;

    private Context context;
    private Version version;

    private VersionRowViewHolderListener listener;

    public VersionRowViewHolder(Context context, Version version, View baseView, VersionRowViewHolderListener listener) {
        this.listener = listener;
        this.context = context;
        this.version = version;
        setupViews(baseView);
        setupOnClickListeners();
    }

    private void setupViews(View parentView){

        baseView = parentView;

        languageNameTextView = (TextView) baseView.findViewById(R.id.language_name_text_view);
        languageTypeImageView = (ImageView) baseView.findViewById(R.id.language_type_image_view);
        versionInfoLayout = (LinearLayout) baseView.findViewById(R.id.version_information_layout);
        infoFrame = (FrameLayout) baseView.findViewById(R.id.info_image_frame);
        status = (Button) baseView.findViewById(R.id.verification_status);
        downloadFrame = (FrameLayout) baseView.findViewById(R.id.download_status_frame);
        downloadProgressBar = (ProgressBar) baseView.findViewById(R.id.download_progress_bar);

        downloadButton = (ImageView) baseView.findViewById(R.id.download_status_image);
        deleteButton = (Button) baseView.findViewById(R.id.delete_button);
        clickableLayout = (LinearLayout) baseView.findViewById(R.id.clickableRow);

        versionInformationHolder = new VersionInformationViewHolder(baseView);
    }

    public void setupViewForVersion(final Version version, boolean isSelected){
        this.version = version;
        int state = isSelected? 2 : 1;
        setRowState();

        versionInformationHolder.setInfoForVersion(context, version);
        languageNameTextView.setTextColor(RowStatusHelper.getColorForState(context, state));
        languageTypeImageView.setImageResource(ViewDataHelper.getDarkCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        languageNameTextView.setText(version.getName());

        int verificationStatus = version.getVerificationStatus();
        status.setBackgroundResource(RowStatusHelper.getColorForStatus(verificationStatus));
        status.setText(RowStatusHelper.getButtonTextForStatus(context, verificationStatus));
    }

    private void setupOnClickListeners(){

        final VersionRowViewHolder finalHolder = this;

        clickableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.versionWasSelected(version);
            }
        });

        downloadFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.downloadWasPressed(finalHolder, version);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deletePressed(version);
            }
        });

        infoFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoWasClicked();
            }
        });
    }

    private void infoWasClicked() {

        if (versionInfoLayout.getVisibility() == View.GONE) {
            CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(versionInfoLayout, 300, CustomSlideAnimationRelativeLayout.EXPAND);
            versionInfoLayout.startAnimation(animationRelativeLayout);
        } else {
            CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(versionInfoLayout, 300, CustomSlideAnimationRelativeLayout.COLLAPSE);
            versionInfoLayout.startAnimation(animationRelativeLayout);
        }
    }

    private void setRowState(){

        DownloadState state = DownloadState.createState(version.getSaveState());
        boolean isDownloaded = (state == DownloadState.DOWNLOAD_STATE_DOWNLOADED);
        versionInformationHolder.setRowState(isDownloaded);

        status.setVisibility((isDownloaded) ? View.VISIBLE : View.GONE);
        downloadButton.setVisibility((state == DownloadState.DOWNLOAD_STATE_NONE)? View.VISIBLE : View.GONE);
        downloadProgressBar.setVisibility((state == DownloadState.DOWNLOAD_STATE_DOWNLOADING) ? View.VISIBLE : View.GONE);
        deleteButton.setVisibility((isDownloaded) ? View.VISIBLE : View.GONE);
        clickableLayout.setClickable(isDownloaded);
        downloadFrame.setClickable(!isDownloaded);
        downloadFrame.setVisibility((!isDownloaded)? View.VISIBLE : View.GONE);
    }

    public interface VersionRowViewHolderListener{

        void downloadWasPressed(VersionRowViewHolder holder, Version version);
        void deletePressed(Version version);
        void versionWasSelected(Version version);
    }
}
