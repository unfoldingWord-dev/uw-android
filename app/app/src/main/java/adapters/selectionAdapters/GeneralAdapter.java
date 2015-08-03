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

/**
 * Created by Fechner on 2/27/15.
 */
public class GeneralAdapter extends ArrayAdapter<GeneralRowInterface> {

    protected List<GeneralRowInterface> models;
    protected Context context;
    protected Fragment fragment = null;

    private int selectedRow;


    public void update(List<GeneralRowInterface> rows){
        update(rows, -1);
    }

    public void update(List<GeneralRowInterface> rows, int selectedRow){
        models = rows;
        this.selectedRow = selectedRow;
        notifyDataSetChanged();
    }

    public GeneralAdapter(Context context, List<GeneralRowInterface> models, Fragment fragment, int selectedRow) {
        super(context, R.layout.row_general, models);
        this.context = context;
        this.models = models;
        this.fragment = fragment;
        this.selectedRow = selectedRow;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        GeneralRowInterface row = getItem(pos);
        ViewHolderForGroup holder = null;
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

        setColorChange(holder, getColorForState(selectedRow == pos));
        holder.title.setText(row.getTitle());

        return view;
    }

    protected int getColorForState(boolean selected){

        if(selected){
            return context.getResources().getColor(R.color.cyan);
        }
        else {
            return context.getResources().getColor(R.color.black_light);
        }
    }


    public void setColorChange(ViewHolderForGroup holder, int color) {

        holder.title.setTextColor(color);
    }

    private static class ViewHolderForGroup {

        TextView title;
        LinearLayout row;
    }
}
