/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

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

    private ReadingToolbarViewData viewData;

    public UWReadingToolbarViewGroup(Toolbar toolbar, Activity activity, ReadingToolbarViewData viewData, UWReadingToolbarListener listener) {

        this.viewData = viewData;
        this.listener = listener;
        this.toolbar = toolbar;
        this.activity = activity;
        setupViews();
    }

    private void setupViews(){

        getViews();
        setupClickListeners();
        updateLabels();
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

    public void setViewData(ReadingToolbarViewData viewData) {
        this.viewData = viewData;
        updateLabels();
    }

    private void updateLabels(){

        updateLabel(viewData.getTitleText(), chapterTextView, chapterLayout);
        updateLabel(viewData.getMainVersionText(), mainVersionTextView, mainVersionLayout);
        updateLabel(viewData.getSecondaryVersionText(), secondaryVersionTextView, secondaryVersionLayout);
        if(!hasTwoVersions){
            secondaryVersionLayout.setVisibility(View.GONE);
        }
    }

    private boolean isValidToDisplay(String text){

        return text != null && text.length() > 0;
    }

    private void updateLabel(String text, TextView textView, ViewGroup containingLayout){

        if(isValidToDisplay(text)){
            textView.setText(text);
            containingLayout.setVisibility(View.VISIBLE);
        }
        else{
            containingLayout.setVisibility(View.INVISIBLE);
        }
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

        leftButton.setVisibility((isMinni) ? View.GONE : View.VISIBLE);
        rightButtonPlaceholder.setVisibility((!isMinni && hasTwoVersions) ? View.INVISIBLE : View.GONE);

        if(hasTwoVersions){
            secondaryVersionLayout.setVisibility((isValidToDisplay(viewData.getSecondaryVersionText())) ? View.VISIBLE : View.INVISIBLE);
        }
        else{
            secondaryVersionLayout.setVisibility(View.GONE);
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
        chapterTextView.setMaxLines((isMinni)? 1 : 2);

        if(isMinni || hasTwoVersions){
            chapterParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }

        if(!isMinni && !hasTwoVersions){
            chapterParams.addRule(getSDKSafeRightOf(), leftButton.getId());
            chapterParams.addRule(getSDKSafeLeftOf(), mainVersionLayout.getId());
        }
        else if (!hasTwoVersions && isMinni){
            chapterParams.addRule(getSDKSafeLeftOf(), mainVersionLayout.getId());
            chapterParams.addRule(getSDKSafeAlignParentLeft());
        }
        else if( hasTwoVersions && isMinni){
            chapterParams.addRule(getSDKSafeRightOf(), secondaryVersionLayout.getId());
            chapterParams.addRule(getSDKSafeLeftOf(), mainVersionLayout.getId());
        }
        else{
            chapterParams.addRule(getSDKSafeRightOf(), leftButton.getId());
            chapterParams.addRule(getSDKSafeLeftOf(), rightButtonPlaceholder.getId());
        }

        chapterLayout.setLayoutParams(chapterParams);
    }

    private void layoutMainVersionView(){

        RelativeLayout.LayoutParams versionParams = new RelativeLayout.LayoutParams(mainVersionLayout.getLayoutParams());
        versionParams.height = getSizeForDp((!isMinni && !hasTwoVersions)? 50 : 25);
        versionParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        if(hasTwoVersions && !isMinni){
            versionParams.addRule(getSDKSafeLeftOf(), rightButtonPlaceholder.getId());
            versionParams.addRule(getSDKSafeRightOf(), centerMarker.getId());
            versionParams.addRule(RelativeLayout.BELOW, chapterLayout.getId());
        }
        else{
            versionParams.addRule(getSDKSafeAlignParentRight());
        }
        mainVersionLayout.setLayoutParams(versionParams);
    }

    private void layoutSecondaryVersionView(){

        if(hasTwoVersions) {
            RelativeLayout.LayoutParams secondaryVersionParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getSizeForDp(25));

            if(isMinni){
                secondaryVersionParams.addRule(getSDKSafeAlignParentLeft());
            }
            else{
                secondaryVersionParams.addRule(RelativeLayout.BELOW, chapterLayout.getId());
                secondaryVersionParams.addRule(getSDKSafeRightOf(), leftButton.getId());
                secondaryVersionParams.addRule(getSDKSafeLeftOf(), centerMarker.getId());
            }
            secondaryVersionLayout.setLayoutParams(secondaryVersionParams);
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
