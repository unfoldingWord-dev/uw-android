package view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Fechner on 10/30/15.
 */
public class ScalingImageView  extends ImageView{

    public ScalingImageView(Context context) {
        super(context);
    }

    public ScalingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        int newHeight = heightMeasureSpec;
        int newWidth = widthMeasureSpec;
        if (drawable != null) {
            float actualAspect = getRatio(drawable);

            // Assuming the width is ok, so we calculate the height.
             int actualWidth = MeasureSpec.getSize(widthMeasureSpec);
             int height = (int) (actualWidth / actualAspect);

            if(height > heightMeasureSpec){
                height = MeasureSpec.getSize(heightMeasureSpec);
                actualWidth =  height * (int) actualAspect;
            }
            newWidth = MeasureSpec.makeMeasureSpec(actualWidth, MeasureSpec.EXACTLY);
            newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(newWidth, newHeight);
    }

    private static float getRatio(Drawable drawable){

        return (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
    }
}
