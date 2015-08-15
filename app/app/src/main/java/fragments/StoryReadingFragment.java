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
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.ReadingScrollNotifications;
import adapters.StoryPagerAdapter;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import sideloading.SideLoadType;
import sideloading.SideSharer;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;
import view.ReadingBottomBarViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryReadingFragment extends Fragment{

    private ViewPager readingViewPager;
    private StoriesChapter mainChapter;
    private StoriesChapter secondChapter;

    private ReadingFragmentListener listener;
    private StoryPagerAdapter adapter;
    private ReadingBottomBarViewGroup mainBottomBar;
    private ReadingBottomBarViewGroup secondBottomBar;

    public static StoryReadingFragment newInstance() {
        StoryReadingFragment fragment = new StoryReadingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StoryReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story_reading, container, false);
        updateData();
        setupViews(view);
        updateVersionInfo();

        return view;
    }

    private void updateData(){
        StoryPage mainPage = UWPreferenceDataManager.getCurrentStoryPage(getActivity().getApplicationContext(), false);
        StoryPage secondaryPage = UWPreferenceDataManager.getCurrentStoryPage(getActivity().getApplicationContext(), true);

        if(mainPage != null){
            mainChapter = mainPage.getStoriesChapter();
        }

        if(secondaryPage != null){
            secondChapter = secondaryPage.getStoriesChapter();
        }
    }


    public void update(){
        updateData();
        updateVersionInfo();
        adapter.update(mainChapter, secondChapter);
    }

    private void setupViews(View view){
        setupBottomBars(view);
        setupPager(view);
        setBottomBarsDiglot(false);
    }

    private void updateVersionInfo(){
        this.mainBottomBar.updateWithVersion(mainChapter.getBook().getVersion());
    }

    private void setupBottomBars(View view){
        mainBottomBar = new ReadingBottomBarViewGroup(getActivity(), (RelativeLayout) view.findViewById(R.id.story_bottom_bar_main_layout),
                mainChapter.getBook().getVersion(), new ReadingBottomBarViewGroup.BottomBarListener() {
            @Override
            public void checkingLevelPressed() {
                listener.showCheckingLevel(mainChapter.getBook().getVersion());
            }

            @Override
            public void versionButtonClicked() {
                shareVersion(mainChapter.getBook().getVersion());
            }

            @Override
            public void shareButtonClicked() {
                listener.clickedChooseVersion(false);
            }
        });

        secondBottomBar = new ReadingBottomBarViewGroup(getActivity(), (RelativeLayout) view.findViewById(R.id.story_bottom_bar_second_layout),
                secondChapter.getBook().getVersion(), new ReadingBottomBarViewGroup.BottomBarListener() {
            @Override
            public void checkingLevelPressed() {
                listener.showCheckingLevel(secondChapter.getBook().getVersion());
            }

            @Override
            public void versionButtonClicked() {
                shareVersion(secondChapter.getBook().getVersion());
            }

            @Override
            public void shareButtonClicked() {
                listener.clickedChooseVersion(true);
            }
        });
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);
        adapter = new StoryPagerAdapter(getActivity().getApplicationContext(), mainChapter, secondChapter);

            readingViewPager.setAdapter(adapter);
            readingViewPager.setOnTouchListener(getDoubleTapTouchListener());

            readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    List<StoryPage> pages = adapter.getMainChapter().getStoryPages();
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
                                mainBottomBar.toggleHidden();
                                return true;
                            }
                        }

                }

                return false;
            }
        };
    }

    public void shareVersion(Version version) {

        SideSharer sharer = new SideSharer(getActivity(), new SideSharer.SideLoaderListener() {
            @Override
            public void sideLoadingSucceeded(String response) {
            }
            @Override
            public void sideLoadingFailed(String errorMessage) {
            }
            @Override
            public boolean confirmSideLoadingType(SideLoadType type) {
                return true;
            }
        });
        sharer.startSharing(version.getAsPreloadJson(getActivity().getApplicationContext()).toString(),
                version.getName() + getActivity().getString(R.string.save_file_extension));
    }

    public void setDiglotShowing(boolean showing){
        adapter.setIsDiglot(showing);
    }

    public void setBottomBarsDiglot(boolean showing){
        View view = getView();
        if(view != null) {
            view.findViewById(R.id.story_bottom_bar_second_layout).setVisibility((showing) ? View.VISIBLE : View.GONE);
        }
    }

    public void setBottomBarHidden(boolean hide){
        mainBottomBar.setHidden(hide);
        secondBottomBar.setHidden(hide);
    }
}
