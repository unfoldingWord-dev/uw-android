/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 9/18/15.
 */
public class SimpleVideoPlayerActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_video_player);
        VideoView videoView =(VideoView)findViewById(R.id.simple_video_player_video_view);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
//        Uri uri= Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_video);
//        videoView.setMediaController(mediaController);
//        videoView.setVideoURI(uri);
//        videoView.requestFocus();
//
//        videoView.start();


    }
}
