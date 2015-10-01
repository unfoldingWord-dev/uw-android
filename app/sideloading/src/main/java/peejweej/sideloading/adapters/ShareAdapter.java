package peejweej.sideloading.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import peejweej.sideloading.R;
import peejweej.sideloading.model.SideLoadType;

/**
 * Created by PJ Fechner on 7/8/15.
 *
 */
public class ShareAdapter extends ArrayAdapter<SideLoadType> {

    public ShareAdapter(Context context, List<SideLoadType> objects) {
        super(context, R.layout.row_sideloading_share, objects);
    }

    @Override
    public SideLoadType getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
//        view = super.getView(position, view, parent);
        final SideLoadType currentRow = getItem(position);
        ViewHolderForGroup holder;
        if (view == null) {

            holder = new ViewHolderForGroup();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_sideloading_share, null);

            holder.labelTextView = (TextView) view.findViewById(R.id.share_label);
            holder.iconView = (ImageView) view.findViewById(R.id.share_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        holder.labelTextView.setText(SideLoadType.getSideLoadName(currentRow));

        int icon = SideLoadType.getResourceForType(currentRow);
        if(icon > -1){
            holder.iconView.setImageResource(icon);
            holder.iconView.setVisibility(View.VISIBLE);
        }
        else{
            holder.iconView.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private class ViewHolderForGroup {

        private TextView labelTextView;
        private ImageView iconView;
    }
}
