package adapters.sharing;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.daoModels.Version;

/**
 * Created by Fechner on 12/11/15.
 */
public class SharingAdapterVersionViewGroup {

    Version version;

    private View baseView;

    @Bind(R.id.share_selection_title)
    TextView titleTextView;

    @Bind(R.id.share_selection_checkbox_image)
    ImageView checkMarkImageView;

    public SharingAdapterVersionViewGroup(View view) {
        baseView = view;
        ButterKnife.bind(this, view);
    }

    public Version getVersion() {
        return version;
    }

    public void updateWithVersion(final Version version, final SharingAdapterVersionViewGroupListener listener){
        this.version = version;
        titleTextView.setText(version.getName());

        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clicked(version);
            }
        });
    }

    public void setChecked(boolean checked){
        checkMarkImageView.setImageResource((checked) ? R.drawable.check_box_checked : R.drawable.check_box_empty);
    }

    public interface SharingAdapterVersionViewGroupListener{

        void clicked(Version version);
    }
}
