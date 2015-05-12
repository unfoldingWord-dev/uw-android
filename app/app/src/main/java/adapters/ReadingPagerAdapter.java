package adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import activity.reading.ReadingActivity;
import model.database.DBManager;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
import utils.USFMParser;
import utils.UWPreferenceManager;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingPagerAdapter extends PagerAdapter {

    private static final String TAG = "ViewPagerAdapter";
    protected String SELECTED_POS = "";

    DBManager dbManager = null;
    private Activity context;
    private TextView chaptersText;
    private ViewGroup container;
    private ArrayList<BibleChapterModel> chapters;
    private View.OnTouchListener pagerOnTouchListener;

    private BookModel nextBook;

    public ReadingPagerAdapter(Object context, ArrayList<BibleChapterModel> models, TextView chaptersText, String positionHolder, View.OnTouchListener pagerOnTouchListener) {
        this.context = (Activity) context;
        Collections.sort(models);
        chapters = models;
        getCount();
        this.chaptersText = chaptersText;
        dbManager = DBManager.getInstance(this.context);
        SELECTED_POS = positionHolder;
        this.pagerOnTouchListener = pagerOnTouchListener;
        setNextBook();
    }

    public void setNextBook(){

        ArrayList<BookModel> versionBooks = chapters.get(0).getParent(context).getParent(context).getChildModels(context);

        long currentBookId = chapters.get(0).getParent(context).uid;
        int currentIndex = -1;

        for(int i = 0; i < versionBooks.size(); i++){

            BookModel book = versionBooks.get(i);
            if(book.uid == currentBookId){
                currentIndex = i + 1;
                break;
            }
        }

        if(currentIndex < 0 || currentIndex >= versionBooks.size()){
            currentIndex = 0;
        }

        BookModel newBook = versionBooks.get(currentIndex);
        if(newBook.getBibleChildModels(context) == null || newBook.getBibleChildModels(context).size() == 0){
            newBook = versionBooks.get(currentIndex + 1);
        }
        this.nextBook = newBook;
    }

    @Override
    public int getCount() {
        return chapters.size() + 1;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
        View view = null;

        if (position == getCount() - 1) {
            view = getNextBookView(inflater);
            ((ViewPager) container).addView(view);

        } else {

            view = inflater.inflate(R.layout.reading_pager_layout, container, false);
            view.setEnabled(false);
            WebView textWebView = (WebView) view.findViewById(R.id.chapterWebView);
            textWebView.getSettings().setJavaScriptEnabled(true);

            String pageText = getTextCss() + USFMParser.getInstance().parseUsfmChapter(chapters.get(position).text);
            textWebView.loadDataWithBaseURL("", pageText, "text/html", "UTF-8", "");
            textWebView.setOnTouchListener(this.pagerOnTouchListener);

            ((ViewPager) container).addView(view);

            manageActionbarText();
        }
        return view;
    }

    private void manageActionbarText(){
        int index = ((ViewPager) container).getCurrentItem();
        if(index < chapters.size()) {
            String title = chapters.get(index).getTitle(context);
            chaptersText.setText(title);
        }
    }

    private String getTextCss(){

        String css = "<style type=\"text/css\">\n" +
                "<!--\n" +
                ".verse { font-size: 10pt}" +
                ".q, .q1, .q2 { margin:0; display: block; padding:0;}\n" +
                ".q, .q1 { margin-left: 1em; }\n" +
                ".q2 { margin-left: 2em; }\n" +
                "p { width:96%; font-size: 15pt; text-align: justify; line-height: 1.5; padding:10px; unicode-bidi:bidi-override; direction:" +
                getTextDirection() + " ;}\n" +
                "-->\n" +
                ".footnote {font-size: 12pt;}" +
                "sup {font-size: 9pt;}" +
                "</style>";
        return css;
    }

    private String getTextDirection(){

        String language = chapters.get(0).getParent(context).getParent(context).getParent(context).languageAbbreviation;

        if(isRTL()){
            return "rtl";
        }
        else{
            return "ltf";
        }
    }

    private boolean isRTL() {
        char desiredChar = ' ';

        int spanIndex = chapters.get(0).text.indexOf("span>");
        for(int i = spanIndex + 5; i < chapters.get(0).text.length(); i++){
            desiredChar = chapters.get(0).text.charAt(i);

            if(!Character.isDigit(desiredChar) && !Character.isWhitespace(desiredChar) && Character.isLetter(desiredChar)){
                break;
            }
        }

        final int directionality = Character.getDirectionality(desiredChar);
        boolean direction = (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
            directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC);

        return direction;
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

        BibleChapterModel model = chapters.get(position);
        UWPreferenceManager.setSelectedBibleChapter(context, model.uid);
        manageActionbarText();
    }

    private View getNextBookView(LayoutInflater inflater){

        View nextChapterView = inflater.inflate(R.layout.next_chapter_screen_layout, container, false);
        Button nextButton = (Button) nextChapterView.findViewById(R.id.next_chapter_screen_button);

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

    private void moveToNextBook(){

        this.chapters = nextBook.getBibleChildModels(context);
        setNextBook();

        UWPreferenceManager.setSelectedBibleChapter(context, chapters.get(0).uid);

        String title = chapters.get(0).getTitle(context);
        chaptersText.setText(title);

        notifyDataSetChanged();

        ((ViewPager) this.container).setCurrentItem(0);

    }
}
