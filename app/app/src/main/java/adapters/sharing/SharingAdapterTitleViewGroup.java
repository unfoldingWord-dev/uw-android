package adapters.sharing;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Fechner on 12/11/15.
 */
public class SharingAdapterTitleViewGroup {

    @Bind(R.id.row_version_group_title)
    TextView titleView;
    @Bind(R.id.row_version_group_indicator)
    ImageView toggleView;

    public SharingAdapterTitleViewGroup(View view) {

        ButterKnife.bind(this, view);
    }

    public void updateWithModel(SharingLanguageViewModel model){
        this.titleView.setText(model.getTitle());
    }

    public void setExpanded(boolean expanded){

        toggleView.setImageResource((expanded)? R.drawable.chevron_up_icon : R.drawable.chevron_down_icon);
    }
}
