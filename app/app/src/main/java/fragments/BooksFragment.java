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

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.datasource.BookDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    static String BOOK_FRAGMENT_INDEX_ID = "BOOK_FRAGMENT_INDEX_ID";

    private BooksFragmentListener mListener = null;

    public void setmListener(BooksFragmentListener mListener) {
        this.mListener = mListener;
    }

    protected ListView mListView = null;

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

        ArrayList<GeneralRowInterface> data = this.getData();

        if (mListView == null) {
            mListView = (ListView) view.findViewById(R.id.generalList);
        }
        if (data == null) {
            return;
        }

        mListView.setOnItemClickListener(this);
        int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getIndexStorageString(), -1);
        GeneralAdapter adapter = new GeneralAdapter(getContext(), data, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);


        mListView.setSelection((scrollPosition > 1) ? scrollPosition -1 : 0);
    }

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    protected ArrayList<GeneralRowInterface> getData(){

        Context context = getContext();

        String chapterId = UWPreferenceManager.getSelectedBibleChapter(context);
        if(Long.parseLong(chapterId) < 0) {
            return null;
        }
        else {
            BibleChapterModel model = new BibleChapterDataSource(context).getModel(chapterId);
            ArrayList<BookModel> books = model.getParent(context).getParent(context).getChildModels(context);

            long selectedId = model.getParent(context).uid;
            ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
            int i = 0;
            for (BookModel book : books) {

                long uid = book.uid;
                if(selectedId == uid){
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getIndexStorageString(), i).commit();
                }
                data.add(book);
                i++;
            }

            return data;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof GeneralRowInterface) {
            GeneralRowInterface model = (GeneralRowInterface) itemAtPosition;

            BookModel selectedModel = new BookDataSource(getContext()).getModel(model.getChildIdentifier());
            long desiredId = selectedModel.getBibleChildModels(getContext()).get(0).uid;
            if(mListener != null) {
                mListener.bookWasSelected(Long.toString(desiredId));
            }
//            startActivityForResult(new Intent(this, this.getChildClass(model)).putExtra(
//                    CHOSEN_ID, model.getChildIdentifier()), 1);
//            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
        }
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
        public void bookWasSelected(String chapterUid);
    }

}
