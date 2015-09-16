package view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

import model.daoModels.Version;

/**
 * Created by Fechner on 8/9/15.
 */
public class UWTabBar {

    public interface BottomBarListener{

        void buttonPressedAtIndex(int index);
    }

    private Context context;
    private boolean hidden = false;

    private ViewGroup parentLayout;
    private LinearLayout baseLayout;
    private List<ImageButton> buttons;
    private BottomBarListener listener;

    public UWTabBar(Context context, int[] buttonImages, ViewGroup layout, BottomBarListener listener) {
        this.context = context;
        parentLayout = layout;
        this.listener = listener;
        setupViews(buttonImages);
    }

    public Context getContext() {
        return context;
    }

    public ViewGroup getParentLayout() {
        return parentLayout;
    }

    public LinearLayout getBaseLayout() {
        return baseLayout;
    }

    private void setupViews(int[] buttons){

        setupBaseLayout();
        setupButtons(buttons);
    }

    private void setupBaseLayout(){

        baseLayout = new LinearLayout(context);
        baseLayout.setBackgroundResource(R.color.primary);
        baseLayout.setWeightSum((float) 1.0);
        parentLayout.addView(baseLayout);
    }

    private void setupButtons(int[] buttonImageRes){

        buttons = new ArrayList<>(buttonImageRes.length);

        for(int i = 0; i < buttonImageRes.length; i++){

            ImageButton button = createButton(i, buttonImageRes[i], ((float) 1.0 / (float) buttonImageRes.length));
            buttons.add(i, button);
            baseLayout.addView(button);
        }
    }

    private ImageButton createButton(int index, int imageResource, float weight){

        ImageButton button = new ImageButton(context);
        button.setBackgroundResource(R.drawable.basic_button_selector);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        button.setOnClickListener(new TabButtonListener(index));
        button.setImageResource(imageResource);
        return button;
    }

    public void toggleHidden(){

        setHidden(!hidden);
    }

    public void setHidden(boolean isHidden){

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) parentLayout.getLayoutParams();
        if(isHidden){
            params.addRule(RelativeLayout.BELOW, R.id.bottom_marker_layout);
        }
        else{
            params.removeRule(RelativeLayout.BELOW);
        }

        parentLayout.setLayoutParams(params);
        hidden = !hidden;
    }

    private class TabButtonListener implements View.OnClickListener{

        final int index;

        public TabButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            listener.buttonPressedAtIndex(index);
        }
    }
}
