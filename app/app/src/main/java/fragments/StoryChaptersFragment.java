package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

import adapters.selectionAdapters.StoriesChapterAdapter;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;
import utils.UWPreferenceDataManager;
import utils.UWPreferenceManager;

/**
 * Fragment for displaying a list of OBS chapters
 */
public class StoryChaptersFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    private ChapterSelectionFragmentListener mListener = null;

    protected ListView mListView = null;
    private TextView titleTextView;

    private boolean showTitle = false;

    //region setup

    /**
     * @param showTitle whether the Fragment should display a title
     * @return a newly constructed StoryChaptersFragment
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showTitle = getArguments().getBoolean(SHOW_TITLE_PARAM);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(mListener == null) {
            try {
                mListener = (ChapterSelectionFragmentListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        if (chapterModels != null) {

            mListView = (ListView) view.findViewById(R.id.generalList);
            mListView.setOnItemClickListener(this);

            StoryPage page = UWPreferenceDataManager.getCurrentStoryPage(getContext(), false);

            int selectedIndex = (page != null)? Integer.parseInt(page.getStoriesChapter().getNumber()) - 1 : -1;
            mListView.setAdapter(new StoriesChapterAdapter(getContext(), chapterModels, selectedIndex));
        }
    }

    //endregion

//    protected void reload(){
//
//        List<StoriesChapter> data = this.getData();
//        StoryPage page = UWPreferenceDataManager.getCurrentStoryPage(getContext(), false);
//        int index = (page != null)? data.indexOf(page.getStoriesChapter()) : -1;
//        StoriesChapterAdapter adapter = new StoriesChapterAdapter(getContext(), data, index);
//        mListView.setAdapter(adapter);
//    }

    //region accessors

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    protected List<StoriesChapter> getData(){

        StoryPage page = UWPreferenceDataManager.getCurrentStoryPage(getContext(), false);
        if(page != null) {
            return page.getStoriesChapter().getBook().getStoryChapters();
        }
        else{
            return null;
        }
    }

    //endregion

    //region OnItemClickListener

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof StoriesChapter) {
            StoriesChapter model = (StoriesChapter) itemAtPosition;

            UWPreferenceManager.setNewStoriesPage(getContext(), model.getStoryPages().get(0), false);
            mListener.chapterWasSelected();
        }
    }

    //endregion

    //region detach

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //endregion
}
