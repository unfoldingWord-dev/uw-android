package adapters.versions;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.DataFileManager;
import model.DownloadState;

/**
 * Created by Fechner on 12/1/15.
 */
public class VersionViewHolder {

    private static final String TAG = "VersionViewHolder";
    private VersionViewModel.ResourceViewModel viewModel;
    private View view;
    private ImageView resourceImage;
    private TextView titleTextView;
    private ImageButton checkingLevelImage;
    private ProgressBar loadingProgressBar;
    private ImageView downloadingImageView;
    private FrameLayout rowActionButtonLayout;

    public VersionViewHolder(View view) {
        this.view = view;
        resourceImage = (ImageView) view.findViewById(R.id.version_row_resource_type_image);
        titleTextView = (TextView) view.findViewById(R.id.version_row_resource_text);
        checkingLevelImage = (ImageButton) view.findViewById(R.id.version_row_checking_level);
        loadingProgressBar = (ProgressBar) view.findViewById(R.id.version_row_download_progress_bar);
        downloadingImageView = (ImageView) view.findViewById(R.id.version_download_image);
        rowActionButtonLayout = (FrameLayout) view.findViewById(R.id.version_row_download_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowClicked();
            }
        });
        checkingLevelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkingLevelClicked();
            }
        });
        rowActionButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonClicked();
            }
        });
    }

    public VersionViewModel.ResourceViewModel getViewModel() {
        return viewModel;
    }

    public void updateViews(final VersionViewModel.ResourceViewModel model){

        this.viewModel = model;
        model.getDownloadStateAsync(new DataFileManager.GetDownloadStateResponse() {
            @Override
            public void foundDownloadState(DownloadState state) {
                setupForDownloadState(state);
                Log.d(TAG, "updated Views for model: " + model.toString() + "to download state: " + state.toString());
            }
        });
        resourceImage.setImageResource(model.getImageResource());
        titleTextView.setText(model.getTitle());
    }

    public void setupForDownloadState(DownloadState state){

        switch (state){
            case DOWNLOAD_STATE_DOWNLOADING:{
                downloadingImageView.setVisibility(View.INVISIBLE);
                loadingProgressBar.setVisibility(View.VISIBLE);
                checkingLevelImage.setImageResource(viewModel.getCheckingLevelImage());
                break;
            }
            case DOWNLOAD_STATE_DOWNLOADED:{
                downloadingImageView.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.INVISIBLE);
                downloadingImageView.setImageResource(R.drawable.trash_can_icon);
                checkingLevelImage.setImageResource(viewModel.getVerifiedCheckingLevelImage());
                break;
            }
            default:{
                downloadingImageView.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.INVISIBLE);
                downloadingImageView.setImageResource(R.drawable.download_icon);
                checkingLevelImage.setImageResource(viewModel.getCheckingLevelImage());
                break;
            }
        }

        checkingLevelImage.setVisibility(View.VISIBLE);
    }

    private void rowClicked(){
        viewModel.itemClicked(this);
    }

    private void checkingLevelClicked(){
        viewModel.checkingLevelClicked();
    }

    private void actionButtonClicked(){
        viewModel.doActionOnModel(this);
    }
}
