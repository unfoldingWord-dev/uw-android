package view;

import android.content.res.Resources;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 8/20/15.
 */
public class ReadingDoubleTapHandler implements View.OnTouchListener {

    private ReadingDoubleTapHandlerListener listener;

    private Handler handler = new Handler();
    private int numberOfTaps = 0;
    private long lastTapTimeMs = 0;
    private long touchDownMs = 0;

    private int tapTimeout;
    private int doubleTapTimeout;

    public ReadingDoubleTapHandler(Resources resources, ReadingDoubleTapHandlerListener listener) {

        this.listener = listener;
        tapTimeout = resources.getInteger(R.integer.tap_timeout);
        doubleTapTimeout = resources.getInteger(R.integer.double_tap_timeout);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownMs = System.currentTimeMillis();
                if ((numberOfTaps > 0)
                        && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                handler.removeCallbacksAndMessages(null);

                if ((System.currentTimeMillis() - touchDownMs) > tapTimeout) {
                    //it was not a tap

                    numberOfTaps = 0;
                    lastTapTimeMs = 0;
                    break;
                }

                if ((numberOfTaps > 0)
                        && (System.currentTimeMillis() - lastTapTimeMs) < doubleTapTimeout) {
                    numberOfTaps += 1;
                } else {
                    numberOfTaps = 1;
                }

                lastTapTimeMs = System.currentTimeMillis();

//                        if(numberOfTaps == 1){
//                            checkShouldChangeNavBarHidden();
//                            return false;
//                        }

                if (numberOfTaps == 2) {
                    if (listener != null) {
                        return listener.doubleTapWasRegistered();
                    }
                }
            }
        }

        return false;
    }

    public interface ReadingDoubleTapHandlerListener{

        /**
         * Called when the handler registers a double tap
         * @return True if the listener has consumed the event
         */
        boolean doubleTapWasRegistered();
    }
}
