package activity.reading;


import android.app.Dialog;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.bookSelection.BookSelectionActivity;
import activity.bookSelection.VersionSelectionActivity;
import fragments.BibleReadingFragment;
import fragments.BooksFragment;
import fragments.ChapterSelectionFragment;
import fragments.ChaptersFragment;
import fragments.VersionSelectionFragment;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;
import view.ViewHelper;

/**
 * Created by Acts Media Inc on 5/12/14.
 */
public class ReadingActivity extends UWBaseActivity implements
        VersionSelectionFragment.VersionSelectionFragmentListener,
        ChapterSelectionFragment.ChapterSelectionListener,
        BooksFragment.BooksFragmentListener,
        ChaptersFragment.ChaptersFragmentListener,
        BibleReadingFragment.BibleReadingFragmentListener
{
    static private final String TAG = "ReadingActivity";

    static final public String BOOK_INDEX_STRING = "READING_INDEX_STRING";

    static private final String VERSION_FRAGMENT_ID = "VERSION_FRAGMENT_ID";
    static private final String CHAPTER_SELECTION_FRAGMENT_ID = "CHAPTER_SELECTION_FRAGMENT_ID";

    private BibleChapter currentChapter;

    private FrameLayout readingLayout;
    private TextView errorTextView;
    private BibleReadingFragment readingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        setupToolbar(false);
        loadData();
        setupViews();

        if(currentChapter == null){
            goToVersionSelection();
        }
    }

    private void loadData(){

        long chapterId = UWPreferenceManager.getSelectedBibleChapter(getApplicationContext());

        if(chapterId > -1){
            currentChapter = BibleChapter.getModelForId(chapterId, DaoDBHelper.getDaoSession(getApplicationContext()));
        }
        else{
            currentChapter = null;
        }
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_LEFT_RIGHT;
    }

    private void setupViews(){
        readingLayout = (FrameLayout) findViewById(R.id.reading_fragment_frame);
        errorTextView = (TextView) findViewById(R.id.reading_error_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadData();

        if (currentChapter == null) {
            setContentView(R.layout.activity_reading);
            readingLayout.setVisibility(View.GONE);
            errorTextView.setVisibility(View.VISIBLE);
        }
        else{
            readingLayout.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentChapter != null) {
            updateViews();
        }
    }

    private void updateViews(){

        updateToolbar();
        updateReadingView();
    }

    private void updateToolbar() {

        boolean hasVersion = (currentChapter != null);

        getToolbar().setCheckingLevelImage((hasVersion)? getCheckingLevelImage() : -1);
        getToolbar().setTitle((hasVersion) ? currentChapter.getTitle() : null, true);

        String rightButtonText = (hasVersion)? currentChapter.getBook().getVersion().getSlug() : "Select Version";
        getToolbar().setRightButtonText(rightButtonText, true);
    }

    private void updateReadingView(){

        if(this.readingFragment == null){
            this.readingFragment = BibleReadingFragment.newInstance(currentChapter.getBook());
            getSupportFragmentManager().beginTransaction().add(readingLayout.getId(), readingFragment, "BibleReadingFragment").commit();
        }
        else{
            readingFragment.updateReadingFragment(currentChapter.getBook());
        }
    }

    private int getCheckingLevelImage(){

        int level = Integer.parseInt(currentChapter.getBook().getVersion().getStatusCheckingLevel());
        switch (level){
            case 2:{
                return R.drawable.level_two;
            }
            case 3:{
                return R.drawable.level_three;
            }
            default:{
                return R.drawable.level_one;
            }
        }
    }

    private boolean isTablet(){

        int screen_density = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        if (screen_density == Configuration.SCREENLAYOUT_SIZE_LARGE || screen_density == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return true;
        }
        else{
            return false;
        }
    }

    private void goToVersionSelection(){

//        Bundle extras = getIntent().getExtras();
//        if (extras == null) {
//            return;
//        }

        Project selectedProject = new Project();
        List<Project> projects = Project.getAllModels(DaoDBHelper.getDaoSession(getApplicationContext()));

        for(Project project : projects){

            if(!project.getSlug().equalsIgnoreCase("obs")){
                selectedProject = project;
                break;
            }
        }
//        if(isTablet()){
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//            VersionSelectionFragment fragment = VersionSelectionFragment.newInstance(projectId, true);
//            fragment.show(ft, VERSION_FRAGMENT_ID);
//        }
//        else {
            startActivity(new Intent(this, VersionSelectionActivity.class).putExtra(
                    VersionSelectionActivity.PROJECT_PARAM, selectedProject));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
//        }
    }

    private void goToChapterActivity(){

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

//        String projectId = extras.getString(GeneralSelectionActivity.CHOSEN_ID);

        if(isTablet()){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ChapterSelectionFragment fragment = ChapterSelectionFragment.newInstance(true);
            fragment.show(ft, CHAPTER_SELECTION_FRAGMENT_ID);
        }
        else {
            startActivity(new Intent(getApplicationContext(), BookSelectionActivity.class));
            overridePendingTransition(R.anim.enter_from_bottom, R.anim.enter_center);
        }
    }

    @Override
    public void toggleNavBar() {
        getToolbar().toggleHidden();
    }

    @Override
    public void rightButtonClicked() {
        goToVersionSelection();
    }

    @Override
    public void centerButtonClicked() {
        goToChapterActivity();
    }

    @Override
    public void checkingLevelButtonClicked() {
        goToCheckingLevelView();
    }

    @Override
    public void rowWasSelected() {
        removeFragment(VERSION_FRAGMENT_ID);
    }

    @Override
    public void selectionFragmentChoseChapter() {
        removeFragment(CHAPTER_SELECTION_FRAGMENT_ID);
    }

    private void removeFragment(String fragmentId){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag(fragmentId);

        if (previous != null) {
            ft.remove(previous);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void bookWasSelected(String chapterUid) {

    }

    @Override
    public void chapterWasSelected() {

    }

    private void goToCheckingLevelView(){
        Bundle args = new Bundle();
        args.putLong(VERSION_ID_PARAM, currentChapter.getBook().getVersionId());
        CheckingLevelFragment fragment = new CheckingLevelFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fragment.show(ft, CHECKING_LEVEL_FRAGMENT_ID);
    }

    static protected final String CHECKING_LEVEL_FRAGMENT_ID = "CHECKING_LEVEL_FRAGMENT_ID";
    static protected String VERSION_ID_PARAM = "VERSION_ID_PARAM";


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else {
        }
    }

    public static class CheckingLevelFragment extends DialogFragment {


        private Version version;

        public CheckingLevelFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                long versionId = getArguments().getLong(VERSION_ID_PARAM);
                version = Version.getVersionForId(versionId, DaoDBHelper.getDaoSession(getActivity().getApplicationContext()));
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.version_information_view, container, false);
            TextView checkingEntityTextView = (TextView) view.findViewById(R.id.checking_entity_text_view);
            ImageView checkingLevelImage = (ImageView) view.findViewById(R.id.checking_level_image);
            TextView versionTextView = (TextView) view.findViewById(R.id.version_text_view);
            TextView publishDateTextView = (TextView) view.findViewById(R.id.publish_date_text_view);
            TextView verificationTextView = (TextView) view.findViewById(R.id.verification_text_view);
            Button status = (Button) view.findViewById(R.id.status);
            TextView checkingLevelExplanationTextView = (TextView) view.findViewById(R.id.checking_level_explanation_text);


            checkingEntityTextView.setText(version.getStatusCheckingEntity());
            checkingLevelImage.setImageResource(ViewHelper.getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
            versionTextView.setText(version.getStatusVersion());
            publishDateTextView.setText(version.getStatusPublishDate());
            verificationTextView.setText(ViewHelper.getVerificationText(version));
            checkingLevelExplanationTextView.setText(ViewHelper.getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));

            int verificationStatus = 1;//version.getVerificationStatus(getActivity().getApplicationContext());
            status.setBackgroundResource(ViewHelper.getColorForStatus(verificationStatus));
            status.setText(ViewHelper.getButtonTextForStatus(verificationStatus, getActivity().getApplicationContext()));

            return view;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }
    }
}
