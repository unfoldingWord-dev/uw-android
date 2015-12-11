package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Fechner on 12/11/15.
 */
public abstract class ViewHolderPrototype {

    abstract int getRowLayoutResource();
    abstract void updateWithModel(Object model);
    abstract void setupWithView(View view);

    public ViewHolderPrototype(Context context, ViewGroup parent) {

        View view = View.inflate(context, getRowLayoutResource(), parent);
        setupWithView(view);
    }
}
