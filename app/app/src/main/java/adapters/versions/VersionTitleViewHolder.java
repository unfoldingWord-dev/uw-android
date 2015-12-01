package adapters.versions;

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
