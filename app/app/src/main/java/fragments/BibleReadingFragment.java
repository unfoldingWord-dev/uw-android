package fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.UWBaseActivity;
import activity.reading.BaseReadingActivity;
import adapters.ReadingPagerAdapter;
import model.SharingHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceDataManager;
import view.ReadingBottomBarViewGroup;
import view.ReadingDoubleTapHandler;

/**
 * Created by PJ Fechner
 * Fragment to handle the display of the text
 */
public class BibleReadingFragment extends Fragment implements ReadingBottomBarViewGroup.BottomBarListener,
        ReadingPagerAdapter.ReadingPagerAdapterListener, ReadingDoubleTapHandler.ReadingDoubleTapHandlerListener {

//    private static final String TAG = "BibleReadingFragment";
    private static final String IS_SECONDARY_PARAM = "IS_SECONDARY_PARAM";

    private ReadingBottomBarViewGroup bottomBar;
    private ReadingPagerAdapter adapter;
    private ViewPager readingViewPager;

    private ReadingFragmentListener listener;

    private Book currentBook;
    private boolean isSecondary;

    //region setup

    /**
     * @param isSecondary true if this fragment is for display as the second fragment in the diglot view.
     * @return Constructs a new Bible Reading fragment.
     */
    public static BibleReadingFragment newInstance(boolean isSecondary) {
        BibleReadingFragment fragment = new BibleReadingFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SECONDARY_PARAM, isSecondary);
        fragment.setArguments(args);
        return fragment;
    }

    ///required extra constructor
    public BibleReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSecondary = getArguments().getBoolean(IS_SECONDARY_PARAM);
        updateBook();

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

    /**
     * Loads the most recent data and updates the views.
     */
    public void update(){

        if(updateBook()) {
            updateVersionInfo();
            adapter.update(currentBook.getBibleChapters(true));
        }
        scrollToCurrentPage();
    }

    private void setupViews(View view){
        bottomBar = new ReadingBottomBarViewGroup(getActivity(), (RelativeLayout) view.findViewById(R.id.bottom_bar_layout),
                currentBook.getVersion(), this);
        setupPager(view);
    }

    private void setupPager(View view){

        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);

        List<BibleChapter> chapters = currentBook.getBibleChapters(true);
        adapter = new ReadingPagerAdapter(getActivity().getApplicationContext(), chapters, new ReadingDoubleTapHandler(getResources(), this), this);

        readingViewPager.setAdapter(adapter);
        readingViewPager.setOnTouchListener(new ReadingDoubleTapHandler(getResources(), this));
        scrollToCurrentPage();
        readingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                boolean userDidScrollChapter = (state == 0);
                if (!userDidScrollChapter) {
                    return;
                }

                int position = readingViewPager.getCurrentItem();
                if (position < adapter.getChapters().size()) {
                    scrolled(position);
                }
            }
        });
    }

    //endregion

    //region updating

    private void updateVersionInfo(){
        this.bottomBar.updateWithVersion(currentBook.getVersion());
    }

    private void scrolled(int position){

        BibleChapter model = adapter.getChapters().get(position);

        BibleChapter currentModel = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        BibleChapter otherModel = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), !isSecondary);

        if (currentModel == null || otherModel == null) {
            return;
        }
        boolean needsUpdate = (!currentModel.getId().equals(model.getId()) && currentModel.getNumber().equals(otherModel.getNumber()));

        if (needsUpdate) {
            UWPreferenceDataManager.changedToBibleChapter(getActivity().getApplicationContext(), model.getId(), isSecondary);
            getActivity().getApplicationContext().sendBroadcast(new Intent(BaseReadingActivity.SCROLLED_PAGE));
        }
    }

    private boolean updateBook(){

        BibleChapter currentItem = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
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

    /**
     * updates the data and scrolls to the currently selected page
     */
    public void scrollToCurrentPage(){
        updateBook();
        BibleChapter correctItem = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
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

    /**
     * hides the bottom bar of the reading view
     * @param hide true if the bar should be hidden
     */
    public void setBottomBarHidden(boolean hide){
        bottomBar.setHidden(hide);
    }

    //endregion

    //region ReadingPagerAdapterListener

    @Override
    public void goToNextBook() {
        Book nextBook = currentBook.getNextBook();
        if(nextBook != null){
            BibleChapter nextChapter = nextBook.getBibleChapters(true).get(0);
            UWPreferenceDataManager.changedToBibleChapter(getActivity().getApplicationContext(), nextChapter.getId(), isSecondary);
            getActivity().getApplicationContext().sendBroadcast(new Intent(BaseReadingActivity.SCROLLED_PAGE));
        }
    }

    //endregion

    //region ReadingDoubleTapHandlerListener

    @Override
    public boolean doubleTapWasRegistered() {

        if (listener != null) {
            listener.toggleNavBar();
            return true;
        }
        else{
            return true;
        }
    }

    //endregion

    //region BottomBarListener

    @Override
    public void checkingLevelPressed() {
        listener.showCheckingLevel(currentBook.getVersion());
    }

    @Override
    public void shareButtonClicked() {

        ((UWBaseActivity)getActivity()).goToNewActivity(SharingHelper.getIntentForSharing(getContext(), currentBook.getVersion()));

//        SideSharer sharer = new SideSharer((UWBaseActivity) getActivity(), new SideSharer.SideLoaderListener() {
//            @Override
//            public void sideLoadingSucceeded(String response) {
//            }
//            @Override
//            public void sideLoadingFailed(String errorMessage) {
//            }
//            @Override
//            public boolean confirmSideLoadingType(SideLoadType type) {
//                return true;
//            }
//        });
//        Version currentVersion = currentBook.getVersion();
//        sharer.startSharing(currentVersion.getAsPreloadJson(getActivity().getApplicationContext()).toString(),
//                currentVersion.getName() + getActivity().getString(R.string.save_file_extension));
    }

    @Override
    public void versionButtonClicked() {
        listener.clickedChooseVersion(isSecondary);
    }

    //endregion
}
