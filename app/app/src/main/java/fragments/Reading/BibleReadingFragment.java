/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package fragments.Reading;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import adapters.ReadingPagerAdapter;
import de.greenrobot.event.EventBus;
import eventbusmodels.BiblePagingEvent;
import eventbusmodels.StoriesPagingEvent;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceDataAccessor;
import view.ReadingDoubleTapHandler;

/**
 * Created by PJ Fechner
 * Fragment to handle the display of the text
 */
public class BibleReadingFragment extends Fragment implements
        ReadingPagerAdapter.ReadingPagerAdapterListener, ReadingDoubleTapHandler.ReadingDoubleTapHandlerListener {

    //    private static final String TAG = "BibleReadingFragment";
    private static final String IS_SECONDARY_PARAM = "IS_SECONDARY_PARAM";
    private static final String TEXT_SIZE_PARAM = "TEXT_SIZE_PARAM";

    private ReadingPagerAdapter adapter;
    private ViewPager readingViewPager;
    private ReadingFragmentListener listener;

    private boolean isSecondary;
    private int textSize;

    //region setup

    /**
     * @param isSecondary true if this fragment is for display as the second fragment in the diglot view.
     * @return Constructs a new Bible Reading fragment.
     */
    public static BibleReadingFragment newInstance(boolean isSecondary, int textSize) {
        BibleReadingFragment fragment = new BibleReadingFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SECONDARY_PARAM, isSecondary);
        args.putInt(TEXT_SIZE_PARAM, textSize);
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
        textSize = getArguments().getInt(TEXT_SIZE_PARAM);

        if (getActivity() instanceof ReadingFragmentListener) {
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEventListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterEventListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bible_reading, container, false);
        setupViews(view);
        update();
        return view;
    }

    private void registerEventListeners() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventListeners() {
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BiblePagingEvent event) {

        setupOrUpdatePager((isSecondary)? event.secondaryChapter : event.mainChapter);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        adapter.updateTextSize(this.textSize);
    }

    /**
     * Loads the most recent data and updates the views.
     */
    public void update() {
        setupOrUpdatePager();
    }

    private void setupViews(View view) {
        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);
    }

    private void setupOrUpdatePager() {

        BiblePagingEvent event = getPagingEvent();
        BibleChapter correctItem = (isSecondary) ? event.secondaryChapter : event.mainChapter;
        if (correctItem != null) {
            setupOrUpdatePager(correctItem);
        }
    }

    private BiblePagingEvent getPagingEvent() {
        return BiblePagingEvent.getStickyEvent(getActivity().getApplicationContext());
    }

    private void setupOrUpdatePager(BibleChapter correctItem) {

        List<BibleChapter> chapters = new ArrayList<>();

        if (correctItem != null) {
            chapters = correctItem.getBook().getBibleChapters(true);
        }
        if (adapter == null) {

            adapter = new ReadingPagerAdapter(getActivity().getApplicationContext(), chapters,
                    new ReadingDoubleTapHandler(getResources(), this), textSize, this);

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


        if (correctItem != null) {

            if (adapter.getChapters().get(0).getBookId() != correctItem.getBookId()) {
                adapter.update(chapters);
            }

            int desiredIndex = chapters.indexOf(correctItem);
            if (readingViewPager.getCurrentItem() != desiredIndex) {
                readingViewPager.setCurrentItem(chapters.indexOf(correctItem), true);
            }
        }
    }

    //endregion

    //region updating

    private void scrolled(int position) {

        BiblePagingEvent event = getPagingEvent();
        BibleChapter newModel = adapter.getChapters().get(position);

        BibleChapter currentModel = (isSecondary) ? event.secondaryChapter : event.mainChapter;
        BibleChapter otherModel = (isSecondary) ? event.mainChapter : event.secondaryChapter;

        if (currentModel == null || otherModel == null) {
            return;
        }
        boolean needsUpdate = (!currentModel.getId().equals(newModel.getId()) && currentModel.getNumber().equals(otherModel.getNumber()));

        if (needsUpdate) {

            BibleChapter newMainModel = (isSecondary) ? otherModel.getBook().getBibleChapterForNumber(newModel.getNumber()) : newModel;
            BibleChapter newSecondaryModel = (isSecondary) ? newModel : otherModel.getBook().getBibleChapterForNumber(newModel.getNumber());
            // post even if the model has changed
            EventBus.getDefault().postSticky(new BiblePagingEvent(newMainModel, newSecondaryModel));
        }
    }
    //endregion

    //region ReadingPagerAdapterListener

    @Override
    public void goToNextBook() {

        BiblePagingEvent event = getPagingEvent();

        BibleChapter currentModel = (isSecondary) ? event.secondaryChapter : event.mainChapter;
        BibleChapter otherModel = (isSecondary) ? event.mainChapter : event.secondaryChapter;
        if (currentModel != null) {
            Book nextBook = currentModel.getBook().getNextBook();
            if (nextBook != null) {
                BibleChapter nextChapter = nextBook.getBibleChapters(true).get(0);
                Book otherNextBook = otherModel.getBook().getNextBook();
                BibleChapter newOtherNextChapter = otherModel;
                if (otherNextBook != null && otherNextBook.getSlug().equals(nextBook.getSlug())) {
                    newOtherNextChapter = otherNextBook.getBibleChapters(true).get(0);
                }
                BibleChapter mainChapter = (isSecondary) ? newOtherNextChapter : nextChapter;
                BibleChapter secondaryChapter = (isSecondary) ? nextChapter : newOtherNextChapter;
                EventBus.getDefault().postSticky(new BiblePagingEvent(mainChapter, secondaryChapter));
            }
        }
    }

    //endregion

    //region ReadingDoubleTapHandlerListener

    @Override
    public boolean doubleTapWasRegistered() {

        if (listener != null) {
            listener.toggleHidden();
            return true;
        } else {
            return true;
        }
    }
}
