/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package view;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import org.unfoldingword.mobile.R;

import java.io.IOException;
import java.io.InputStream;

import signing.Status;

/**
 * Created by Fechner on 7/6/15.
 */
public class ViewContentHelper {

    //region images
    /**
     * @param level Checking level of the version being shown
     * @return dark Image resource for the passed checking level
     */
    static public int getDarkCheckingLevelImageResource(int level){
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

    /**
     * @param level Checking level of the version being shown
     * @return Normal Checking level image for the passed level
     */
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

    /**
     * @param currentStatus verification status of the version being shown
     * @return The resource for the passed status
     */
    public static int getDrawableForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return R.drawable.green_checkmark;
            case 1:
                return R.drawable.yellow_exclamation_point;
            default:
                return R.drawable.red_x_button;
        }
    }

    //endregion

    //region text

    /**
     * @param currentStatus verification status of version being shown
     * @return Single character to put in the status button.
     */
    public static int getVerificationButtonTextForStatus(int currentStatus){

        switch (currentStatus){
            case 0:
                return R.string.verified_button_char;
            case 1:
                return R.string.expired_button_char;
            default:
                return R.string.x_button_char;
        }
    }

    /**
     * @param level Checking level of the version being shown
     * @return the text explaining the checking level
     */
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

    /**
     *
     * @param status status for which you want text
     * @param title title you want to use in the text
     * @return text based ont he status
     */
    public static String getTextForStatus(Status status, String title) {

        switch (status) {

            case VERIFIED: {
                return "";
            }
            case EXPIRED: {
                return "Verification for " + title + " has Expired\n";
            }
            case ERROR: {
                return "Error Verifying " + title + "\n";
            }
            default: {
                return "Failed to Verify " + title + "\n";
            }
        }
    }

    //endregion

    //region colors

    /**
     * @param selected if the row is selected
     * @return Color resource for the passed row selection
     */
    public static int getColorForSelection(boolean selected){

        if(selected){
            return R.color.primary;
        }
        else {
            return R.color.black;
        }
    }

    /**
     * @param context context with the desired assets
     * @param strName image name
     * @return Created Bitmap or null if it could not be found
     */
    @Nullable
    public static Bitmap getBitmapFromAsset(Context context, String strName)
    {
        AssetManager assetManager = context.getAssets();

        InputStream input;
        try {
            input = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return BitmapFactory.decodeStream(input);
    }

    //endregion
}
