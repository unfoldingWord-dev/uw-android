package view;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.unfoldingword.mobile.R;

import fragments.ReadingFragmentListener;
import model.daoModels.Version;

/**
 * Created by Fechner on 8/9/15.
 */
public class ReadingBottomBarViewGroup {

    public interface BottomBarListener{
        void checkingLevelPressed();
        void versionButtonClicked();
        void shareButtonClicked();
    }

    private boolean hidden = false;

    private RelativeLayout baseLayout;
    private ImageButton checkingLevelButton;
    private TextView versionTextView;
    private BottomBarListener listener;

    public ReadingBottomBarViewGroup(Context context, RelativeLayout layout, Version version, BottomBarListener listener) {
        baseLayout = layout;
        this.listener = listener;
        setupViews(context);
        updateWithVersion(version);
    }

    private void setupViews(Context context){

        this.checkingLevelButton = (ImageButton) baseLayout.findViewById(R.id.bottom_bar_level_button);
        this.versionTextView = (TextView) baseLayout.findViewById(R.id.bottom_bar_left_button_text_view);
        checkingLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.checkingLevelPressed();
            }
        });

        baseLayout.findViewById(R.id.bottom_bar_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.versionButtonClicked();
            }
        });

        ImageButton shareButton = (ImageButton) baseLayout.findViewById(R.id.bottom_bar_share_button);
        shareButton.setImageDrawable(new IconDrawable(context, FontAwesomeIcons.fa_share).colorRes(R.color.white).sizeDp(30));

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.shareButtonClicked();
            }
        });
    }

    public void updateWithVersion(Version version){
        this.checkingLevelButton.setImageResource(ViewHelper.getCheckingLevelImage(Integer.parseInt(version.getStatusCheckingLevel())));
        this.versionTextView.setText(version.getName());
    }

    public void toggleHidden(){

        setHidden(!hidden);
    }

    public void setHidden(boolean hide){

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseLayout.getLayoutParams();
        if(hide){
            params.addRule(RelativeLayout.BELOW, R.id.bottom_marker_layout);
        }
        else{
            params.removeRule(RelativeLayout.BELOW);
        }

        baseLayout.setLayoutParams(params);
        hidden = !hidden;

    }
}
