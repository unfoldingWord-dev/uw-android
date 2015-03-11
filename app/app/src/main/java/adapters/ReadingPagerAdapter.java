package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import model.database.DBManager;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingPagerAdapter extends PagerAdapter {


    private static final String TAG = "ViewPagerAdapter";

    DisplayImageOptions options;
    View view = null;
    DBManager dbManager = null;
    private Intent intent;
    private Activity activity;
    private static Context context;
    private TextView actionbarTextView;
    private ViewGroup container;
    private ArrayList<BibleChapterModel> chapters;
    private VersionModel selectedVersion;

    public ReadingPagerAdapter(Object context, ArrayList<BibleChapterModel> models, TextView actionbarTextView, Intent intent) {
        this.context = (Context) context;
        Collections.sort(models);
        chapters = models;
        getCount();
        this.actionbarTextView = actionbarTextView;
        dbManager = DBManager.getInstance(this.context);
        this.activity = (Activity) context;
        this.intent = intent;
    }

    @Override
    public int getCount() {
        return chapters.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;

        // getting last row values of data base
        int frameCount = chapters.size();

        view = inflater.inflate(R.layout.reading_pager_layout, container, false);
        view.setEnabled(false);
        TextView storyTextView = (TextView) view.findViewById(R.id.chapterTextView);
        storyTextView.setText(Html.fromHtml(chapters.get(position).text));

        ((ViewPager) container).addView(view);

        manageActionbarText();
        return view;
    }

    private void manageActionbarText(){
        int index = ((ViewPager) container).getCurrentItem();
        actionbarTextView.setText(chapters.get(index).getTitle(context));
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        manageActionbarText();
    }
}
