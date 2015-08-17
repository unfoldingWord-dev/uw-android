package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import utils.AsyncImageLoader;
import utils.UWPreferenceManager;
import view.ASyncImageView;
import view.popover.ActionItem;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class StoryPagerAdapter extends PagerAdapter {


    private static final String TAG = "ViewPagerAdapter";

    private Activity context;
    private ViewGroup container;
    private StoriesChapter mainChapter;
    private StoriesChapter secondChapter;

    private int lastChapterNumber = -1;
    private boolean isDiglot;

    private boolean isLandscape = false;

    public StoryPagerAdapter(Activity context, StoriesChapter mainChapter, StoriesChapter secondChapter) {
        this.mainChapter = mainChapter;
        this.secondChapter = secondChapter;
        if(mainChapter != null) {
            List<StoriesChapter> chapters = mainChapter.getBook().getStoryChapters();
            lastChapterNumber = Integer.parseInt(chapters.get(chapters.size() -1 ).getNumber());
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return (mainChapter != null)? mainChapter.getStoryPages().size() + 1 : 0;
    }

    public StoriesChapter getMainChapter() {
        return mainChapter;
    }

    public void update(StoriesChapter mainChapter, StoriesChapter secondChapter){
        this.mainChapter = mainChapter;
        this.secondChapter = secondChapter;
        notifyDataSetChanged();
    }

    public void setIsDiglot(boolean isDiglot){
        this.isDiglot = isDiglot;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private StoryPage getMainModelForRow(int index){
        return mainChapter.getStoryPages().get(index);
    }

    private StoryPage getSecondModelForRow(int index){
        return secondChapter.getStoryPages().get(index);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
        View view = null;

        if (position == getCount() - 1) {
            view = getNextChapterView(inflater);
        } else {

            StoryPage currentMainPage = getMainModelForRow(position);
            StoryPage currentSecondPage = getSecondModelForRow(position);

            view = inflater.inflate(R.layout.stories_pager_layout, container, false);
            ASyncImageView chapterImageView = (ASyncImageView) view.findViewById(R.id.chapter_image_view);
            chapterImageView.setScaleType((isLandscape)? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.FIT_START);

            TextView mainTextView = (TextView) view.findViewById(R.id.story_main_text_view);
            TextView secondaryTextView = (TextView) view.findViewById(R.id.story_secondary_text_view);

            mainTextView.setText(currentMainPage.getText());
            secondaryTextView.setText(currentSecondPage.getText());
            String imgUrl = mainChapter.getStoryPages().get(position).getImageUrl();
            String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
            String path = lastBitFromUrl.replaceAll("[{//:}]", "");
            chapterImageView.setImageBitmap(getBitmapFromAsset("images/" + path));

            setupPageForDiglot(mainTextView, secondaryTextView);
        }
        ((ViewPager) container).addView(view);
        return view;
    }

    private void setupPageForDiglot(TextView mainView, TextView secondView){

        secondView.setVisibility((isDiglot) ? View.VISIBLE : View.GONE);
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

        if(Integer.parseInt(mainChapter.getNumber()) == lastChapterNumber){
            String nextButtonString = context.getResources().getString(R.string.chapters);
            nextButton.setText(nextButtonString);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onBackPressed();
                }
            });
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

        int chapterNumber = Integer.parseInt(mainChapter.getNumber());
//        String languageName = mainChapter.language;
        String nextChapterNumber = Integer.toString(chapterNumber + 1);
        if(nextChapterNumber.length() == 1){
            nextChapterNumber = "0" + nextChapterNumber;
        }

        List<StoriesChapter> chapters = mainChapter.getBook().getStoryChapters();

        StoriesChapter nextChapter = null;
        int newChapterNumber = Integer.parseInt(this.mainChapter.getNumber());
        for(StoriesChapter chapter : chapters){
            if(Integer.parseInt(chapter.getNumber()) == newChapterNumber + 1){
                nextChapter = chapter;
                break;
            }
        }

        this.mainChapter = nextChapter;
        getCount();

//        int current_value = Integer.parseInt(mainChapter.getNumber());
        UWPreferenceManager.setSelectedStoryPage(context, nextChapter.getStoryPages().get(0).getId());
        notifyDataSetChanged();
        ((ViewPager) this.container).setCurrentItem(0);
        context.getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    public void setIsLandscape(boolean isLandscape){
        this.isLandscape = isLandscape;
        notifyDataSetChanged();
    }
}
