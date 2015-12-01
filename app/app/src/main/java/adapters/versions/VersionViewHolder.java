package adapters.versions;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 12/1/15.
 */
public class VersionViewHolder {

    View view;
    ImageView resourceImage;
    TextView titleTextView;
    ImageView checkingLevelImage;
    ProgressBar loadingProgressBar;
    ImageView downloadingImageView;
    FrameLayout rowActionButtonLayout;

    public VersionViewHolder(View view) {
        this.view = view;
        resourceImage = (ImageView) view.findViewById(R.id.version_row_resource_type_image);
        titleTextView = (TextView) view.findViewById(R.id.version_row_resource_text);
        checkingLevelImage = (ImageView) view.findViewById(R.id.version_row_checking_level);
        loadingProgressBar = (ProgressBar) view.findViewById(R.id.version_row_download_progress_bar);
        downloadingImageView = (ImageView) view.findViewById(R.id.version_download_image);
        rowActionButtonLayout = (FrameLayout) view.findViewById(R.id.version_row_download_button);
    }
}
