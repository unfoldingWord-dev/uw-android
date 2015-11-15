/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ASyncImageView extends ImageView {

    public ASyncImageView(Context context) {
        super(context);
    }

    public ASyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ASyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setImageUrl(final String url, boolean crop){

        Picasso picasso = Picasso.with(getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        if(crop) {
            picasso.load(url).fit().centerCrop().into(this);
        }
        else{
            picasso.load(url).fit().centerInside().into(this);
        }
    }

    public void setLocalImage(final Uri uri, boolean crop){

        Picasso picasso = Picasso.with(getContext());
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        if(crop) {
            picasso.load(uri).centerCrop().into(this);
        }
        else{
            picasso.load(uri).into(this);
        }
    }

    public void setImageUrl(final String url ){

        Picasso picasso = Picasso.with(getContext());
        picasso.load(url).into(this);
    }
}
