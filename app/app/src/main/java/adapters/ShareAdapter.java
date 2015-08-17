package adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.unfoldingword.mobile.R;

import java.util.List;

//import android.widget.IconTextView;

/**
 * Created by Fechner on 7/8/15.
 */
public class ShareAdapter extends ArrayAdapter<String> {

    public ShareAdapter(Context context, List<String> objects) {
        super(context, R.layout.row_share, objects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final String currentRow = getItem(position);
        ViewHolderForGroup holder = null;
        if (view == null) {

            holder = new ViewHolderForGroup();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_share, parent, false);

            holder.labelTextView = (TextView) view.findViewById(R.id.share_label);
            holder.iconView = (ImageView) view.findViewById(R.id.share_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        holder.labelTextView.setText(currentRow);

        Drawable image = getDrawableForText(currentRow);
        if(image != null){
            holder.iconView.setImageDrawable(image);
            holder.iconView.setVisibility(View.VISIBLE);
        }
        else{
            int icon = getResourceForText(currentRow);
            if(icon > -1){
                holder.iconView.setImageResource(getResourceForText(currentRow));
                holder.iconView.setVisibility(View.VISIBLE);
            }
            else{
                holder.iconView.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }

    private int getResourceForText(String text){

//        if(text.equalsIgnoreCase(getContext().getString(R.string.qr_code_title))){
//            return R.drawable.qr_code;
//        }
       if(text.equalsIgnoreCase(getContext().getString(R.string.bluetooth_title))){
            return R.drawable.bluetooth_icon;
        }
        else if(text.contains(getContext().getString(R.string.external_storage_title))){
            return R.drawable.sd_card_icon;
        }

        else{
            return -1;
        }
    }

    private Drawable getDrawableForText(String text){

        if(text.contains(getContext().getString(R.string.wi_fi_direct_title))){
            return new IconDrawable(getContext(), FontAwesomeIcons.fa_wifi).colorRes(R.color.black);
        }
        else if(text.contains(getContext().getString(R.string.choose_directory_title)) || text.contains(getContext().getString(R.string.choose_file_title))){
            return new IconDrawable(getContext(), FontAwesomeIcons.fa_folder).colorRes(R.color.black);
        }
        else if(text.contains(getContext().getString(R.string.auto_find_title))){
            return new IconDrawable(getContext(), FontAwesomeIcons.fa_search).colorRes(R.color.black);
        }
        else{
            return null;
        }
    }

    private class ViewHolderForGroup {

        private TextView labelTextView;
        private ImageView iconView;
    }
}
