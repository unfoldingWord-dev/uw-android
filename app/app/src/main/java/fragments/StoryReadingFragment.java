package fragments;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.StoryPagerAdapter;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceDataManager;
import view.ReadingDoubleTapHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryReadingFragment extends Fragment implements ReadingDoubleTapHandler.ReadingDoubleTapHandlerListener{

    private static final String TEXT_SIZE_PARAM = "TEXT_SIZE_PARAM";

    private ViewPager readingViewPager;
    private StoriesChapter  mainChapter;
    private StoriesChapter secondChapter;

    private ReadingFragmentListener listener;
    private StoryPagerAdapter adapter;
//    private TabBar mainBottomBar;
//    private TabBar secondBottomBar;

    private RelativeLayout baseLayout;

    private int textSize;
    private View secondBarView;

    public static StoryReadingFragment newInstance(int textSize) {
        StoryReadingFragment fragment = new StoryReadingFragment();
        Bundle args = new Bundle();
        args.putInt(TEXT_SIZE_PARAM, textSize);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textSize = getArguments().getInt(TEXT_SIZE_PARAM);
        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    public void setTextSize(int textSize){
        this.textSize = textSize;
        adapter.setTextSize(this.textSize);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story_reading, container, false);
        baseLayout = (RelativeLayout) view.findViewById(R.id.story_reading_fragment_base_layout);
        updateData();
        setupViews(view);
        updateVersionInfo();
        setDiglotShowing(false);

        return view;
    }

    private void updateData(){
        if(getActivity() != null) {
            StoryPage mainPage = UWPreferenceDataAccessor.getCurrentStoryPage(getActivity().getApplicationContext(), false);
            StoryPage secondaryPage = UWPreferenceDataAccessor.getCurrentStoryPage(getActivity().getApplicationContext(), true);

            if (mainPage != null) {
                mainChapter = mainPage.getStoriesChapter();
            }

            if (secondaryPage != null) {
                secondChapter = secondaryPage.getStoriesChapter();
            }
        }
    }

    public void update(){
        updateData();
        updateVersionInfo();
        adapter.update(mainChapter, secondChapter);
        StoryPage page = UWPreferenceDataAccessor.getCurrentStoryPage(getActivity().getApplicationContext(), false);
        if(page != null) {
            int currentIndex = page.getStoriesChapter().getStoryPages().indexOf(page);
            readingViewPager.setCurrentItem(currentIndex);
        }
    }

    private void setupViews(View view){
        setupBottomBars(view);
        setupPager(view);
//        secondBarView = view.findViewById(R.id.story_bottom_bar_second_layout);
        setBottomBarsDiglot(false);
        adapter.setIsLandscape(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private void updateVersionInfo(){

//        if(mainChapter != null && secondChapter != null){
//            this.mainBottomBar.updateWithVersion(mainChapter.getBook().getVersion());
//            this.secondBottomBar.updateWithVersion(secondChapter.getBook().getVersion());
//        }
    }

    private void setupBottomBars(View view){

        Version mainVersion = (mainChapter != null)? mainChapter.getBook().getVersion() : null;

//        mainBottomBar = new TabBar(getActivity(), (RelativeLayout) view.findViewById(R.id.story_bottom_bar_main_layout),
//                mainVersion, new TabBar.BottomBarListener() {
//            @Override
//            public void checkingLevelPressed() {
//                listener.showCheckingLevel(mainChapter.getBook().getVersion());
//            }
//
//            @Override
//            public void versionButtonClicked() {
//                listener.clickedChooseVersion(false);
//            }
//
//            @Override
//            public void shareButtonClicked() {
//                shareVersion(mainChapter.getBook().getVersion());
//            }
//        });

        Version secondVersion = (secondChapter != null)? secondChapter.getBook().getVersion() : null;
//        secondBottomBar = new TabBar(getActivity(), (RelativeLayout) view.findViewById(R.id.story_bottom_bar_second_layout),
//                secondVersion, new TabBar.BottomBarListener() {
//            @Override
//            public void checkingLevelPressed() {
//                listener.showCheckingLevel(secondChapter.getBook().getVersion());
//            }
//
//            @Override
//            public void versionButtonClicked() {
//                listener.clickedChooseVersion(true);
//            }
//
//            @Override
//            public void shareButtonClicked() {
//                shareVersion(secondChapter.getBook().getVersion());
//            }
//        });
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);
        adapter = new StoryPagerAdapter(getActivity(), mainChapter, secondChapter, textSize);

            readingViewPager.setAdapter(adapter);
            readingViewPager.setOnTouchListener(new ReadingDoubleTapHandler(getResources(), this));

            readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    if(state != 0){
                        return;
                    }
                    int position = readingViewPager.getCurrentItem();
                    List<StoryPage> pages = adapter.getMainChapter().getStoryPages();

                    if (position < pages.size()) {
                        StoryPage model = pages.get(position);
                        UWPreferenceDataManager.setNewStoriesPage(getActivity().getApplicationContext(), model, false);
//                        getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
                    }
                }
            });
    }

    @Override
    public boolean doubleTapWasRegistered() {

        if(listener != null) {
            listener.toggleHidden();
            return true;
        }
        else {
            return false;
        }
    }

    public void setDiglotShowing(boolean showing){

        if(adapter != null) {
            adapter.setIsDiglot(showing);
        }
        setBottomBarsDiglot(showing);
    }

    public void setBottomBarsDiglot(boolean showing){
//        secondBarView.findViewById(R.id.story_bottom_bar_second_layout).setVisibility((showing) ? View.VISIBLE : View.GONE);
    }

    public void setBottomBarHidden(boolean hide){

//        LinearLayout layout = (LinearLayout) baseLayout.findViewById(R.id.stories_bottom_bar_layout);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
//        if(hide){
//            params.addRule(RelativeLayout.BELOW, R.id.bottom_marker_layout);
//        }
//        else{
//            params.removeRule(RelativeLayout.BELOW);
//        }
//
//        layout.setLayoutParams(params);
    }

    public void setOrientationAsLandscape(boolean isLandscape){
        adapter.setIsLandscape(isLandscape);
    }
}
