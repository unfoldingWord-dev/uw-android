package fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.ReadingPagerAdapter;
import adapters.ReadingScrollNotifications;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceManager;
import view.ViewHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BibleReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BibleReadingFragment extends Fragment {

    private static final String BOOK_PARAM = "BOOK_PARAM";

    private ViewPager readingViewPager;
    private Book currentBook;

    private ReadingFragmentListener listener;
    private ReadingPagerAdapter adapter;

    private ImageButton checkingLevelButton;
    private TextView versionTextView;

    public static BibleReadingFragment newInstance(Book book) {
        BibleReadingFragment fragment = new BibleReadingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BOOK_PARAM, book);
        fragment.setArguments(args);
        return fragment;
    }

    public BibleReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentBook = (Book) getArguments().getSerializable(BOOK_PARAM);
        }

        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bible_reading, container, false);
        setupViews(view);
        updateVersionInfo();

        return view;
    }

    public void update(BibleChapter chapter){
        this.currentBook = chapter.getBook();
        updateVersionInfo();
        adapter.update(chapter.getBook().getBibleChapters(true));
        scrollToCurrentPage();
    }

    private void setupViews(View view){
        this.checkingLevelButton = (ImageButton) view.findViewById(R.id.bottom_bar_level_button);
        this.versionTextView = (TextView) view.findViewById(R.id.bottom_bar_left_button_text_view);

        checkingLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkingLevelPressed();
            }
        });

        view.findViewById(R.id.bottom_bar_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionButtonClicked();
            }
        });

        view.findViewById(R.id.bottom_bar_share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButtonClicked();
            }
        });


        setupPager(view);
    }

    private void updateVersionInfo(){

        this.versionTextView.setText(currentBook.getVersion().getName());
        this.checkingLevelButton.setImageResource(ViewHelper.getCheckingLevelImage(
                Integer.parseInt(currentBook.getVersion().getStatusCheckingLevel())));
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);

        List<BibleChapter> chapters = currentBook.getBibleChapters(true);
        adapter = new ReadingPagerAdapter(getActivity().getApplicationContext(), chapters,  getDoubleTapTouchListener());

        readingViewPager.setAdapter(adapter);
        readingViewPager.setOnTouchListener(getDoubleTapTouchListener());
        scrollToCurrentPage();
        readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position < adapter.getChapters().size()) {
                    BibleChapter model = adapter.getChapters().get(position);
                    UWPreferenceManager.setSelectedBibleChapter(getActivity().getApplicationContext(), model.getId());
                    getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void scrollToCurrentPage(){

        long currentItem = UWPreferenceManager.getSelectedBibleChapter(getActivity().getApplicationContext());
        for(int i = 0; i < currentBook.getBibleChapters().size(); i++){
            if(currentItem == currentBook.getBibleChapters().get(i).getId()){
                readingViewPager.setCurrentItem(i);
                return;
            }
        }
    }

    private View.OnTouchListener getDoubleTapTouchListener(){

        return new View.OnTouchListener() {
            Handler handler = new Handler();


            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            Resources res = getResources();
            int tapTimeout = res.getInteger(R.integer.tap_timeout);
            int doubleTapTimeout = res.getInteger(R.integer.double_tap_timeout);

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        if ((numberOfTaps > 0)
                                && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);

                        if ((System.currentTimeMillis() - touchDownMs) > tapTimeout) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if ((numberOfTaps > 0)
                                && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }

                        lastTapTimeMs = System.currentTimeMillis();

//                        if(numberOfTaps == 1){
//                            checkShouldChangeNavBarHidden();
//                            return false;
//                        }

                        if(numberOfTaps == 2){
                            if(listener != null) {
                                listener.toggleNavBar();
                                return true;
                            }
                        }

                }

                return false;
            }
        };
    }


    public void checkingLevelPressed() {
        listener.showCheckingLevel(currentBook.getVersion());
    }

    public void shareButtonClicked() {

    }

    public void versionButtonClicked() {
        listener.chooseVersion(false);
    }
}
