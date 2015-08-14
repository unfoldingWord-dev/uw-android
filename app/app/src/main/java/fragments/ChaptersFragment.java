package fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.ChaptersFragment.ChaptersFragmentListener} interface
 * to handle interaction events.
 * Use the {@link fragments.ChaptersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChaptersFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static String BOOK_CHAPTERS_INDEX_STRING = "BOOK_CHAPTERS_INDEX_STRING";


    private ChaptersFragmentListener mListener = null;

    public void setmListener(ChaptersFragmentListener mListener) {
        this.mListener = mListener;
    }

    protected ListView mListView = null;

    private List<BibleChapter> chapters;
    private GeneralAdapter adapter;

    private int selectedRow = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BookSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChaptersFragment newInstance(ChaptersFragmentListener listener) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        fragment.mListener = listener;
        return fragment;
    }

    public ChaptersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
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
        BibleChapter currentChapter = UWPreferenceDataManager.getCurrentBibleChapter(getContext(), false);

        if(currentChapter != null) {
            chapters = currentChapter.getBook().getBibleChapters(true);
        }
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
        adapter = new GeneralAdapter(getContext(), data, this, selectedRow);
        mListView.setAdapter(adapter);

        int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getIndexStorageString(), -1);
        mListView.setSelection((scrollPosition > 0) ? scrollPosition - 1 : 0);
    }

    protected void reload(Book newBook){

        this.chapters = newBook.getBibleChapters(true);
        adapter.update(getData());
    }

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    protected List<GeneralRowInterface> getData(){

        BibleChapter currentChapter = UWPreferenceDataManager.getCurrentBibleChapter(getContext(), false);
        List<GeneralRowInterface> dataList = new ArrayList<GeneralRowInterface>();

        for(BibleChapter row : chapters) {
            dataList.add(new GeneralRowInterface.BasicGeneralRowInterface(row.getUniqueSlug(), row.getNumber()));
            if(row.getId() == currentChapter.getId()){
                selectedRow = chapters.indexOf(row);
            }
        }

        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        UWPreferenceManager.changedToBibleChapter(getContext(), chapters.get(position).getId(), false);
        if(mListener != null) {
            mListener.chapterWasSelected();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mListener == null) {
//            try {
//                mListener = (ChaptersFragmentListener) activity;
//            } catch (ClassCastException e) {
//                throw new ClassCastException(activity.toString()
//                        + " must implement OnFragmentInteractionListener");
//            }
        }
    }

    protected String getIndexStorageString() {
        return BOOK_CHAPTERS_INDEX_STRING;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ChaptersFragmentListener {
        public void chapterWasSelected();
    }

}
