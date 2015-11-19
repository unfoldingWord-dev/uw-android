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
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceDataAccessor;
import view.ReadingDoubleTapHandler;

/**
 * Created by PJ Fechner
 * Fragment to handle the display of the text
 */
public class BibleReadingFragment extends Fragment implements
        ReadingPagerAdapter.ReadingPagerAdapterListener, ReadingDoubleTapHandler.ReadingDoubleTapHandlerListener, UWPreferenceDataAccessor.PreferencesBibleChapterChangedListener{

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

        if(getActivity() instanceof ReadingFragmentListener){
            this.listener = (ReadingFragmentListener) getActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UWPreferenceDataAccessor.addBibleChapterListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UWPreferenceDataAccessor.removeBibleChapterListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bible_reading, container, false);
        setupViews(view);
        update();
        return view;
    }

    public void setTextSize(int textSize){
        this.textSize = textSize;
        adapter.updateTextSize(this.textSize);
    }

    /**
     * Loads the most recent data and updates the views.
     */
    public void update(){
        setupOrUpdatePager();
    }

    private void setupViews(View view){
        readingViewPager = (ViewPager) view.findViewById(R.id.myViewPager);
    }

    private void setupOrUpdatePager(){

        BibleChapter correctItem = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        if(correctItem != null){
            setupOrUpdatePager(correctItem);
        }

    }

    private void setupOrUpdatePager(BibleChapter correctItem){

        List<BibleChapter> chapters = new ArrayList<>();

        if(correctItem != null){
            chapters = correctItem.getBook().getBibleChapters(true);
        }
        if(adapter == null) {

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


        if(correctItem != null){

            if(adapter.getChapters().get(0).getBookId() != correctItem.getBookId()){
                adapter.update(chapters);
            }

            int desiredIndex = chapters.indexOf(correctItem);
            if(readingViewPager.getCurrentItem() != desiredIndex) {
                readingViewPager.setCurrentItem(chapters.indexOf(correctItem), true);
            }
        }
    }

    //endregion

    //region updating

    private void scrolled(int position){

        BibleChapter model = adapter.getChapters().get(position);

        BibleChapter currentModel = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        BibleChapter otherModel = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), !isSecondary);

        if (currentModel == null || otherModel == null) {
            return;
        }
        boolean needsUpdate = (!currentModel.getId().equals(model.getId()) && currentModel.getNumber().equals(otherModel.getNumber()));

        if (needsUpdate) {
            UWPreferenceDataAccessor.changeToNewBibleChapter(getActivity().getApplicationContext(), model, isSecondary);
        }
    }
    //endregion

    //region ReadingPagerAdapterListener

    @Override
    public void goToNextBook() {
        BibleChapter currentModel = UWPreferenceDataAccessor.getCurrentBibleChapter(getActivity().getApplicationContext(), isSecondary);
        if(currentModel != null){
            Book nextBook = currentModel.getBook().getNextBook();
            if(nextBook != null) {
                BibleChapter nextChapter = nextBook.getBibleChapters(true).get(0);
                UWPreferenceDataAccessor.changeToNewBibleChapter(getActivity().getApplicationContext(), nextChapter, isSecondary);
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
        }
        else{
            return true;
        }
    }

    @Override
    public void bibleChapterChanged(BibleChapter mainChapter, BibleChapter secondaryChapter) {
        setupOrUpdatePager((isSecondary)? secondaryChapter : mainChapter);
    }
}
