package adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import java.util.List;

import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.parsers.USFMParser;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingPagerAdapter extends PagerAdapter {

    private static final String TAG = "ViewPagerAdapter";

    private Context context;
    private ViewGroup container;
    private List<BibleChapter> chapters;
    private View.OnTouchListener pagerOnTouchListener;
    private int textSize;

    private ReadingPagerAdapterListener listener;

    //region Setup

    public ReadingPagerAdapter(Context context, List<BibleChapter> models, View.OnTouchListener pagerOnTouchListener,
                               int textSize, ReadingPagerAdapterListener listener) {
        this.context = context;
        this.textSize = textSize;
        this.listener = listener;
        chapters = models;
        this.pagerOnTouchListener = pagerOnTouchListener;
    }

    public void update(List<BibleChapter> models){
        this.chapters = models;
        notifyDataSetChanged();
    }

    public void updateTextSize(int textSize){
        this.textSize = textSize;
        notifyDataSetChanged();
    }

    //endregion

    //region Accessors

    public List<BibleChapter> getChapters() {
        return chapters;
    }

    //endregion

    //region pager methods

    @Override
    public int getCount() {
        Book nextBook = chapters.get(0).getBook().getNextBook();

        if(nextBook == null){
            return chapters.size();
        }
        else {
            return chapters.size() + 1;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
        View view = null;

        if (position == chapters.size()) {
            view = getNextBookView(inflater);
        }
        else{

            view = inflater.inflate(R.layout.reading_pager_layout, container, false);
            view.setEnabled(false);
            WebView textWebView = (WebView) view.findViewById(R.id.chapterWebView);
            textWebView.getSettings().setJavaScriptEnabled(true);

            textWebView.setOnTouchListener(this.pagerOnTouchListener);
            String pageText = getTextCss() + new USFMParser().parseUsfmChapter(chapters.get(position).getText());
            textWebView.loadDataWithBaseURL("", pageText, "text/html", "UTF-8", "");
        }

        ((ViewPager) container).addView(view);
        return view;
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

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if(position == (this.getCount() - 1)){
            return;
        }
    }

    //endregion

    //region text creation

    private String getTextCss(){

        String css = "<style type=\"text/css\">\n" +
                ".selah {text-align: right; font-style: italic; float: right;}\n" +
                ".verse { font-size: 9pt}\n" +
                ".d {font-style: italic; text-align: center; padding: 0px; line-height: 0.9;}\n" +
                ".q, .q1, .q2 { margin:0; display: block; padding:0;}\n" +
                ".q, .q1 { padding-left: 1em; }\n" +
                ".q2 { padding-left: 2em; }\n" +
                ".q3 { padding-left: 3em; }\n" +
                "p { width:96%; font-size: " + Integer.toString(textSize) + "pt; text-align: justify; line-height: 1.3; padding:5px; unicode-bidi:bidi-override; direction:" +
                getTextDirection() + " ;}\n" +
                ".footnote {font-size: 11pt;}\n" +
                "sup {font-size: 9pt;}\n" +
                "</style>\n";
        return css;
    }

    private String getTextDirection(){

        if(isRTL()){
            return "rtl";
        }
        else{
            return "ltf";
        }
    }

    private boolean isRTL() {
        char desiredChar = ' ';

        int spanIndex = chapters.get(0).getText().indexOf("span>");
        for(int i = spanIndex + 5; i < chapters.get(0).getText().length(); i++){
            desiredChar = chapters.get(0).getText().charAt(i);

            if(!Character.isDigit(desiredChar) && !Character.isWhitespace(desiredChar) && Character.isLetter(desiredChar)){
                break;
            }
        }

        final int directionality = Character.getDirectionality(desiredChar);
        boolean direction = (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
            directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC);

        return direction;
    }

    //endregion


    //region handling next book

    private View getNextBookView(LayoutInflater inflater){

        View nextChapterView = inflater.inflate(R.layout.next_chapter_screen_layout, container, false);
        Button nextButton = (Button) nextChapterView.findViewById(R.id.next_chapter_screen_button);

        Book nextBook = chapters.get(0).getBook().getNextBook();
        if(nextBook != null) {
            String nextButtonString = context.getResources().getString(R.string.next_book);
            nextButton.setText(nextButtonString + " " + nextBook.getTitle());
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToNextBook();
                }
            });
            return nextChapterView;
        }
        else{
            return null;
        }
    }

    private void moveToNextBook(){

        if(listener != null) {
            listener.goToNextBook();
        }
    }

    //endregion

    public interface ReadingPagerAdapterListener{

        /**
         * User wants to go to the next book
         */
        void goToNextBook();
    }
}
