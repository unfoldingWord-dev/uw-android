package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import activity.bookSelection.ChapterSelectionActivity;
import model.database.DBManager;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.VersionModel;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingPagerAdapter extends PagerAdapter {


    private static final String TAG = "ViewPagerAdapter";

    protected String SELECTED_POS = "";

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

    public ReadingPagerAdapter(Object context, ArrayList<BibleChapterModel> models, TextView actionbarTextView, String positionHolder) {
        this.context = (Context) context;
        Collections.sort(models);
        chapters = models;
        getCount();
        this.actionbarTextView = actionbarTextView;
        dbManager = DBManager.getInstance(this.context);
        this.activity = (Activity) context;
        SELECTED_POS = positionHolder;
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
        WebView textWebView = (WebView) view.findViewById(R.id.chapterWebView);
        textWebView.getSettings().setJavaScriptEnabled(true);

        String pageText = getTextCss() + chapters.get(position).text;
        textWebView.loadDataWithBaseURL("", pageText, "text/html", "UTF-8", "");

        ((ViewPager) container).addView(view);

        manageActionbarText();
        return view;
    }

    private void manageActionbarText(){
        int index = ((ViewPager) container).getCurrentItem();
        actionbarTextView.setText(chapters.get(index).getTitle(context));
    }

    private String getTextCss(){



        String css = "<style type=\"text/css\">\n" +
                "<!--\n" +
                ".verse { font-size: 12pt}" +
                ".q, .q1, .q2 { margin:0; display: block; padding:0;}\n" +
                ".q, .q1 { margin-left: 1em; }\n" +
                ".q2 { margin-left: 2em; }\n" +
                "p { font-size: 15pt; line-height: 1.3; padding:10px; unicode-bidi:bidi-override; direction:" +
                getTextDirection() + " ;}\n" +
                "-->\n" +
                "</style>";
        return css;
    }

    private String getTextDirection(){
        String direction = chapters.get(0).getParent(context).getParent(context).readingDirection;

        if(direction.equalsIgnoreCase("rtl")){
            return "rtl";
        }
        else{
            return "ltf";
        }
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
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SELECTED_POS, position).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(ChapterSelectionActivity.CHAPTERS_INDEX_STRING, position).commit();
        manageActionbarText();
    }
}
