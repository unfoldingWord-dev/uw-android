package adapter.selectionAdapters;

import android.content.Context;
import android.preference.PreferenceManager;
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
public class GeneralAdapter extends ArrayAdapter<GeneralRowInterface> {

    protected static String SELECTED_POS;


    protected final List<GeneralRowInterface> models;
    protected final ActionBarActivity activity;
    protected TextView actionbarTextview;
    protected Context context;

    public GeneralAdapter(Context context, int resource, List<GeneralRowInterface> models, TextView actionbarTextView, ActionBarActivity activity, String positionHolder) {
        super(context, resource, models);
        SELECTED_POS = positionHolder;
        this.context = context;
        this.models = models;
        this.actionbarTextview = actionbarTextView;
        this.activity = activity;
    }

    public GeneralAdapter(Context context, List<GeneralRowInterface> models, TextView actionbarTextView, ActionBarActivity activity, String positionHolder) {
        super(context, R.layout.row_general, models);
        SELECTED_POS = positionHolder;
        this.context = context;
        this.models = models;
        this.actionbarTextview = actionbarTextView;
        this.activity = activity;
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
        int selected = PreferenceManager.getDefaultSharedPreferences(context).getInt(SELECTED_POS, -1);
        if (selected != -1) {
            if (pos == (selected - 1))
                setColorChange(holder, context.getResources().getColor(R.color.cyan));
            else
                setColorChange(holder, context.getResources().getColor(R.color.black_light));

        } else {
            if (pos == 0)
                setColorChange(holder, context.getResources().getColor(R.color.cyan));
            else
                setColorChange(holder, context.getResources().getColor(R.color.black_light));
        }

        holder.title.setText(models.get(pos).getTitle());

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
