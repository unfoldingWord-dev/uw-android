package peejweej.sideloading.view;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import peejweej.sideloading.R;

/**
 * Created by Fechner on 7/1/15.
 */
public class ToolbarViewGroup {

    private Activity activity;

    private Toolbar toolbar;
    private ImageButton leftButton;

    private LinearLayout titleLayout;
    private TextView titleText;
    private ImageView titleButtonIndicator;


    private UWToolbarListener listener;

    private boolean hidden = false;

    public ToolbarViewGroup(Toolbar toolbar, Activity activity, boolean hasLogo, int backResource, UWToolbarListener listener) {

        this.listener = listener;
        this.toolbar = toolbar;
        this.activity = activity;
        setupViews();
        setVisibilities(hasLogo, backResource);
        if(backResource > 0){
            leftButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    leftButtonClicked();
                }
            });
        }
    }

    private void setupViews(){

        this.leftButton = (ImageButton) activity.findViewById(R.id.toolbar_left_button);

        this.titleLayout = (LinearLayout) activity.findViewById(R.id.toolbar_middle_button_layout);
        this.titleText = (TextView) activity.findViewById(R.id.toolbar_title);
        this.titleButtonIndicator = (ImageView) activity.findViewById(R.id.toolbar_title_button_image);
    }

    private void setVisibilities(boolean hasLogo, int backResource){

        leftButton.setVisibility((backResource > 0)? View.VISIBLE : View.INVISIBLE);
        if(backResource > 0) {
            leftButton.setImageResource(backResource);
        }

        titleLayout.setVisibility(View.GONE);
    }

    public void setBackResource(int resource){
        leftButton.setImageResource(resource);
    }

    public void setTitle(String text, boolean clickable){

        if(text != null) {
            titleText.setText(text);
            if (clickable) {
                titleLayout.setClickable(true);
                titleLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        centerButtonClicked();
                    }
                });
                titleButtonIndicator.setVisibility(View.VISIBLE);
            } else {
                titleButtonIndicator.setVisibility(View.GONE);
                titleLayout.setClickable(false);
            }
            titleLayout.setVisibility(View.VISIBLE);
        }
        else{
            titleLayout.setVisibility(View.GONE);
        }
    }

    public void setBackButtonResource(int resource){
        leftButton.setImageResource(resource);
    }

    private void leftButtonClicked(){

        if(this.listener != null){
            this.listener.leftButtonClicked();
        }
    }

    private void centerButtonClicked(){
        if(this.listener != null){
            this.listener.centerButtonClicked();
        }
    }

    private void rightButtonClicked(){
        if(this.listener != null){
            this.listener.rightButtonClicked();
        }
    }

    public void setBackgroundColor(int color){
        this.toolbar.setBackgroundColor(color);
    }

    public interface UWToolbarListener{

        /**
         * Toolbar's left button was clicked
         */
        void leftButtonClicked();

        /**
         * Toolbar's center button was clicked
         */
        void centerButtonClicked();

        /**
         * Toolbar's right button was clicked
         */
        void rightButtonClicked();
    }
}
