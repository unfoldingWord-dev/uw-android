package adapters.selectionAdapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import model.daoModels.StoriesChapter;
import utils.AsyncImageLoader;
import view.ASyncImageView;

/**
 * Created by Acts Media Inc on 4/12/14.E
 */
public class StoriesChapterAdapter extends GeneralAdapter{

    public StoriesChapterAdapter(Context context, List<GeneralRowInterface> list, TextView actionbarTextView, ActionBarActivity activity, String storageString) {
        super(context, R.layout.row_for_frames, list, actionbarTextView, activity, storageString);
    }

    public StoriesChapterAdapter(Context context, List<GeneralRowInterface> list, Fragment fragment, String storageString) {
        super(context, R.layout.row_for_frames, list, fragment, storageString);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_for_frames, parent, false);
            holder = new ViewHolder();
            holder.chapterNameTextView = (TextView) convertView.findViewById(R.id.chapterNametextView);
            holder.referenceTextView = (TextView) convertView.findViewById(R.id.refTextView);
            holder.chapterScreenImageView = (ASyncImageView) convertView.findViewById(R.id.chapterScreenImageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // setting color to particular listview
        int selectionPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(SELECTED_POS, -1);
        setColorChange(holder, getColorForState(selectionPosition, position));


        StoriesChapter positionModel = (StoriesChapter) models.get(position);
        String imgUrl = positionModel.getStoryPages().get(0).getImageUrl();
        String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
        String path = lastBitFromUrl.replaceAll("[{//:}]", "");

        String imagePath = "assets://images/" + path;


        holder.chapterScreenImageView.setImageUrl(imagePath);
        holder.chapterNameTextView.setText(positionModel.getTitle());
        holder.referenceTextView.setText(positionModel.getRef());
        return convertView;
    }

    public void setColorChange(ViewHolder holder, int color) {
        holder.chapterNameTextView.setTextColor(color);
        holder.referenceTextView.setTextColor(color);
    }

    public String getLastBitFromUrl(String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    private static class ViewHolder {
        TextView chapterNameTextView;
        TextView referenceTextView;
        ASyncImageView chapterScreenImageView;
    }
}
