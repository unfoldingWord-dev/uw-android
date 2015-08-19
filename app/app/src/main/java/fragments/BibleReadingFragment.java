package fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.UWBaseActivity;
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
public class BibleReadingFragment extends Fragment implements ReadingBottomBarViewGroup.BottomBarListener, ReadingPagerAdapter.ReadingPagerAdapterListener{

    private static final String TAG = "BibleReadingFragment";
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
            isSecondary = getArguments().getBoolean(IS_SECONDARY_PARAM);
            updateBook();
        }

        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bible_reading, container, false);
        updateBook();
        if(currentBook != null){
            setupViews(view);
            updateVersionInfo();
        }

        return view;
    }

    public void update(){

        if(updateBook()) {
            updateVersionInfo();
            adapter.update(currentBook.getBibleChapters(true));
        }
        scrollToCurrentPage();
    }

    private void setupViews(View view){
        if(currentBook != null) {
            bottomBar = new ReadingBottomBarViewGroup(getActivity(), (RelativeLayout) view.findViewById(R.id.bottom_bar_layout), currentBook.getVersion(), this);
            setupPager(view);
        }
    }

    private void updateVersionInfo(){
        this.bottomBar.updateWithVersion(currentBook.getVersion());
    }

    @Override
    public void goToNextBook() {
        Book nextBook = currentBook.getNextBook();
        if(nextBook != null){
            BibleChapter nextChapter = nextBook.getBibleChapters(true).get(0);
            UWPreferenceManager.changedToBibleChapter(getActivity().getApplicationContext(), nextChapter.getId(), isSecondary);
            getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
        }
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);

        List<BibleChapter> chapters = currentBook.getBibleChapters(true);
        adapter = new ReadingPagerAdapter(getActivity().getApplicationContext(), chapters, getDoubleTapTouchListener(), this);

        readingViewPager.setAdapter(adapter);
        readingViewPager.setOnTouchListener(getDoubleTapTouchListener());
        scrollToCurrentPage();
        readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                if (position < adapter.getChapters().size()) {
//                    BibleChapter model = adapter.getChapters().get(position);
//
//                    BibleChapter currentModel = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
//                    BibleChapter otherModel = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), !isSecondary);
//                    if(currentModel == null || otherModel == null){
//                        return;
//                    }
//                    boolean needsUpdate = (!currentModel.getId().equals(model.getId()) && currentModel.getNumber().equals(otherModel.getNumber())) ;
//
//                     if(needsUpdate) {
//
////                        Log.i(TAG, "will scroll from: " + currentModel.getNumber() +" to: " + model.getNumber() + " IsSecondary: " + isSecondary);
//                        UWPreferenceManager.changedToBibleChapter(getActivity().getApplicationContext(), model.getId(), isSecondary);
//                        getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
//                    }
////                    else{
////                         Log.i(TAG, "won't scroll from: " + currentModel.getNumber() +" to: " + model.getNumber() + " IsSecondary: " + isSecondary);
////                     }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state != 0){
                    return;
                }

                int position = readingViewPager.getCurrentItem();
                if (position < adapter.getChapters().size()) {
                    BibleChapter model = adapter.getChapters().get(position);

                    BibleChapter currentModel = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
                    BibleChapter otherModel = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), !isSecondary);
                    if (currentModel == null || otherModel == null) {
                        return;
                    }
                    boolean needsUpdate = (!currentModel.getId().equals(model.getId()) && currentModel.getNumber().equals(otherModel.getNumber()));

                    if (needsUpdate) {

//                        Log.i(TAG, "will scroll from: " + currentModel.getNumber() +" to: " + model.getNumber() + " IsSecondary: " + isSecondary);
                        UWPreferenceManager.changedToBibleChapter(getActivity().getApplicationContext(), model.getId(), isSecondary);
                        getActivity().getApplicationContext().sendBroadcast(new Intent(ReadingScrollNotifications.SCROLLED_PAGE));
                    }
//                    else{
//                         Log.i(TAG, "won't scroll from: " + currentModel.getNumber() +" to: " + model.getNumber() + " IsSecondary: " + isSecondary);
//                     }
                }
                else{
//                    goToNextBook();
                }
            }
        });
    }

    private boolean updateBook(){

        BibleChapter currentItem = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        if(currentItem != null) {
            boolean needsToUpdateBook = (this.currentBook != null && !currentBook.getId().equals(currentItem.getBookId()));
            this.currentBook = currentItem.getBook();
            if(needsToUpdateBook){
                adapter.update(currentBook.getBibleChapters());
                readingViewPager.setCurrentItem(0, false);
            }
            return needsToUpdateBook;
        }
        return false;
    }

    public void scrollToCurrentPage(){
        updateBook();
        BibleChapter correctItem = UWPreferenceDataManager.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        BibleChapter currentItem = currentBook.getBibleChapters(true).get(readingViewPager.getCurrentItem());

        boolean shouldUpdate = (correctItem != null && currentItem != null && !correctItem.getId().equals(currentItem.getId()));

        if(shouldUpdate) {

            for (int i = 0; i < currentBook.getBibleChapters().size(); i++) {
                if (correctItem.getId().equals(currentBook.getBibleChapters().get(i).getId())) {
                    readingViewPager.setCurrentItem(i, true);
                    return;
                }
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
        SideSharer sharer = new SideSharer((UWBaseActivity) getActivity(), new SideSharer.SideLoaderListener() {
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
