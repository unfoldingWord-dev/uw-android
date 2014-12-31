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
import android.util.Log;
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

import org.json.JSONException;
import org.unfoldingword.mobile.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import activity.ChapterSelectionActivity;
import db.DBManager;
import models.ChaptersModel;
import parser.JsonParser;
import utils.AppUtils;
import utils.AppVariable;
import utils.NetWorkUtil;

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
    private ArrayList<ChaptersModel> models;
    private ImageLoader mImageLoader;
    private String chapter_number;
    private String next;
    private ViewGroup container;

    public ViewPagerAdapter(Object context, ArrayList<ChaptersModel> models, ImageLoader mImageLoader, String nextChapter, String chapter_number, TextView actionbarTextView, Intent intent, String languages) {
        this.context = (Context) context;
        models.add(new ChaptersModel());
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

    public static boolean storeImage(Context currentContex, Bitmap imageData, String fileName) {
        //get path to external storage (SD card)

        Log.i(TAG, "Will Store Image: " + fileName);

//        File sdIconStorageDir = new File(AppUtils.DIR_NAME);
        File prelimFile = new File(currentContex.getFilesDir(), fileName);

        //create storage directories, if they don't exist
//        if (!sdIconStorageDir.isDirectory()) {
//            sdIconStorageDir.mkdirs();
//        }

        try {

//            File resolveMeSDCard = new File(AppUtils.DIR_NAME + fileName);
//            resolveMeSDCard.createNewFile();

            File saveFile = new File(currentContex.getFilesDir(), fileName);
            saveFile.createNewFile();
//            String filePath = sdIconStorageDir.toString() + filename;

            FileOutputStream fileOutputStream = currentContex.openFileOutput(fileName, Context.MODE_PRIVATE);

            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
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
                        ChaptersModel nextChapter = null;
                        try {
                            int current_value = Integer.parseInt(chapter_number);
                            if (current_value < 10) {
                                nextChapter = dbManager.getNextChapter("0" + (current_value + 1) + "", languages);
                            } else {
                                nextChapter = dbManager.getNextChapter((current_value + 1) + "", languages);
                            }

                            chapter_number = nextChapter.number;
                            actionbarTextView.setText(nextChapter.title);
                            languages = nextChapter.loadedLanguage;
                            // increment chapter selection
                            int prv_pos = PreferenceManager.getDefaultSharedPreferences(context).getInt(ChapterSelectionActivity.SELECTED_CHAPTER_POS, -1);
                            if (prv_pos != -1) {
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(ChapterSelectionActivity.SELECTED_CHAPTER_POS, prv_pos + 1).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {

                        }
                        
                        try {
                            ViewPagerAdapter.this.models = JsonParser.parseStory(nextChapter.jsonArray);
                            ViewPagerAdapter.this.models.add(new ChaptersModel());
                            intent.removeExtra(ChapterSelectionActivity.CHAPTERS_MODEL_INSTANCE);
                            AppVariable.MODELS = nextChapter;
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {

                        }
                        ((ViewPager) ViewPagerAdapter.this.container).setCurrentItem(0);

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
            String imgUrl = models.get(position).imgUrl;
            String lastBitFromUrl = getLastBitFromUrl(imgUrl);
            String s = lastBitFromUrl.replaceAll("[}]", "");

            /*
            if (NetWorkUtil.isConnected(context)) {
                if (imgUrl.contains("{{")) {
                    String replace = imgUrl.replace("{{", "");
                    if (replace.contains("}}")) {
                        String lastURL = replace.replace("}}", "");
                        mImageLoader.displayImage(lastURL, chapterImageView, options, this);
                    }
                } else {

                    mImageLoader.displayImage(imgUrl, chapterImageView, options, this);
                }
            } else {
            */
                mImageLoader.displayImage("assets://images/" + s, chapterImageView, options, this);
            //}


        }
        ((ViewPager) container).addView(view);
        return view;
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
            String w = getLastBitFromUrl(url);
            mImageLoader.displayImage("assets://images/" + w, imageView, options);
        } else {
            String lastBitFromUrl = getLastBitFromUrl(url);
            String s = lastBitFromUrl.replaceAll("[}]", "");
            mImageLoader.displayImage("assets://images/" + s, imageView, options);
        }

    }

    @Override
    public void onLoadingComplete(String url, View view, Bitmap bitmap) {
        if (url.contains("file")) {

        } else {
            String lastBitFromUrl = "";
            if (url.contains("}}")) {
                String replace = url.replace("}}", "");
                lastBitFromUrl = getLastBitFromUrl(replace);
            } else {
                lastBitFromUrl = getLastBitFromUrl(url);
                storeImage(context, bitmap, lastBitFromUrl);
            }
        }


    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    public String getLastBitFromUrl(String url) {
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }
}
