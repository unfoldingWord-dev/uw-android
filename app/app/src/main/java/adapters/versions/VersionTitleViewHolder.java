package adapters.versions;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 12/1/15.
 */
public class VersionTitleViewHolder {

    View view;
    int index;
    TextView titleView;
    ImageView indicatorView;

    public VersionTitleViewHolder(View view, int index) {
        this.view = view;
        this.index = index;
        titleView = (TextView) view.findViewById(R.id.row_version_group_title);
        indicatorView = (ImageView) view.findViewById(R.id.row_version_group_indicator);
    }

    public void updateWithModel(Context context, VersionViewModel model, boolean selected){
        titleView.setText(model.getTitle());
        titleView.setTextColor(context.getResources().getColor((selected) ? R.color.primary : R.color.black));
    }

    public void setExpanded(boolean expanded){

        indicatorView.setImageResource((expanded)? R.drawable.chevron_up_icon : R.drawable.chevron_down_icon);
    }
//    public void setOnCLickListener(final int index){
//
//        this.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                indexClicked(index);
//            }
//        });
//    }
//
//    public void indexClicked(int index){
//
//    }
}
