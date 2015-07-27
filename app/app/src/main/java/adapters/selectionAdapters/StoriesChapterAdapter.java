package adapters.selectionAdapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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


/**
 * Created by Acts Media Inc on 4/12/14.E
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
        setColorChange(holder, getColorForState(selectedPosition == position));


        StoriesChapter positionModel = (StoriesChapter) getItem(position);
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
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }


    protected int getColorForState(boolean selected){

        if(!selected){
            return context.getResources().getColor(R.color.black_light);
        }
        else {
            return context.getResources().getColor(R.color.cyan);
        }
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
