package fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import adapters.selectionAdapters.ChaptersAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceDataAccessor;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;

/**
 * Fragment used for a user to select a BibleChapter from a list
 */
public class ChaptersFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ChapterSelectionFragmentListener listener = null;

    public void setListener(ChapterSelectionFragmentListener listener) {
        this.listener = listener;
    }

    protected ListView mListView = null;

    private List<BibleChapter> chapters;
    private ChaptersAdapter adapter;

    private int selectedRow = -1;

    //region setup
    /**
     * @param listener Listener for when a chapter is selected
     * @return a newly constructed ChaptersFragment
     */
    public static ChaptersFragment newInstance(ChapterSelectionFragmentListener listener) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        fragment.listener = listener;
        return fragment;
    }

    public ChaptersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.general_list, container, false);
        setupData();
        setupViews(view);
        return view;
    }

    private void setupData(){
        BibleChapter currentChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getContext(), false);

        if(currentChapter != null) {
            chapters = currentChapter.getBook().getBibleChapters(true);
        }
    }

    protected List<GeneralRowInterface> getData(){

        BibleChapter currentChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getContext(), false);
        List<GeneralRowInterface> dataList = new ArrayList<>();

        if(currentChapter == null){
            return dataList;
        }

        for(BibleChapter row : chapters) {
            dataList.add(new GeneralRowInterface.BasicGeneralRowInterface(row.getUniqueSlug(), row.getNumber()));
            if(row.getId().equals(currentChapter.getId())){
                selectedRow = chapters.indexOf(row);
            }
        }

        return dataList;
    }

    private void setupViews(View view){
        prepareListView(view);
    }
    protected void prepareListView(View view) {

        List<GeneralRowInterface> data = this.getData();

        if (mListView == null) {
            mListView = (ListView) view.findViewById(R.id.generalList);
        }
        if (data == null) {
            return;
        }

        mListView.setOnItemClickListener(this);
        adapter = new ChaptersAdapter(getContext(), data, this, selectedRow);
        mListView.setAdapter(adapter);
    }

    /**
     * reloads list with new book
     * @param newBook new book from which to display the chapters.
     */
    protected void reload(Book newBook){

        this.chapters = newBook.getBibleChapters(true);
        adapter.update(getData());
    }

    //endregion

    //region accessors

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    //endregion

    //region OnItemClickListener

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        UWPreferenceDataManager.changedToBibleChapter(getContext(), chapters.get(position).getId(), false);
        if(listener != null) {
            listener.chapterWasSelected();
        }
    }

    //endregion

    //region detach

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //endregion
}
