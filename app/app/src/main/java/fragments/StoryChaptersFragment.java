package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.StoriesChapterAdapter;
import model.DaoDBHelper;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import model.daoModels.Version;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragments.StoryChaptersFragment.StoryChaptersFragmentListener} interface
 * to handle interaction events.
 * Use the {@link fragments.StoryChaptersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryChaptersFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    public static String STORY_CHAPTERS_INDEX_STRING = "STORY_CHAPTERS_INDEX_STRING";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    private StoryChaptersFragmentListener mListener = null;

    protected ListView mListView = null;
    private boolean showTitle = false;
    private TextView titleTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BookSelectionFragment.
     */
    public static StoryChaptersFragment newInstance(boolean showTitle) {
        StoryChaptersFragment fragment = new StoryChaptersFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_TITLE_PARAM, showTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryChaptersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            showTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
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
        titleTextView = (TextView) view.findViewById(R.id.chapter_selection_text_view);
        if(!showTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            titleTextView.setVisibility(View.VISIBLE);
        }
        prepareListView(view);
    }

    protected void prepareListView(View view) {

        List<StoriesChapter> chapterModels = this.getData();
//        BookModel book = this.chosenVersion.getChildModels(getApplicationContext()).get(0);

        if (chapterModels != null) {

            mListView = (ListView) view.findViewById(R.id.generalList);
            mListView.setOnItemClickListener(this);

            mListView.setAdapter(new StoriesChapterAdapter(getContext(), chapterModels, 2));

            int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(STORY_CHAPTERS_INDEX_STRING, -1);
            mListView.setSelection((scrollPosition > 0)? scrollPosition - 1 : 0);
        }
    }

    protected void reload(){

        List<StoriesChapter> data = this.getData();
        StoryPage page = UWPreferenceDataManager.getCurrentStoryPage(getContext(), false);
        int index = data.indexOf(page);
        StoriesChapterAdapter adapter = new StoriesChapterAdapter(getContext(), data, 2);
        mListView.setAdapter(adapter);
    }

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    protected List<StoriesChapter> getData(){

        StoryPage page = UWPreferenceDataManager.getCurrentStoryPage(getContext(), false);

        List<StoriesChapter> chapters = page.getStoriesChapter().getBook().getStoryChapters();

        return chapters;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof StoriesChapter) {
            StoriesChapter model = (StoriesChapter) itemAtPosition;

            UWPreferenceManager.setNewStoriesPage(getContext(), model.getStoryPages().get(0), false);
            mListener.chapterWasSelected();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mListener == null) {
            try {
                mListener = (StoryChaptersFragmentListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    protected String getIndexStorageString() {
        return STORY_CHAPTERS_INDEX_STRING;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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
    public interface StoryChaptersFragmentListener {
        public void chapterWasSelected();
    }

}
