package adapters.selectionAdapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import model.daoModels.StoriesChapter;
import utils.AsyncImageLoader;
import view.ASyncImageView;
import view.ViewGraphicsHelper;


/**
 * Created by PJ Fechner
 * Adapter for showing OBS Chapters
 */
public class StoriesChapterAdapter extends ArrayAdapter<StoriesChapter>{

    private Context context;
    private int selectedPosition;

    public StoriesChapterAdapter(Context context, List<StoriesChapter> list, int selectedPosition) {
        super(context, R.layout.row_for_frames, list);
        this.context = context;
        this.selectedPosition = selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

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

        setColorChange(holder, ViewGraphicsHelper.getColorForSelection(selectedPosition == position));

        StoriesChapter positionModel = getItem(position);
        String imgUrl = positionModel.getStoryPages().get(0).getImageUrl();
        String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
        String path = lastBitFromUrl.replaceAll("[{//:}]", "");

        holder.chapterScreenImageView.setImageBitmap(getBitmapFromAsset("images/" + path));
        holder.chapterNameTextView.setText(positionModel.getTitle());
        holder.referenceTextView.setText(positionModel.getRef());
        return convertView;
    }

    private Bitmap getBitmapFromAsset(String strName)
    {
        AssetManager assetManager = context.getAssets();
        InputStream input = null;
        try {
            input = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(input);
    }

    public void setColorChange(ViewHolder holder, int color) {
        holder.chapterNameTextView.setTextColor(color);
        holder.referenceTextView.setTextColor(color);
    }

    private static class ViewHolder {
        TextView chapterNameTextView;
        TextView referenceTextView;
        ASyncImageView chapterScreenImageView;
    }
}
