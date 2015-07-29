package adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import activity.bookSelection.StoryChapterSelectionActivity;
import model.daoModels.StoriesChapter;
import utils.AsyncImageLoader;
import utils.UWPreferenceManager;
import view.ASyncImageView;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class StoryPagerAdapter extends PagerAdapter {


    private static final String TAG = "ViewPagerAdapter";

    private Context context;
    private ViewGroup container;
    private StoriesChapter currentChapter;

    private int lastChapterNumber = -1;


    public StoryPagerAdapter(Context context, StoriesChapter model) {
        currentChapter = model;
        if(currentChapter != null) {
            lastChapterNumber = currentChapter.getBook().getStoryChapters().size();
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return (currentChapter != null)? currentChapter.getStoryPages().size() + 1 : 0;
    }

    public void update(StoriesChapter chapter){
        this.currentChapter = chapter;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
        View view = null;

        if (position == getCount() - 1) {
            view = getNextChapterView(inflater);
        } else {

            view = inflater.inflate(R.layout.stories_pager_layout, container, false);
            ASyncImageView chapterImageView = (ASyncImageView) view.findViewById(R.id.chapter_image_view);
            TextView storyTextView = (TextView) view.findViewById(R.id.story_text_view);
            storyTextView.setText(currentChapter.getStoryPages().get(position).getText());
            String imgUrl = currentChapter.getStoryPages().get(position).getImageUrl();
            String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
            String path = lastBitFromUrl.replaceAll("[{//:}]", "");

//            String imagePath = context.getAssets(). "assets://";/

            chapterImageView.setImageBitmap(getBitmapFromAsset("images/" + path));
        }
        ((ViewPager) container).addView(view);
        return view;
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

    private View getNextChapterView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.next_chapter_screen_layout, container, false);
        Button nextButton = (Button) view.findViewById(R.id.next_chapter_screen_button);

        if(Integer.parseInt(currentChapter.getNumber()) == lastChapterNumber){
            String nextButtonString = context.getResources().getString(R.string.chapters);
            nextButton.setText(nextButtonString);
//            nextButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    activity.finish();
//                }
//            });
        }
        else {
            String nextButtonString = context.getResources().getString(R.string.next_chapter);
            nextButton.setText(nextButtonString);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToNextChapter();
                }
            });
        }
        return view;
    }

    private void moveToNextChapter(){

        int chapterNumber = Integer.parseInt(currentChapter.getNumber());
//        String languageName = currentChapter.language;
        String nextChapterNumber = Integer.toString(chapterNumber + 1);
        if(nextChapterNumber.length() == 1){
            nextChapterNumber = "0" + nextChapterNumber;
        }

        List<StoriesChapter> chapters = currentChapter.getBook().getStoryChapters();

        StoriesChapter nextChapter = null;
        int newChapterNumber = Integer.parseInt(this.currentChapter.getNumber());
        for(StoriesChapter chapter : chapters){
            if(Integer.parseInt(chapter.getNumber()) == newChapterNumber + 1){
                nextChapter = chapter;
                break;
            }
        }

        this.currentChapter = nextChapter;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(StoryChapterSelectionActivity.CHAPTERS_INDEX_STRING, newChapterNumber).commit();
        getCount();

        int current_value = Integer.parseInt(currentChapter.getNumber());
        UWPreferenceManager.setSelectedStoryPage(context, nextChapter.getId());

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
}
