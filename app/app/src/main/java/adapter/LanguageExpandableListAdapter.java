package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.distantshores.unfoldingword.R;

import java.util.List;

import models.LanguageModel;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class LanguageExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<LanguageModel> models;

    public LanguageExpandableListAdapter(Context context, List<LanguageModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getGroupCount() {
        return models.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return models.size();
    }

    @Override
    public Object getGroup(int i) {
        return models;
    }

    @Override
    public Object getChild(int i, int i2) {
        return models;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int pos, boolean b, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_language_chooser_group, viewGroup, false);
            holder = new ViewHolder();
            holder.languageNameTextView = (TextView) view.findViewById(R.id.languageNameTextView);
            holder.languageTypeImageView = (ImageView) view.findViewById(R.id.languageTypeImageView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        List<LanguageModel> list = (List<LanguageModel>) getGroup(pos);
        holder.languageNameTextView.setText(list.get(pos).languageName);
        return view;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    private static class ViewHolder {
        TextView languageNameTextView;
        ImageView languageTypeImageView;
    }
}
