package view;

import android.content.Context;

import org.unfoldingword.mobile.R;

import model.daoModels.Version;

/**
 * Created by Fechner on 7/6/15.
 */
public class ViewDataHelper {

    static public int getCheckingLevelText(int level){

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

    static public int getDarkCheckingLevelImage(int level){
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

    public static String getButtonTextForStatus(int currentStatus, Context context){

        switch (currentStatus){
            case 0:
                return context.getResources().getString(R.string.verified_button_char);
            case 1:
                return context.getResources().getString(R.string.expired_button_char);
            default:
                return context.getResources().getString(R.string.x_button_char);
        }
    }

    public static int getCheckingLevelImage(int level){

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
}
