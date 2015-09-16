package view;

import android.app.ActionBar;
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

        if(!isMinni && !hasTwoVersions){

            RelativeLayout.LayoutParams chapterParams = new RelativeLayout.LayoutParams(chapterLayout.getLayoutParams());
            chapterParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            chapterParams.height = getSizeForDp(50);
            chapterParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            chapterLayout.setLayoutParams(chapterParams);

            RelativeLayout.LayoutParams versionParams = new RelativeLayout.LayoutParams(mainVersionLayout.getLayoutParams());
            versionParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.ALIGN_PARENT_END : RelativeLayout.ALIGN_PARENT_RIGHT);
            versionParams.height = getSizeForDp(50);
            versionParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mainVersionLayout.setLayoutParams(versionParams);

            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            toolbarParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            toolbar.setLayoutParams(toolbarParams);

            chapterLayout.setVisibility(View.VISIBLE);
            mainVersionLayout.setVisibility(View.VISIBLE);
            secondaryVersionLayout.setVisibility(View.GONE);
            leftButton.setVisibility(View.VISIBLE);
            rightButtonPlaceholder.setVisibility(View.INVISIBLE);
        }
        else if(isMinni && !hasTwoVersions){

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(chapterLayout.getLayoutParams());
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.height = getSizeForDp(25);
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            chapterLayout.setLayoutParams(layoutParams);

            layoutParams = new RelativeLayout.LayoutParams(mainVersionLayout.getLayoutParams());
            layoutParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.ALIGN_PARENT_END : RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.height = getSizeForDp(25);
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mainVersionLayout.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            toolbarParams.height = getSizeForDp(25);
            toolbar.setLayoutParams(toolbarParams);

            chapterLayout.setVisibility(View.VISIBLE);
            mainVersionLayout.setVisibility(View.VISIBLE);
            secondaryVersionLayout.setVisibility(View.GONE);
            leftButton.setVisibility(View.GONE);
            rightButtonPlaceholder.setVisibility(View.GONE);
        }
        else if(!isMinni){

            RelativeLayout.LayoutParams chapterParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getSizeForDp(25));
            chapterParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            chapterParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            chapterLayout.setLayoutParams(chapterParams);

            RelativeLayout.LayoutParams mainVersionParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getSizeForDp(25));
            mainVersionParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, rightButtonPlaceholder.getId());
            mainVersionParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, centerMarker.getId());
            mainVersionParams.addRule(RelativeLayout.BELOW, chapterLayout.getId());
            mainVersionLayout.setLayoutParams(mainVersionParams);

            RelativeLayout.LayoutParams secondaryVersionParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getSizeForDp(25));
            secondaryVersionParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, leftButton.getId());
            secondaryVersionParams.addRule((Build.VERSION.SDK_INT >= 17)? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, centerMarker.getId());
            secondaryVersionParams.addRule(RelativeLayout.BELOW, chapterLayout.getId());
            secondaryVersionLayout.setLayoutParams(secondaryVersionParams);

            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            toolbarParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            toolbar.setLayoutParams(toolbarParams);

            secondaryVersionLayout.setVisibility(View.VISIBLE);
            leftButton.setVisibility(View.VISIBLE);
            rightButtonPlaceholder.setVisibility(View.INVISIBLE);
            chapterLayout.setVisibility(View.VISIBLE);
            mainVersionLayout.setVisibility(View.VISIBLE);
        }
        else{

            RelativeLayout.LayoutParams chapterParams = new RelativeLayout.LayoutParams(chapterLayout.getLayoutParams());
            chapterParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            chapterParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            chapterParams.height = getSizeForDp(25);
            chapterParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            chapterLayout.setLayoutParams(chapterParams);

            RelativeLayout.LayoutParams mainVersionParams = new RelativeLayout.LayoutParams(mainVersionLayout.getLayoutParams());
            mainVersionParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.ALIGN_PARENT_END : RelativeLayout.ALIGN_PARENT_RIGHT);
            mainVersionParams.addRule((Build.VERSION.SDK_INT >= 17)? RelativeLayout.END_OF : RelativeLayout.RIGHT_OF, chapterLayout.getId());
            mainVersionParams.height = getSizeForDp(25);
            mainVersionParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mainVersionLayout.setLayoutParams(mainVersionParams);

            RelativeLayout.LayoutParams secondaryVersionParams = new RelativeLayout.LayoutParams(secondaryVersionLayout.getLayoutParams());
            secondaryVersionParams.addRule((Build.VERSION.SDK_INT >= 17) ? RelativeLayout.ALIGN_PARENT_START : RelativeLayout.ALIGN_PARENT_LEFT, leftButton.getId());
            secondaryVersionParams.addRule((Build.VERSION.SDK_INT >= 17)? RelativeLayout.START_OF : RelativeLayout.LEFT_OF, chapterLayout.getId());
            secondaryVersionParams.height = getSizeForDp(25);
            secondaryVersionParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            secondaryVersionLayout.setLayoutParams(secondaryVersionParams);

            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            toolbarParams.height = getSizeForDp(25);
            toolbar.setLayoutParams(toolbarParams);

            secondaryVersionLayout.setVisibility(View.VISIBLE);
            leftButton.setVisibility(View.GONE);
            rightButtonPlaceholder.setVisibility(View.GONE);
            chapterLayout.setVisibility(View.VISIBLE);
            mainVersionLayout.setVisibility(View.VISIBLE);
        }

        if(chapterText == null || chapterText.length() < 1){
            chapterLayout.setVisibility(View.INVISIBLE);
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
}
