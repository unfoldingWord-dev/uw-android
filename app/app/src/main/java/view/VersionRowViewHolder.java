package view;

import android.content.Context;
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
import utils.ViewHelper;

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
    private Button informationStatus;
    private ImageView downloadButton;

    private FrameLayout downloadFrame;
    private ProgressBar downloadProgressBar;

    private Button downloadAudioButton;
    private ViewGroup downloadingAudioLayout;
    private Button downloadVideoButton;
    private ViewGroup downloadingVideoLayout;
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
        informationStatus = (Button) baseView.findViewById(R.id.verification_information_status);
        downloadFrame = (FrameLayout) baseView.findViewById(R.id.download_status_frame);
        downloadProgressBar = (ProgressBar) baseView.findViewById(R.id.download_progress_bar);

        downloadButton = (ImageView) baseView.findViewById(R.id.download_status_image);
        downloadAudioButton = (Button) baseView.findViewById(R.id.download_audio_button);
        downloadingAudioLayout = (ViewGroup) baseView.findViewById(R.id.versions_downloading_audio_layout);
        downloadVideoButton = (Button) baseView.findViewById(R.id.download_video_button);
        downloadingVideoLayout = (ViewGroup) baseView.findViewById(R.id.versions_downloading_video_layout);
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
        languageTypeImageView.setImageResource(ViewContentHelper.getDarkCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        languageNameTextView.setText(version.getName());

        int verificationStatus = version.getVerificationStatus();
        status.setBackgroundResource(RowStatusHelper.getColorForStatus(verificationStatus));
        status.setText(RowStatusHelper.getButtonTextForStatus(context, verificationStatus));

        informationStatus.setBackgroundResource(RowStatusHelper.getColorForStatus(verificationStatus));
        informationStatus.setText(RowStatusHelper.getButtonTextForStatus(context, verificationStatus));

        setupForAudioDownloadState(version.getAudioDownloadState(), version.hasAudio() && version.getSaveState() == DownloadState.DOWNLOAD_STATE_DOWNLOADED.ordinal());
        setupForVideoDownloadState(version.getVideoDownloadState(), false);
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

        downloadAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.audioButtonWasClicked(finalHolder, version);
            }
        });

        downloadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.videoButtonWasClicked(finalHolder, version);
            }
        });
    }

    private void infoWasClicked() {

        if (versionInfoLayout.getVisibility() == View.GONE) {
            ViewHelper.expand(versionInfoLayout);
//            CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(versionInfoLayout, 300, CustomSlideAnimationRelativeLayout.EXPAND);
//            versionInfoLayout.startAnimation(animationRelativeLayout);
        } else {
            ViewHelper.collapse(versionInfoLayout);
//            CustomSlideAnimationRelativeLayout animationRelativeLayout = new CustomSlideAnimationRelativeLayout(versionInfoLayout, 300, CustomSlideAnimationRelativeLayout.COLLAPSE);
//            versionInfoLayout.startAnimation(animationRelativeLayout);
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
        downloadFrame.setVisibility((!isDownloaded) ? View.VISIBLE : View.GONE);
    }

    public void setupForAudioDownloadState(DownloadState state, boolean canDownload){

        final VersionRowViewHolder finalHolder = this;
        setupForDownloadingAudio(state == DownloadState.DOWNLOAD_STATE_DOWNLOADING);
        if(!canDownload) {
            downloadAudioButton.setVisibility( View.GONE);
        }
        else if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADED){
            downloadAudioButton.setText("Delete Audio");
            downloadAudioButton.setTextColor(context.getResources().getColor(R.color.delete_button_text_color));
            downloadAudioButton.setBackgroundResource(R.drawable.delete_button_click);
            downloadAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.deleteAudioWasPressed(version);
                }
            });
        }
        else{
            downloadAudioButton.setText("Download Audio");
            downloadAudioButton.setTextColor(context.getResources().getColor(R.color.black));
            downloadAudioButton.setBackgroundResource(R.drawable.gray_button_selector);
            downloadAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.audioButtonWasClicked(finalHolder, version);
                }
            });
        }
    }

    public void setupForVideoDownloadState(DownloadState state, boolean canDownload) {

        setupForDownloadingVideo(state == DownloadState.DOWNLOAD_STATE_DOWNLOADING);
        downloadVideoButton.setVisibility((canDownload) ? View.VISIBLE : View.GONE);
    }

    private void setupForDownloadingAudio(boolean downloading){

        this.downloadingAudioLayout.setVisibility((downloading)? View.VISIBLE : View.GONE);
        this.downloadAudioButton.setVisibility((downloading)? View.GONE : View.VISIBLE);
    }

    private void setupForDownloadingVideo(boolean downloading){

        this.downloadingVideoLayout.setVisibility((downloading)? View.VISIBLE : View.GONE);
        this.downloadVideoButton.setVisibility((downloading)? View.GONE : View.VISIBLE);
    }

    public interface VersionRowViewHolderListener{

        void deleteAudioWasPressed(Version version);
        void downloadWasPressed(VersionRowViewHolder holder, Version version);
        void deletePressed(Version version);
        void versionWasSelected(Version version);
        void audioButtonWasClicked(VersionRowViewHolder holder, Version version);
        void videoButtonWasClicked(VersionRowViewHolder holder, Version version);
    }
}
