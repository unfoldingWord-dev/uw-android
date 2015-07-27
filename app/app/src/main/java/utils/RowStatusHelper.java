package utils;

import android.content.Context;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 7/27/15.
 */
public class RowStatusHelper {

    public static String getButtonTextForStatus(Context context, int currentStatus){

        switch (currentStatus){
            case 0:
                return context.getString(R.string.verified_button_char);
            case 1:
                return context.getString(R.string.expired_button_char);
            default:
                return context.getString(R.string.x_button_char);
        }
    }

    public static int getColorForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return R.drawable.green_checkmark;
            case 1:
                return R.drawable.yellow_exclamation_point;
            default:
                return R.drawable.red_x_button;
        }
    }

    public static int getColorForState(Context context, int state){

        switch (state){
            case 1:
                return context.getResources().getColor(R.color.black_light);
            case 2:
                return context.getResources().getColor(R.color.cyan);
            default:
                return context.getResources().getColor(R.color.lightgrey);
        }
    }
}
