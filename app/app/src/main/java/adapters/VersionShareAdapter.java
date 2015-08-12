package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import model.daoModels.Version;

/**
 * Created by Fechner on 2/27/15.
 */
public class VersionShareAdapter extends ArrayAdapter<Version> {

    private VersionAdapterListener listener;

    public interface VersionAdapterListener {
        void rowSelectedOrDeselected();
    }

    protected List<Version> models;
    protected Context context;

    private int chosenIndex = -1;

    public VersionShareAdapter(Context context, List<Version> models, VersionAdapterListener listener) {
        super(context, R.layout.row_version_selection, models);
        this.context = context;
        this.models = models;
        this.listener = listener;
    }

    private void resetSelection(){

        chosenIndex = -1;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        final Version currentItem = models.get(pos);
        ViewHolderForGroup holder = null;
        if (view == null) {

            holder = new ViewHolderForGroup();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_version_selection, parent, false);

            holder.titleTextView = (TextView) view.findViewById(R.id.keyboard_selection_title);
            holder.completedImageView = (ImageView) view.findViewById(R.id.keyboard_selection_checkbox_image);

            view.setTag(holder);
        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        holder.titleTextView.setText(currentItem.getName());
        holder.position = pos;

        view.setOnClickListener(new KeyboardsAdapterRowClickListener(pos, holder));
        holder.completedImageView.setImageResource((pos == chosenIndex) ? R.drawable.checkbox_selected : R.drawable.checkbox);

        return view;
    }

    public Version getSelectedVersion(){

        if(chosenIndex > -1 && models.size() > chosenIndex) {
            return models.get(chosenIndex);
        }
        else{
            return null;
        }
    }


    class KeyboardsAdapterRowClickListener implements View.OnClickListener{

        final int pos;
        final ViewHolderForGroup viewGroup;


        public KeyboardsAdapterRowClickListener(int pos, ViewHolderForGroup viewGroup) {
            this.pos = pos;
            this.viewGroup = viewGroup;
        }

        @Override
        public void onClick(View v) {
            if(chosenIndex == pos){
                resetSelection();
            }
            else{
                chosenIndex = pos;
            }
            viewGroup.completedImageView.setImageResource((pos == chosenIndex)? R.drawable.checkbox_selected : R.drawable.checkbox);
            listener.rowSelectedOrDeselected();
            notifyDataSetChanged();
        }
    }

    private class ViewHolderForGroup {

        private TextView titleTextView;
        private ImageView completedImageView;
        private int position;
    }

    public void reloadData(List<Version> rows){
        this.models = rows;
        this.notifyDataSetChanged();
    }
}


