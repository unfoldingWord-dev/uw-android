package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import activity.ChapterSelectionActivity;
import models.ChapterModel;

/**
 * Created by Acts Media Inc on 4/12/14.
 */
public class ChapterAdapter extends ArrayAdapter<ChapterModel> implements ImageLoadingListener {
    protected ImageLoader imageLoader;
    DisplayImageOptions options;
    private Context context;
    private ArrayList<ChapterModel> list;

    public ChapterAdapter(Context context, ArrayList<ChapterModel> list, ImageLoader imageLoader) {
        super(context, R.layout.row_for_frames, list);
        this.context = context;
        this.list = list;
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
        int selected_pos = PreferenceManager.getDefaultSharedPreferences(context).getInt(ChapterSelectionActivity.SELECTED_CHAPTER_POS, -1);
        if (selected_pos != -1) {
            if (selected_pos == position) {
                setColor(holder, context.getResources().getColor(R.color.cyan));
            } else {
                setColor(holder, context.getResources().getColor(R.color.black_light));
            }
        } else {
            if (position == 0) {
                setColor(holder, context.getResources().getColor(R.color.cyan));
            } else {
                setColor(holder, context.getResources().getColor(R.color.black_light));
            }
        }

        // setup image view
        String imgUrl = list.get(position).pageModels.get(0).imageUrl;
        String lastBitFromUrl = getLastBitFromUrl(imgUrl);
        String s = lastBitFromUrl.replaceAll("[{//:}]", "");
        /*
        if (NetWorkUtil.isConnected(context)) {
            if (imgUrl.contains("{{")) {
                String replace = imgUrl.replace("{{", "");
                if (replace.contains("}}")) {
                    String lastURL = replace.replace("}}", "");
                    imageLoader.displayImage(lastURL, holder.chapterScreenImageView, options, this);
                }
            } else {
                imageLoader.displayImage(imgUrl, holder.chapterScreenImageView, options, this);
            }
        } else {
*/
            imageLoader.displayImage("assets://images/" + s, holder.chapterScreenImageView, options, this);

  //      }


        holder.chapterNameTextView.setText(list.get(position).title);
        holder.referenceTextView.setText(list.get(position).reference);
        return convertView;
    }

    private void setColor(ViewHolder holder, int color) {
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
