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

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import model.datasource.BibleChapterDataSource;
import model.datasource.BookDataSource;
import model.modelClasses.mainData.BibleChapterModel;
import model.modelClasses.mainData.BookModel;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public static String BOOK_CHAPTERS_INDEX_STRING = "BOOK_CHAPTERS_INDEX_STRING";

    private ChaptersFragmentListener mListener = null;

    public void setmListener(ChaptersFragmentListener mListener) {
        this.mListener = mListener;
    }

    private String manualId = null;
    protected ListView mListView = null;

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
        GeneralAdapter adapter = new GeneralAdapter(getContext(), data, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);

        int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getIndexStorageString(), -1);
        mListView.setSelection((scrollPosition > 0)? scrollPosition - 1 : 0);
    }

    protected void reload(String newId){

        manualId = newId;

        ArrayList<GeneralRowInterface> data = this.getData();
        GeneralAdapter adapter = new GeneralAdapter(getContext(), data, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);
    }

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    protected ArrayList<GeneralRowInterface> getData(){

        Context context = this.getActivity().getApplicationContext();

        String chapterId = UWPreferenceManager.getSelectedBibleChapter(context);
        if(this.manualId != null){
            chapterId = manualId;
        }
        if(Long.parseLong(chapterId) < 0) {
            return null;
        }
        else {
            BibleChapterModel model = new BibleChapterDataSource(context).getModel(chapterId);
            ArrayList<BibleChapterModel> chapters = model.getParent(context).getBibleChildModels(context);

            long selectedId = model.uid;
            ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
            int i = 0;
            for (BibleChapterModel chapter : chapters) {
                if(chapter.uid == selectedId){
                    int chosenIndex = (this.manualId == null)? i : Integer.parseInt(this.manualId);
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(getIndexStorageString(), chosenIndex).commit();
                }
                data.add(chapter);
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

            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(this.getIndexStorageString(), -1).commit();
            // put selected position  to sharedprefences
            UWPreferenceManager.setSelectedBibleChapter(getContext(), Long.parseLong(model.getChildIdentifier()));
            if(mListener != null) {
                mListener.chapterWasSelected();
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
