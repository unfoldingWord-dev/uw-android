package view;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 7/1/15.
 */
public class UWReadingToolbarViewGroup {

    private Activity activity;
    private UWReadingToolbarListener listener;

    private Toolbar toolbar;
    private ImageButton leftButton;

    private LinearLayout chapterLayout;
    private TextView chapterTextView;

    private LinearLayout mainVersionLayout;
    private TextView mainVersionTextView;

    private LinearLayout secondaryVersionLayout;
    private TextView secondaryVersionTextView;

    private FrameLayout centerMarker;
    private FrameLayout rightButtonPlaceholder;

    private boolean isMinni = false;
    private boolean hasTwoVersions = false;

    private String chapterText;
    private String mainVersionText;
    private String secondaryVersionText;

    public UWReadingToolbarViewGroup(Toolbar toolbar, Activity activity, UWReadingToolbarListener listener) {

        this.listener = listener;
        this.toolbar = toolbar;
        this.activity = activity;
        setupViews();
    }

    private void setupViews(){

        getViews();
        setupClickListeners();
        layoutViews();
    }

    private void getViews(){

        this.leftButton = (ImageButton) activity.findViewById(R.id.reading_bar_back_button);

        this.chapterLayout = (LinearLayout) activity.findViewById(R.id.reading_toolbar_chapter_button);
        this.chapterTextView = (TextView) activity.findViewById(R.id.reading_toolbar_chapter_text);

        this.mainVersionLayout = (LinearLayout) activity.findViewById(R.id.reading_toolbar_main_version_button);
        this.mainVersionTextView = (TextView) activity.findViewById(R.id.reading_toolbar_main_version_text);

        this.secondaryVersionLayout = (LinearLayout) activity.findViewById(R.id.reading_toolbar_secondary_version_button);
        this.secondaryVersionTextView = (TextView) activity.findViewById(R.id.reading_toolbar_secondary_version_text);

        this.rightButtonPlaceholder = (FrameLayout) activity.findViewById(R.id.reading_toolbar_right_button_placeholder);
        this.centerMarker = (FrameLayout) activity.findViewById(R.id.center_marker);
    }

    private void setupClickListeners(){

        this.leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backButtonClicked();
            }
        });

        this.chapterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.chaptersButtonClicked();
            }
        });

        this.mainVersionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.mainVersionButtonClicked();
            }
        });

        this.secondaryVersionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.secondaryVersionButtonClicked();
            }
        });
    }

    public void setChapterText(String chapterText) {
        this.chapterText = chapterText;
        if(chapterText != null) {
            this.chapterTextView.setText(this.chapterText);
        }
        else{
            chapterLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setMainVersionText(String mainVersionText) {
        this.mainVersionText = mainVersionText;
        mainVersionTextView.setText((this.mainVersionText != null)? this.mainVersionText : "");
        layoutViews();
    }

    public void setSecondaryVersionText(String secondaryVersionText) {
        this.secondaryVersionText = secondaryVersionText;
        secondaryVersionTextView.setText((this.secondaryVersionText != null)? this.secondaryVersionText : "");
        layoutViews();
    }

    public boolean hasTwoVersions() {
        return hasTwoVersions;
    }
    public void setHasTwoVersions(boolean hasTwoVersions) {
        this.hasTwoVersions = hasTwoVersions;
        layoutViews();
    }

    public boolean isMinni() {
        return isMinni;
    }
    public void setIsMinni(boolean isSmall) {
        this.isMinni = isSmall;
        layoutViews();
    }

    public boolean toggleIsMinni(){
        setIsMinni(!isMinni);
        return isMinni;
    }

    public void setViewState(boolean isMinni, boolean hasTwoVersions){
        this.isMinni = isMinni;
        this.hasTwoVersions = hasTwoVersions;
        layoutViews();
    }

    private void layoutViews(){

        layoutChapterView();
        layoutMainVersionView();
        layoutSecondaryVersionView();
        layoutToolbar();
        setViewVisibilities();
    }

    private void setViewVisibilities(){

        chapterLayout.setVisibility(View.VISIBLE);
        mainVersionLayout.setVisibility(View.VISIBLE);

        leftButton.setVisibility((isMinni) ? View.GONE : View.VISIBLE);
        secondaryVersionLayout.setVisibility((hasTwoVersions) ? View.VISIBLE : View.GONE);
        rightButtonPlaceholder.setVisibility((!isMinni && hasTwoVersions) ? View.INVISIBLE : View.GONE);

        if(chapterText == null || chapterText.length() < 1) {
            chapterLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void layoutToolbar(){
        ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
        toolbarParams.height = (isMinni)? getSizeForDp(30) : ViewGroup.LayoutParams.WRAP_CONTENT;
        toolbar.setLayoutParams(toolbarParams);
    }

    private void layoutChapterView(){

        RelativeLayout.LayoutParams chapterParams = new RelativeLayout.LayoutParams(chapterLayout.getLayoutParams());
        chapterParams.addRule((!isMinni && !hasTwoVersions) ? RelativeLayout.CENTER_IN_PARENT : RelativeLayout.CENTER_HORIZONTAL);
        chapterParams.height = getSizeForDp((!isMinni && !hasTwoVersions)? 50 : 25);
        chapterParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        if(isMinni || hasTwoVersions){
            chapterParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        chapterLayout.setLayoutParams(chapterParams);
    }

    private void layoutMainVersionView(){

        RelativeLayout.LayoutParams versionParams = new RelativeLayout.LayoutParams(mainVersionLayout.getLayoutParams());
        versionParams.height = getSizeForDp((!isMinni && !hasTwoVersions)? 50 : 25);
        versionParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        if(!hasTwoVersions || isMinni){
            versionParams.addRule(getSDKSafeAlignParentRight());
        }
        if(hasTwoVersions && !isMinni){
            versionParams.addRule(getSDKSafeLeftOf(), rightButtonPlaceholder.getId());
            versionParams.addRule(getSDKSafeRightOf(), centerMarker.getId());
            versionParams.addRule(RelativeLayout.BELOW, chapterLayout.getId());
        }
        if(hasTwoVersions && isMinni){
            versionParams.addRule(getSDKSafeRightOf(), chapterLayout.getId());
        }
        mainVersionLayout.setLayoutParams(versionParams);
    }

    private void layoutSecondaryVersionView(){

        if(hasTwoVersions) {
            RelativeLayout.LayoutParams secondaryVersionParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getSizeForDp(25));
            secondaryVersionParams.addRule((isMinni) ? getSDKSafeLeftOf() : RelativeLayout.BELOW, chapterLayout.getId());
            secondaryVersionLayout.setLayoutParams(secondaryVersionParams);
            if(isMinni){
                secondaryVersionParams.addRule(getSDKSafeAlignParentLeft());
            }
            else{
                secondaryVersionParams.addRule(getSDKSafeRightOf(), leftButton.getId());
                secondaryVersionParams.addRule(getSDKSafeLeftOf(), centerMarker.getId());
            }
        }
    }

    public void setBackgroundColor(int color){
        this.toolbar.setBackgroundColor(color);
    }

    public interface UWReadingToolbarListener{

        /**
         * Toolbar's back button was
         */
        void backButtonClicked();

        /**
         * Toolbar's chapters button was clicked
         */
        void chaptersButtonClicked();

        /**
         * Toolbar's main version button was clicked
         */
        void mainVersionButtonClicked();

        /**
         * Toolbar's secondary version button was clicked
         */
        void secondaryVersionButtonClicked();
    }


    private int getSizeForDp(int sizeInDP){
        return (int) (sizeInDP * activity.getResources().getDisplayMetrics().density + 0.5f) ;
    }

    private static int getSDKSafeAlignParentRight(){
        return (Build.VERSION.SDK_INT >= 17) ? RelativeLayout.ALIGN_PARENT_END : RelativeLayout.ALIGN_PARENT_RIGHT;
    }

    private static int getSDKSafeAlignParentLeft(){
        return (Build.VERSION.SDK_INT >= 17) ? RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_PARENT_LEFT;
    }

    private static int getSDKSafeLeftOf(){
        return (Build.VERSION.SDK_INT >= 17) ? RelativeLayout.START_OF : RelativeLayout.LEFT_OF;
    }
    private static int getSDKSafeRightOf(){
        return (Build.VERSION.SDK_INT >= 17) ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF;
    }

}
