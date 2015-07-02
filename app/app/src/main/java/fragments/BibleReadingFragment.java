package fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.ReadingPagerAdapter;
import model.daoModels.BibleChapter;
import model.daoModels.Book;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BibleReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BibleReadingFragment extends Fragment {

    public interface BibleReadingFragmentListener{
        void toggleNavBar();
    }

    private static final String BOOK_PARAM = "BOOK_PARAM";

    private ViewPager readingViewPager;
    private Book currentBook;

    private BibleReadingFragmentListener listener;
    private ReadingPagerAdapter adapter;

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

        if(getActivity() instanceof BibleReadingFragmentListener){
            this.listener = (BibleReadingFragmentListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bible_reading, container, false);
        setupViews(view);

        return view;
    }

    public void updateReadingFragment(Book book){
        this.currentBook = book;
        adapter.update(book.getBibleChapters());
    }

    private void setupViews(View view){
        setupPager(view);
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);

        List<BibleChapter> chapters = currentBook.getBibleChapters();
        adapter = new ReadingPagerAdapter(getActivity().getApplicationContext(), chapters,  getDoubleTapTouchListener());

        readingViewPager.setAdapter(adapter);
        readingViewPager.setOnTouchListener(getDoubleTapTouchListener());

//        int currentItem = Integer.parseInt(currentChapter.getNumber().replaceAll("[^0-9]", "")) - 1;
//        readingViewPager.setCurrentItem(currentItem);
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


}
