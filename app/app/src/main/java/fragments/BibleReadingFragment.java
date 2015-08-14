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

import adapters.ReadingPagerAdapter;
import adapters.ReadingScrollNotifications;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Version;
import sideloading.SideLoadType;
import sideloading.SideSharer;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;
import view.ReadingBottomBarViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BibleReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BibleReadingFragment extends Fragment implements ReadingBottomBarViewGroup.BottomBarListener{

    private static final String IS_SECONDARY_PARAM = "IS_SECONDARY_PARAM";

    private boolean isSecondary;
    private ViewPager readingViewPager;
    private Book currentBook;

    private ReadingFragmentListener listener;
    private ReadingPagerAdapter adapter;

    private ReadingBottomBarViewGroup bottomBar;

    public static BibleReadingFragment newInstance(boolean isSecondary) {
        BibleReadingFragment fragment = new BibleReadingFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SECONDARY_PARAM, isSecondary);
        fragment.setArguments(args);
        return fragment;
    }

    public BibleReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loadBook();
            isSecondary = getArguments().getBoolean(IS_SECONDARY_PARAM);
        }

        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    private void loadBook(){
        BibleChapter currentItem = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        currentBook = currentItem.getBook();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bible_reading, container, false);
        setupViews(view);
        updateVersionInfo();

        return view;
    }

    public void update(){
        loadBook();
        updateVersionInfo();
        adapter.update(currentBook.getBibleChapters(true));
        scrollToCurrentPage();
    }

    private void setupViews(View view){
        bottomBar = new ReadingBottomBarViewGroup((RelativeLayout) view.findViewById(R.id.bottom_bar_layout), currentBook.getVersion(), this);
        setupPager(view);
    }

    private void updateVersionInfo(){
        this.bottomBar.updateWithVersion(currentBook.getVersion());
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

                if (position < adapter.getChapters().size()) {
                    BibleChapter model = adapter.getChapters().get(position);
                    UWPreferenceManager.changedToBibleChapter(getActivity().getApplicationContext(), model.getId(), isSecondary);
                    getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void scrollToCurrentPage(){

        BibleChapter currentItem = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        if(currentItem == null){
            return;
        }
        for(int i = 0; i < currentBook.getBibleChapters().size(); i++){
            if(currentItem.getId() == currentBook.getBibleChapters().get(i).getId()){
                readingViewPager.setCurrentItem(i, false);
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
                    case MotionEvent.ACTION_DOWN: {
                        touchDownMs = System.currentTimeMillis();
                        if ((numberOfTaps > 0)
                                && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                            return true;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
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

                        if (numberOfTaps == 2) {
                            if (listener != null) {
                                listener.toggleNavBar();
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        };
    }

    public void setBottomBarHidden(boolean hide){
        bottomBar.setHidden(hide);
    }

    @Override
    public void checkingLevelPressed() {
        listener.showCheckingLevel(currentBook.getVersion());
    }

    @Override
    public void shareButtonClicked() {
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
        Version currentVersion = currentBook.getVersion();
        sharer.startSharing(currentVersion.getAsPreloadJson(getActivity().getApplicationContext()).toString(),
                currentVersion.getName() + getActivity().getString(R.string.save_file_extension));
    }

    @Override
    public void versionButtonClicked() {
        listener.clickedChooseVersion(isSecondary);
    }
}
