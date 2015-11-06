package adapters.selectionAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import view.ViewContentHelper;

/**
 * Created by Fechner on 2/27/15.
 * Basic adapter for initial screen
 */
public class InitialPageAdapter extends ArrayAdapter<GeneralRowInterface> {

    protected List<GeneralRowInterface> models;
    protected Context context;

    private int selectedPosition;


    public InitialPageAdapter(Context context, List<GeneralRowInterface> models, int selectedPosition) {
        super(context, R.layout.row_general, models);
        this.context = context;
        this.models = models;
        this.selectedPosition = selectedPosition;
    }

    public void updateWithData(List<GeneralRowInterface> data){

        this.models = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

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

        holder.title.setText(models.get(pos).getTitle());

        return view;
    }

    private static class ViewHolderForGroup {
        TextView title;
        LinearLayout row;
    }
}
