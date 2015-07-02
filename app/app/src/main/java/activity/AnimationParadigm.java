package activity;

import java.io.Serializable;

/**
 * Created by Fechner on 5/28/15.
 */
public enum AnimationParadigm implements Serializable {
    ANIMATION_STOCK(0), ANIMATION_LEFT_RIGHT(1), ANIMATION_VERTICAL(2), ANIMATION_FORWARD_RIGHT_BACK_DOWN(3), ANIMATION_FORWARD_UP_BACK_LEFT(4);

    final int num;

    AnimationParadigm(int num) {
        this.num = num;
    }

}
