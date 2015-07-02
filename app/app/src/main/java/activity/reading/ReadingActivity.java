package activity.reading;


import android.app.Dialog;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.BuildConfig;
import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import activity.AnimationParadigm;
import activity.UWBaseActivity;
import activity.bookSelection.BookSelectionActivity;
import activity.bookSelection.GeneralSelectionActivity;
import activity.bookSelection.InitialPageActivity;
import activity.bookSelection.VersionSelectionActivity;
import adapters.ReadingPagerAdapter;
import fragments.BibleReadingFragment;
import fragments.BooksFragment;
import fragments.ChapterSelectionFragment;
import fragments.ChaptersFragment;
import fragments.VersionSelectionFragment;
import model.DaoDBHelper;
import model.daoModels.BibleChapter;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.Version;
import utils.UWPreferenceManager;

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
        updateViews();
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

    private void updateViews(){

        updateToolbar();
        updateReadingView();
    }

    private void updateToolbar() {

        boolean hasVersion = (currentChapter == null);

        getToolbar().setCheckingLevelImage((hasVersion)? getCheckingLevelImage() : -1);
        getToolbar().setTitle((hasVersion) ? currentChapter.getTitle() : null, true);

        String rightButtonText = (hasVersion)? currentChapter.getBook().getVersion().getSlug() : "Select Version";
        getToolbar().setRightButtonText(rightButtonText, true);
    }

    private void updateReadingView(){

        if(this.readingFragment == null){
            this.readingFragment = BibleReadingFragment.newInstance(currentChapter.getBook());
            getSupportFragmentManager().beginTransaction().add(readingLayout.getId(), readingFragment, "BibleReadingFragment");
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

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        String projectId = Long.toString(currentChapter.getBook().getVersion().getLanguage().getProjectId());

//        if(isTablet()){
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//            VersionSelectionFragment fragment = VersionSelectionFragment.newInstance(projectId, true);
//            fragment.show(ft, VERSION_FRAGMENT_ID);
//        }
//        else {
            startActivity(new Intent(this, VersionSelectionActivity.class).putExtra(
                    GeneralSelectionActivity.CHOSEN_ID, projectId));
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
            View view = inflater.inflate(R.layout.version_information_fragment, container, false);
            TextView checkingEntityTextView = (TextView) view.findViewById(R.id.checkingEntitytextView);
            ImageView checkingLevelImage = (ImageView) view.findViewById(R.id.checking_level_image);
            TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);
            TextView publishDateTextView = (TextView) view.findViewById(R.id.publishDateTextView);
            TextView verificationTextView = (TextView) view.findViewById(R.id.verification_text_view);
            Button status = (Button) view.findViewById(R.id.status);
            TextView checkingLevelExplanationTextView = (TextView) view.findViewById(R.id.checking_level_explanation_text);


            checkingEntityTextView.setText(version.getStatusCheckingEntity());
            checkingLevelImage.setImageResource(getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
            versionTextView.setText(version.getStatusVersion());
            publishDateTextView.setText(version.getStatusPublishDate());
            verificationTextView.setText(getVerificationText(version, getActivity().getApplicationContext()));
            checkingLevelExplanationTextView.setText(getCheckingLevelText(Integer.parseInt(version.getStatusCheckingLevel())));

            int verificationStatus = 1;//version.getVerificationStatus(getActivity().getApplicationContext());
            status.setBackgroundResource(getColorForStatus(verificationStatus));
            status.setText(getButtonTextForStatus(verificationStatus, getActivity().getApplicationContext()));

            return view;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            return dialog;
        }

        private int getCheckingLevelImage(int level){
            switch (level){
                case 2:{
                    return R.drawable.level_two_dark;
                }
                case 3:{
                    return R.drawable.level_three_dark;
                }
                default:{
                    return R.drawable.level_one_dark;
                }
            }
        }

        private String getVerificationText(Version version, Context context){

            String text;
            int status = 1;// version.getstatus(context);
            switch (status){
                case 0:{
                    text = context.getResources().getString(R.string.verified_title_start);
                    break;
                }
                case 1:{
                    text = context.getResources().getString(R.string.expired_title_start) + "\n";// + version.verificationText;
                    break;
                }
                case 3:{
                    text = context.getResources().getString(R.string.failed_title_start) + "\n";// + version.verificationText;
                    break;
                }
                default:{
                    text = context.getResources().getString(R.string.error_title_start) + "\n";// + version.verificationText;
                }
            }

//            ArrayList<String> organizations = version.getSigningOrganizations(context);

//            if(status == 0){
//                for(String org : organizations){
//                    text += " " + org + ",";
//                }
//                text = text.substring(0, text.length() - 1);
//            }

            return text;
        }

        private int getCheckingLevelText(int level){

            switch (level){
                case 2:{
                    return R.string.level_two;
                }
                case 3:{
                    return R.string.level_three;
                }
                default:{
                    return R.string.level_one;
                }
            }
        }

        private int getColorForStatus(int currentStatus){

            switch (currentStatus){
                case 0:
                    return R.drawable.green_checkmark;
                case 1:
                    return R.drawable.yellow_exclamation_point;
                default:
                    return R.drawable.red_x_button;
            }
        }

        private String getButtonTextForStatus(int currentStatus, Context context){

            switch (currentStatus){
                case 0:
                    return context.getResources().getString(R.string.verified_button_char);
                case 1:
                    return context.getResources().getString(R.string.expired_button_char);
                default:
                    return context.getResources().getString(R.string.x_button_char);
            }
        }
    }
}
