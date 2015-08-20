package adapters.selectionAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import view.ViewGraphicsHelper;

/**
 * Created by PJ Fechner on 2/27/15.
 * Adapter for Chapters
 */
public class ChaptersAdapter extends ArrayAdapter<GeneralRowInterface> {

    protected Context context;
    private int selectedRow;
    protected List<GeneralRowInterface> models;

    protected Fragment fragment = null;

    public ChaptersAdapter(Context context, List<GeneralRowInterface> models, Fragment fragment, int selectedRow) {
        super(context, R.layout.row_general);
        this.context = context;
        this.models = models;
        this.fragment = fragment;
        this.selectedRow = selectedRow;
    }

    public void update(List<GeneralRowInterface> rows){
        update(rows, -1);
    }

    public void update(List<GeneralRowInterface> rows, int selectedRow){
        models = rows;
        this.selectedRow = selectedRow;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        GeneralRowInterface row = models.get(pos);
        ViewHolderForGroup holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_general, parent, false);
            holder = new ViewHolderForGroup();
            holder.title = (TextView) view.findViewById(R.id.itemTitle);
            holder.row = (LinearLayout) view.findViewById(R.id.row);
            view.setTag(holder);

        } else {
            holder = (ViewHolderForGroup) view.getTag();
        }

        setColorChange(holder, ViewGraphicsHelper.getColorForSelection(selectedRow == pos));
        holder.title.setText(row.getTitle());

        return view;
    }

    public void setColorChange(ViewHolderForGroup holder, int color) {

        holder.title.setTextColor(color);
    }

    private static class ViewHolderForGroup {

        TextView title;
        LinearLayout row;
    }
}
