package adapters.selectionAdapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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
public class UWGeneralListAdapter extends ArrayAdapter<GeneralRowInterface> {

    protected List<GeneralRowInterface> models;
    protected Context context;

    private int selectedPosition;


    public UWGeneralListAdapter(Context context, List<GeneralRowInterface> models, int selectedPosition) {
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

        setColorChange(holder, getColorForState(selectedPosition, pos));
        holder.title.setText(models.get(pos).getTitle());

        return view;
    }

    protected int getColorForState(int selectionPosition, int itemPosition){

        if(selectionPosition == -1 || itemPosition != selectionPosition){
            return context.getResources().getColor(R.color.black_light);
        }
        else {
            return context.getResources().getColor(R.color.cyan);
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
