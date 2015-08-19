package activity;

import org.unfoldingword.mobile.R;

import java.io.Serializable;

/**
 * Created by PJ Fechner on 5/28/15.
 * Enum to define the desired animation to transition to and from activites
 */
public enum AnimationParadigm implements Serializable {
    ANIMATION_STOCK(0), ANIMATION_LEFT_RIGHT(1), ANIMATION_VERTICAL(2), ANIMATION_FORWARD_RIGHT_BACK_DOWN(3), ANIMATION_FORWARD_UP_BACK_LEFT(4);

    final int num;

    AnimationParadigm(int num) {
        this.num = num;
    }

    public static int getNextAnimationEnter(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.enter_from_right;
            }
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_VERTICAL:
            default: {
                return R.anim.enter_from_bottom;
            }
        }
    }

    public static int getNextAnimationExit(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.exit_on_left;
            }
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_VERTICAL:
            default:{
                return R.anim.enter_center;
            }
        }
    }

    public static int getEndingAnimationEnter(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.left_in;
            }
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_VERTICAL:
            default:{
                return R.anim.enter_center;
            }
        }
    }

    public static int getEndingAnimationExit(AnimationParadigm paradigm){

        switch (paradigm){
            case ANIMATION_FORWARD_UP_BACK_LEFT:
            case ANIMATION_LEFT_RIGHT:{
                return R.anim.right_out;
            }
            case ANIMATION_FORWARD_RIGHT_BACK_DOWN:
            case ANIMATION_VERTICAL:
            default:{
                return R.anim.exit_on_bottom;
            }
        }
    }

}
