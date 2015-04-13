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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.Collections;

import adapters.selectionAdapters.GeneralAdapter;
import adapters.selectionAdapters.GeneralRowInterface;
import adapters.selectionAdapters.StoriesChapterAdapter;
import model.datasource.VersionDataSource;
import model.modelClasses.mainData.StoriesChapterModel;
import model.modelClasses.mainData.VersionModel;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public static String STORY_CHAPTERS_INDEX_STRING = "STORY_CHAPTERS_INDEX_STRING";
    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    private StoryChaptersFragmentListener mListener = null;

    protected ListView mListView = null;
    ImageLoader mImageLoader;
    private boolean showTitle = false;
    private TextView titleTextView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment BookSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        ArrayList<GeneralRowInterface> chapterModels = this.getData();
//        BookModel book = this.chosenVersion.getChildModels(getApplicationContext()).get(0);

        if (chapterModels != null) {

            mListView = (ListView) view.findViewById(R.id.generalList);
            mListView.setOnItemClickListener(this);

            mImageLoader = ImageLoader.getInstance();

            if (mImageLoader.isInited()) {
                ImageLoader.getInstance().destroy();
            }

            mImageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
            mListView.setAdapter(new StoriesChapterAdapter(getContext(), chapterModels, this, mImageLoader, this.getIndexStorageString()));

            int scrollPosition = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(STORY_CHAPTERS_INDEX_STRING, -1);
            mListView.setSelection((scrollPosition > 0)? scrollPosition - 1 : 0);
        }
    }

    protected void reload(){

        ArrayList<GeneralRowInterface> data = this.getData();
        GeneralAdapter adapter = new GeneralAdapter(getContext(), data, this, this.getIndexStorageString());
        mListView.setAdapter(adapter);
    }

    private Context getContext(){
        return this.getActivity().getApplicationContext();
    }

    protected ArrayList<GeneralRowInterface> getData(){

        String versionId = UWPreferenceManager.getSelectedStoryVersion(getContext());

        VersionModel version = new VersionDataSource(getContext()).getModel(versionId);

        ArrayList<StoriesChapterModel> chapters = version.getChildModels(
                getContext()).get(0).getStoryChildModels(getContext());
        Collections.sort(chapters);

        long chapterId = Long.parseLong(UWPreferenceManager.getSelectedStoryChapter(getContext()));

        ArrayList<GeneralRowInterface> data = new ArrayList<GeneralRowInterface>();
        int i = 0;
        for(StoriesChapterModel model : chapters){
            if(chapterId == model.uid){
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(STORY_CHAPTERS_INDEX_STRING, i).commit();
            }
            data.add(model);
            i++;
        }

        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowIndex) {

        Object itemAtPosition = adapterView.getItemAtPosition(position);
        if (itemAtPosition instanceof GeneralRowInterface) {
            GeneralRowInterface model = (GeneralRowInterface) itemAtPosition;

            // put selected position  to sharedprefences
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(this.getIndexStorageString(), (int) rowIndex).commit();
            UWPreferenceManager.setSelectedStoryChapter(getContext(), Long.parseLong(model.getChildIdentifier()));
            mListener.chapterWasSelected();
//            startActivityForResult(new Intent(this, this.getChildClass(model)).putExtra(
//                    CHOSEN_ID, model.getChildIdentifier()), 1);
//            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_on_left);
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
