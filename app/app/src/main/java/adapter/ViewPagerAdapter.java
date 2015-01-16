package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;

import activity.ChapterSelectionActivity;
import db.DBManager;
import db.ImageDatabaseHandler;
import models.ChapterModel;
import models.PageModel;
import utils.AppVariable;
import utils.URLUtils;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ViewPagerAdapter extends PagerAdapter implements ImageLoadingListener {


    private static final String TAG = "ViewPagerAdapter";

    DisplayImageOptions options;
    View view = null;
    DBManager dbManager = null;
    private String languages;
    private Intent intent;
    private Activity activity;
    private static Context context;
    private TextView actionbarTextView;
    private ArrayList<PageModel> models;
    private ImageLoader mImageLoader;
    private String chapter_number;
    private String next;
    private ViewGroup container;


    public ViewPagerAdapter(Object context, ArrayList<PageModel> models, com.nostra13.universalimageloader.core.ImageLoader mImageLoader, String nextChapter, String chapter_number, TextView actionbarTextView, Intent intent, String languages) {
        this.context = (Context) context;
//        models.add(new ChapterModel());
        this.models = models;
        this.mImageLoader = mImageLoader;
        next = nextChapter;
        this.chapter_number = chapter_number;
        this.actionbarTextView = actionbarTextView;
        setImageOptions();
        dbManager = DBManager.getInstance(this.context);
        this.activity = (Activity) context;
        this.intent = intent;
        this.languages = languages;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;

        // getting last row values of data base
        int frameCount = dbManager.getFrameCount();

        if (position == getCount() - 1) {
            if (frameCount != Integer.parseInt(chapter_number)) {
//            ((ViewPager) container).setCurrentItem(position + 1);

                view = inflater.inflate(R.layout.next_chapter_screen_layout, container, false);
                Button nextButton = (Button) view.findViewById(R.id.nextChapterScreenbutton);
                nextButton.setText(next);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        moveToNextChapter();
                    }
                });
            } else {
                view = inflater.inflate(R.layout.finish_screen_layout, container, false);
                Button finishButton = (Button) view.findViewById(R.id.nextChapterScreenbutton);
                finishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.finish();
                    }
                });
            }
        } else {
            view = inflater.inflate(R.layout.pager_layout, container, false);
            ImageView chapterImageView = (ImageView) view.findViewById(R.id.chapterImageView);
            TextView storyTextView = (TextView) view.findViewById(R.id.storyTextView);
            storyTextView.setText(models.get(position).text);
            String imgUrl = models.get(position).imageUrl;
            String lastBitFromUrl = URLUtils.getLastBitFromUrl(imgUrl);
            String path = lastBitFromUrl.replaceAll("[}]", "");

            boolean fileHasBeenSaved =  ImageDatabaseHandler.fileHasBeenSaved(context, path);

            Bitmap image = ImageDatabaseHandler.loadImageFrom(context, path);
            if(image != null){
                chapterImageView.setImageBitmap(image);
            }
            else{
                String imagePath = "assets://images/" + path;

                mImageLoader.displayImage(imagePath, chapterImageView, options, this);
            }

        }
        ((ViewPager) container).addView(view);
        return view;
    }

    private void moveToNextChapter(){

        ChapterModel currentChapter = models.get(0).parentChapter;
        ChapterModel nextChapter = currentChapter.parentBook.getNextChapter(currentChapter);

        ArrayList<PageModel> newPages = nextChapter.pageModels;

        int current_value = Integer.parseInt(chapter_number);
        chapter_number = nextChapter.number;
        actionbarTextView.setText(nextChapter.title);
        languages = nextChapter.parentBook.language;
        // increment chapter selection
        int prv_pos = PreferenceManager.getDefaultSharedPreferences(context).getInt(ChapterSelectionActivity.SELECTED_CHAPTER_POS, -1);
        if (prv_pos != -1) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(ChapterSelectionActivity.SELECTED_CHAPTER_POS, prv_pos + 1).commit();
        }

        models = newPages;
        intent.removeExtra(ChapterSelectionActivity.CHAPTERS_MODEL_INSTANCE);
        AppVariable.MODELS = nextChapter;
        notifyDataSetChanged();

        ((ViewPager) this.container).setCurrentItem(0);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    private void setImageOptions() {
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(new ColorDrawable(Color.WHITE))
                .showImageOnFail(new ColorDrawable(Color.WHITE)).build();
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String url, View view, FailReason failReason) {
        ImageView imageView = (ImageView) view.findViewById(R.id.chapterImageView);
        if (url.contains("file")) {
            String w = URLUtils.getLastBitFromUrl(url);
            mImageLoader.displayImage("assets://images/" + w, imageView, options);
        } else {
            String lastBitFromUrl = URLUtils.getLastBitFromUrl(url);
            String s = lastBitFromUrl.replaceAll("[}]", "");
            mImageLoader.displayImage("assets://images/" + s, imageView, options);
        }

    }

    @Override
    public void onLoadingComplete(String url, View view, Bitmap bitmap) {

        if (url.contains("file")) {
            System.out.println("It worked!");
        } else {
            String lastBitFromUrl = "";
            if (url.contains("}}")) {
                String replace = url.replace("}}", "");
                lastBitFromUrl = URLUtils.getLastBitFromUrl(replace);
            } else {
                lastBitFromUrl = URLUtils.getLastBitFromUrl(url);
                ImageDatabaseHandler.storeImage(context, bitmap, lastBitFromUrl);
            }
        }


    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

}
