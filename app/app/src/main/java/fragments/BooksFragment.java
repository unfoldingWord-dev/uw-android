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

/**
 * Created by PJ Fechner
 * Fragment for selecting a book from a list.
 */
public class BooksFragment extends Fragment implements AdapterView.OnItemClickListener {

    private BooksFragmentListener listener = null;

    protected ListView listView = null;

    private List<Book> books;
    private int selectedRow = -1;

    //region setup

    /**
     * @param listener listener for when a book is selected
     * @return newly constructed instance of a BooksFragment
     */
    public static BooksFragment newInstance(BooksFragmentListener listener) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        fragment.listener = listener;
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

        if (listView == null) {
            listView = (ListView) view.findViewById(R.id.generalList);
        }
        if (data == null) {
            return;
        }

        listView.setOnItemClickListener(this);

        ChaptersAdapter adapter = new ChaptersAdapter(getContext(), data, this, selectedRow);
        listView.setAdapter(adapter);
//        listView.setSelection((scrollPosition > 1) ? scrollPosition - 1 : 0);
    }

    protected List<GeneralRowInterface> getData(){

        BibleChapter currentChapter = UWPreferenceDataAccessor.getCurrentBibleChapter(getContext(), false);
        if(currentChapter == null){
            return null;
        }
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
    //endregion

    //region accessors

    public void setListener(BooksFragmentListener listener) {
        this.listener = listener;
    }

    //endregion

    //region OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        if(listener != null) {
            listener.bookWasSelected(books.get(position));
        }
    }

    //endregion

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface BooksFragmentListener {
        /**
         * User selected a book
         * @param book book that was selected
         */
        void bookWasSelected(Book book);
    }
}
