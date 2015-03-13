package adapters.selectionAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.unfoldingword.mobile.R;

import java.util.List;

import model.modelClasses.mainData.StoriesChapterModel;
import utils.AsyncImageLoader;
import utils.URLUtils;

/**
 * Created by Acts Media Inc on 4/12/14.
 */
public class StoriesChapterAdapter extends GeneralAdapter implements ImageLoadingListener {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;

    public StoriesChapterAdapter(Context context, List<GeneralRowInterface> list, TextView actionbarTextView, ActionBarActivity activity, ImageLoader imageLoader, String storageString) {
        super(context, R.layout.row_for_frames, list, actionbarTextView, activity, storageString);
        this.imageLoader = imageLoader;
        setImageOptions();
    }

    private void setImageOptions() {
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(new ColorDrawable(Color.WHITE))
                .showImageOnFail(new ColorDrawable(Color.BLACK)).build();
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
            holder.chapterScreenImageView = (ImageView) convertView.findViewById(R.id.chapterScreenImageView);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // setting color to particular listview
        int selectionPosition = PreferenceManager.getDefaultSharedPreferences(context).getInt(SELECTED_POS, -1);
        setColorChange(holder, getColorForState(selectionPosition, position));


        StoriesChapterModel positionModel = (StoriesChapterModel) models.get(position);
        String imgUrl = positionModel.getChildModels(context).get(0).imageUrl;
        String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
        String path = lastBitFromUrl.replaceAll("[{//:}]", "");

        String imagePath = "assets://images/" + path;


        imageLoader.displayImage(imagePath, holder.chapterScreenImageView, options, this);

        holder.chapterNameTextView.setText(positionModel.title);
        holder.referenceTextView.setText(positionModel.description);
        return convertView;
    }

    public void setColorChange(ViewHolder holder, int color) {
        holder.chapterNameTextView.setTextColor(color);
        holder.referenceTextView.setTextColor(color);
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String url, View view, FailReason failReason) {
        ImageView imageView = (ImageView) view.findViewById(R.id.chapterScreenImageView);
        if (url.contains("file")) {
            String w = getLastBitFromUrl(url);
            imageLoader.displayImage("assets://images/" + w, imageView, options);
        } else {
            String lastBitFromUrl = getLastBitFromUrl(url);

            String s = lastBitFromUrl.replaceAll("[}]", "");

            imageLoader.displayImage("assets://images/" + s, imageView, options);
        }

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    public String getLastBitFromUrl(String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    private static class ViewHolder {
        TextView chapterNameTextView;
        TextView referenceTextView;
        ImageView chapterScreenImageView;
    }
}
