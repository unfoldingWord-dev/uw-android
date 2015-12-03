package adapters.versions;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.DownloadState;

/**
 * Created by Fechner on 12/1/15.
 */
public class VersionViewHolder {

    private static final String TAG = "VersionViewHolder";
    View view;
    ImageView resourceImage;
    TextView titleTextView;
    ImageButton checkingLevelImage;
    ProgressBar loadingProgressBar;
    ImageView downloadingImageView;
    FrameLayout rowActionButtonLayout;

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
                Log.i(TAG, "click listener clicked");
            }
        });
    }

    public void updateViews(Context context, VersionViewModel.ResourceViewModel model){

        resourceImage.setImageResource(model.getImageResource());
        titleTextView.setText(model.getTitle());
        checkingLevelImage.setImageResource(model.getCheckingLevelImage());
        setupForDownloadState(model.getDownloadState(context));
    }

    public void setupForDownloadState(DownloadState state){

        switch (state){
            case DOWNLOAD_STATE_DOWNLOADING:{
                downloadingImageView.setVisibility(View.INVISIBLE);
                loadingProgressBar.setVisibility(View.VISIBLE);
                break;
            }
            case DOWNLOAD_STATE_DOWNLOADED:{
                downloadingImageView.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.GONE);
                downloadingImageView.setImageResource(R.drawable.trash_can_icon);
                break;
            }
            default:{
                downloadingImageView.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.GONE);
                downloadingImageView.setImageResource(R.drawable.download_icon);
                break;
            }
        }
    }
}
