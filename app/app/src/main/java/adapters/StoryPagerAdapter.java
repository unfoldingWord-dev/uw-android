package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import activity.reading.BaseReadingActivity;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import utils.AsyncImageLoader;
import utils.UWPreferenceManager;
import view.ASyncImageView;
import view.ViewContentHelper;

/**
 * Created by Acts Media Inc on 5/12/14.
 * Adapter for OBS pages
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
    private int textSize;

    //region setup
    public StoryPagerAdapter(Activity context, StoriesChapter mainChapter, StoriesChapter secondChapter, int textSize) {
        this.mainChapter = mainChapter;
        this.secondChapter = secondChapter;
        this.textSize = textSize;
        if(mainChapter != null) {
            List<StoriesChapter> chapters = mainChapter.getBook().getStoryChapters();
            lastChapterNumber = Integer.parseInt(chapters.get(chapters.size() -1 ).getNumber());
        }
        this.context = context;
    }

    //region data finding methods

    private StoryPage getMainModelForRow(int index){
        return mainChapter.getStoryPages().get(index);
    }

    private StoryPage getSecondModelForRow(int index){
        return secondChapter.getStoryPages().get(index);
    }

    public StoriesChapter getMainChapter() {
        return mainChapter;
    }

    public void setTextSize(int textSize){
        this.textSize = textSize;
        notifyDataSetChanged();
    }
    //endregion

    //region pager methods

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
        View view = null;

        if (position == getCount() - 1) {
            view = getNextChapterView(inflater);
        }
        if(view == null){

            StoryPage currentMainPage = getMainModelForRow(position);
            StoryPage currentSecondPage = getSecondModelForRow(position);

            view = inflater.inflate(R.layout.stories_pager_layout, container, false);
            ASyncImageView chapterImageView = (ASyncImageView) view.findViewById(R.id.chapter_image_view);
            chapterImageView.setScaleType((isLandscape)? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.FIT_START);

            TextView mainTextView = (TextView) view.findViewById(R.id.story_main_text_view);
            TextView secondaryTextView = (TextView) view.findViewById(R.id.story_secondary_text_view);
            mainTextView.setTextSize((float) textSize);
            secondaryTextView.setTextSize((float) textSize);

            mainTextView.setText(currentMainPage.getText());
            secondaryTextView.setText(currentSecondPage.getText());
            String imgUrl = mainChapter.getStoryPages().get(position).getImageUrl();
            String lastBitFromUrl = AsyncImageLoader.getLastBitFromUrl(imgUrl);
            String path = lastBitFromUrl.replaceAll("[{//:}]", "");
            chapterImageView.setImageBitmap(ViewContentHelper.getBitmapFromAsset(context, "images/" + path));

            setupPageForDiglot(mainTextView, secondaryTextView);
        }
        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(mainChapter == null){
            return 0;
        }
        if(Integer.parseInt(mainChapter.getNumber()) == lastChapterNumber){
            return (mainChapter != null)? mainChapter.getStoryPages().size() : 0;
        }
        else {
            return (mainChapter != null) ? mainChapter.getStoryPages().size() + 1 : 0;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    private void setupPageForDiglot(TextView mainView, TextView secondView){
        secondView.setVisibility((isDiglot) ? View.VISIBLE : View.GONE);
    }

    //endregion

    //region screen changing

    public void update(StoriesChapter mainChapter, StoriesChapter secondChapter){
        this.mainChapter = mainChapter;
        this.secondChapter = secondChapter;
        notifyDataSetChanged();
    }

    /**
     * Changes the layout based on whether the user wants the diglot view
     * @param isDiglot true if the user currently wantds diglot
     */
    public void setIsDiglot(boolean isDiglot){
        this.isDiglot = isDiglot;
        notifyDataSetChanged();
    }



    //endregion

    //region handling next chapter

    private View getNextChapterView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.next_chapter_screen_layout, container, false);
        Button nextButton = (Button) view.findViewById(R.id.next_chapter_screen_button);

        if(Integer.parseInt(mainChapter.getNumber()) == lastChapterNumber){
            return null;
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

        StoriesChapter nextChapter = mainChapter.getNextChapter();
        if(nextChapter != null){
            mainChapter = nextChapter;
            getCount();

            UWPreferenceManager.setSelectedStoryPage(context, mainChapter.getStoryPages().get(0).getId());
            notifyDataSetChanged();
            ((ViewPager) this.container).setCurrentItem(0);
            context.getApplicationContext().sendBroadcast(new Intent(BaseReadingActivity.SCROLLED_PAGE));
        }
    }

    //endregion

    /**
     * Lays the views out based on the change to/from landscape
     * @param isLandscape true if the view will now be landscape
     */
    public void setIsLandscape(boolean isLandscape){
        this.isLandscape = isLandscape;
        notifyDataSetChanged();
    }
}
