package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.daoModels.Book;

/**
 * Created by PJ Fechner
 *
 * Dialogue Fragment which contains both book and chapter selection for bible.
 */
public class ChapterSelectionFragment extends DialogFragment implements BooksFragment.BooksFragmentListener{

    private static final String SHOW_TITLE_PARAM = "SHOW_TITLE_PARAM";

    private ChapterSelectionFragmentListener listener;

    private ChaptersFragment chapterFragment;
    private TabHost tabHost;
    private boolean showTitle = false;

    //region setup

    /**
     * @param showTitle True of the fragment should show a title
     * @return a newly constructed ChapterSelectionFragment
     */
    public static ChapterSelectionFragment newInstance(boolean showTitle) {

        ChapterSelectionFragment fragment = new ChapterSelectionFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_TITLE_PARAM, showTitle);

        fragment.setArguments(args);
        return fragment;
    }

    public ChapterSelectionFragment() {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ChapterSelectionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_selection, container, false);
        doSetup(view);
        return view;
    }

    private void  doSetup(View view){
        setupViews(view);
        addChildFragments();
    }

    private void setupViews(View view){

        tabHost = (TabHost) view.findViewById(R.id.tabHost);
        TextView titleTextView = (TextView) view.findViewById(R.id.chapter_selection_text_view);
        if(!showTitle){
            titleTextView.setVisibility(View.GONE);
        }
        else{
            titleTextView.setVisibility(View.VISIBLE);
        }
    }

    private void addChildFragments(){

        setupTabs();
        BooksFragment booksFragment = BooksFragment.newInstance(this);
        booksFragment.setListener(this);
        chapterFragment = ChaptersFragment.newInstance(this.listener);
        chapterFragment.setListener(this.listener);

        FragmentManager manager = getChildFragmentManager();
        if(manager.getFragments() != null) {
            for(Fragment fragment : manager.getFragments()){
                manager.beginTransaction().remove(fragment).commit();
            }
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.books_tab, booksFragment).add(R.id.chapters_tab, chapterFragment).commit();
    }

    //endregion

    //region tab handling

    private void setupTabs() {
        tabHost.setup(); // you must call this before adding your tabs!
        if(tabHost.getTabWidget().getTabCount() > 0){
            tabHost.clearAllTabs();
        }
        tabHost.addTab(newTab("Book", R.id.books_tab));
        tabHost.addTab(newTab("Chapter", R.id.chapters_tab));
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.darkgray));
        tabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.clear));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int tab = tabHost.getCurrentTab();
                tabHost.getTabWidget().getChildAt(0).setBackgroundColor(getResources().getColor(R.color.clear));
                tabHost.getTabWidget().getChildAt(1).setBackgroundColor(getResources().getColor(R.color.clear));
                tabHost.getTabWidget().getChildAt(tab).setBackgroundColor(getResources().getColor(R.color.darkgray));
            }
        });
    }

    private TabHost.TabSpec newTab(String tag, int tabContentId) {
        TabHost.TabSpec spec = tabHost.newTabSpec(tag);
        spec.setContent(tabContentId);
        spec.setIndicator(tag);
        return spec;
    }

    //endregion

    //region detach

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //endregion

    //region BooksFragmentListener

    @Override
    public void bookWasSelected(Book book) {

        chapterFragment.reload(book);
        tabHost.setCurrentTab(1);
    }

    //endregion
}
