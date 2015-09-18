package view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Fechner on 8/9/15.
 */
public class ReadingTabBar extends UWTabBar{


    private AudioPlayerViewGroup audioPlayer;
    private ViewGroup audioPlayerLayout;

    public ReadingTabBar(Context context, int[] buttonImages, ViewGroup layout, ViewGroup audioPlayerLayout, BottomBarListener listener) {
        super(context, buttonImages, layout, listener);
        this.audioPlayerLayout = audioPlayerLayout;
    }

    public void showTextSizeChooser(){

    }

    private int getSizeForDp(int sizeInDP){
        return (int) (sizeInDP * getContext().getResources().getDisplayMetrics().density + 0.5f) ;
    }
}
