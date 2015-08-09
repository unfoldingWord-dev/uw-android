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

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.ReadingPagerAdapter;
import adapters.ReadingScrollNotifications;
import adapters.StoryPagerAdapter;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import utils.UWPreferenceManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryReadingFragment extends Fragment {

    private static final String CHAPTER_PARAM = "CHAPTER_PARAM";

    private ViewPager readingViewPager;
    private StoriesChapter currentChapter;

    private ReadingFragmentListener listener;
    private StoryPagerAdapter adapter;

    public static StoryReadingFragment newInstance(StoriesChapter chapter) {
        StoryReadingFragment fragment = new StoryReadingFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHAPTER_PARAM, chapter);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentChapter = (StoriesChapter) getArguments().getSerializable(CHAPTER_PARAM);
        }

        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story_reading, container, false);
        setupViews(view);

        return view;
    }

    public void update(StoriesChapter chapter){
        this.currentChapter = chapter;
        adapter.update(chapter);
        readingViewPager.setCurrentItem(0);
    }

    private void setupViews(View view){
        setupPager(view);
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);
        adapter = new StoryPagerAdapter(getActivity().getApplicationContext(), currentChapter);

            readingViewPager.setAdapter(adapter);
            readingViewPager.setOnTouchListener(getDoubleTapTouchListener());

            readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    List<StoryPage> pages = adapter.getCurrentChapter().getStoryPages();
                if (position < pages.size()) {
                    StoryPage model = pages.get(position);
                    UWPreferenceManager.setSelectedStoryPage(getActivity().getApplicationContext(), model.getId());
                    getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
