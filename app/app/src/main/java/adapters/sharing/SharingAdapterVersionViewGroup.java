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

    public int section;
    public int row;
    public boolean isChecked = false;

    private SharingAdapterVersionViewGroupListener listener;

    public SharingAdapterVersionViewGroup(View view) {
        baseView = view;
        ButterKnife.bind(this, view);
    }

    public Version getVersion() {
        return version;
    }

    public void updateWithVersion(final Version version, int section, int row, final SharingAdapterVersionViewGroupListener listener){
        this.version = version;
        this.listener = listener;
        this.section = section;
        this.row = row;
        titleTextView.setText(version.getName());

        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasClicked();
            }
        });
    }
    private void wasClicked() {
        toggleChecked();
        listener.clicked(this);
    }

    private void toggleChecked(){
        setChecked(!isChecked);
    }
    public void setChecked(boolean checked){
        this.isChecked = checked;
        checkMarkImageView.setImageResource((checked) ? R.drawable.check_box_checked : R.drawable.check_box_empty);
    }

    public interface SharingAdapterVersionViewGroupListener{

        void clicked(SharingAdapterVersionViewGroup viewGroup);
    }
}
