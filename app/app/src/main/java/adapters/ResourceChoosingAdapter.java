/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.daoModels.Version;
import model.parsers.MediaType;

//import android.widget.IconTextView;

/**
 * Created by Fechner on 7/8/15.
 */
public class ResourceChoosingAdapter extends ArrayAdapter<ResourceChoosingAdapter.ResourceChoosingAdapterProtocol> {

    private List<ResourceChoosingAdapterProtocol> objects;
    private List<Boolean> selectionsList;

    public ResourceChoosingAdapter(Context context, List<ResourceChoosingAdapterProtocol> objects) {
        super(context, R.layout.row_resource_choosing, objects);
        this.objects = objects;
        seedSelections();
    }

    private void seedSelections(){
        selectionsList = new ArrayList<>();

        for(int i = 0; i < objects.size(); i++){
            selectionsList.add(i, false);
        }
    }

    public List<ResourceChoosingAdapterProtocol> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ResourceChoosingAdapterProtocol currentRow = getItem(position);
        ViewHolderForGroup holder = null;
        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_resource_choosing, parent, false);
            holder = new ViewHolderForGroup(position, view);
            view.setTag(holder);

        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        holder.setIndex(position);
        holder.labelTextView.setText(currentRow.getName());

        Drawable iconImage = currentRow.getImage();
        if(iconImage != null) {
            holder.iconView.setImageDrawable(currentRow.getImage());
        }
        holder.iconView.setVisibility((iconImage != null)? View.VISIBLE : View.INVISIBLE);

        return view;
    }

    public boolean hasSelectedIndex(int index){

        return selectionsList.get(index);
    }

    private class GroupClickListener implements View.OnClickListener{

        ViewHolderForGroup group;

        public GroupClickListener(ViewHolderForGroup group) {
            this.group = group;
        }

        @Override
        public void onClick(View v) {
            group.toggleChecked();
        }
    }

    protected class ViewHolderForGroup {

        boolean checked = false;
        GroupClickListener clickListener;
        private int index;

        @Bind(R.id.row_resource_choosing_title)
        protected TextView labelTextView;

        @Bind(R.id.row_resource_choosing_icon)
        protected ImageView iconView;

        @Bind(R.id.row_resource_choosing_check_mark)
        protected ImageView checkMarkView;

        public ViewHolderForGroup(int index, View view) {
            this.index = index;
            ButterKnife.bind(this, view);
            clickListener = new GroupClickListener(this);
            view.setOnClickListener(clickListener);
            updateCheckMark();
        }

        public void setIndex(int index) {
            this.index = index;
        }

        void toggleChecked(){
            checked = !checked;
            selectionsList.set(index, checked);
            updateCheckMark();
        }

        void updateCheckMark(){
            checkMarkView.setImageResource((checked)? R.drawable.checkbox_selected : R.drawable.checkbox);
        }
    }

    public interface ResourceChoosingAdapterProtocol {

        Version getVersion();
        String getName();
        Drawable getImage();
        MediaType getType();
    }
}
