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
import java.util.List;

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import utils.UWPreferenceManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BooksFragment.BooksFragmentListener} interface
 * to handle interaction events.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment implements AdapterView.OnItemClickListener {


    static String BOOK_FRAGMENT_INDEX_ID = "BOOK_FRAGMENT_INDEX_ID";

    private BooksFragmentListener mListener = null;
    public void setmListener(BooksFragmentListener mListener) {
        this.mListener = mListener;
    }
    protected ListView mListView = null;
    private List<Book> books;
    private int selectedRow = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BookSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BooksFragment newInstance(BooksFragmentListener listener) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        fragment.mListener = listener;
        return fragment;
    }

    public BooksFragment() {
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
        setupViews(view);
        return view;
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
        int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getIndexStorageString(), -1);
        GeneralAdapter adapter = new GeneralAdapter(getContext(), data, this, selectedRow);
        mListView.setAdapter(adapter);


        mListView.setSelection((scrollPosition > 1) ? scrollPosition - 1 : 0);
    }

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

//    protected ArrayList<GeneralRowInterface> getData(){
//
//        Context context = getContext();
//
//        long chapterId = UWPreferenceManager.getSelectedBibleChapter(context);
//        if(chapterId < 0) {
//            return null;
//        }
//        else {
//            BibleChapter model = BibleChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(context));
//            List<Book> books = model.getBook().getVersion().getBooks();
//
//            long selectedId = model.getBookId();
//            ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
//            int i = 0;
//            for (Book book : books) {
//
//                if(book.getBibleChapters() == null || book.getBibleChapters().size() == 0){
//                    continue;
//                }
//                long uid = book.getId();
//                if(selectedId == uid){
//                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getIndexStorageString(), i).commit();
//                }
//                data.add(new GeneralRowInterface.BasicGeneralRowInterface(Long.toString(book.getId()), book.getTitle()));
//                i++;
//            }
//            return data;
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        if(mListener != null) {
            mListener.bookWasSelected(books.get(position));
        }
    }

    protected List<GeneralRowInterface> getData(){

        BibleChapter currentChapter = DaoDBHelper.getDaoSession(getContext()).getBibleChapterDao()
                .loadDeep(UWPreferenceManager.getSelectedBibleChapter(getContext()));
        books = currentChapter.getBook().getVersion().getBooks();
        long currentBookId = currentChapter.getBookId();

        List<GeneralRowInterface> dataList = new ArrayList<GeneralRowInterface>();
        for(Book row : books) {
            dataList.add(new GeneralRowInterface.BasicGeneralRowInterface(row.getUniqueSlug(), row.getTitle()));
            if(row.getId() == currentBookId){
                selectedRow = books.indexOf(row);
            }
        }

        return dataList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mListener == null) {
//            try {
//                mListener = (BooksFragmentListener) activity;
//            } catch (ClassCastException e) {
//                throw new ClassCastException(activity.toString()
//                        + " must implement OnFragmentInteractionListener");
//            }
        }
    }

    protected String getIndexStorageString() {
        return BOOK_FRAGMENT_INDEX_ID;
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
    public interface BooksFragmentListener {
        void bookWasSelected(Book book);
    }

}
