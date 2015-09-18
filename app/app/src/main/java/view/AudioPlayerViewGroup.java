package view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

/**
 * Created by Fechner on 9/18/15.
 */
public class AudioPlayerViewGroup {

    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private TextView currentTimeTextView;
    private TextView endTimeTextView;
    private Context context;

    private SeekBar seekBar;

    private final Handler handler = new Handler();

    public AudioPlayerViewGroup(Context context, View containingView) {
        this.context = context;
        getViews(containingView);
        setupListeners();
    }

    private void getViews(View containingView){

        playPauseButton = (ImageButton) containingView.findViewById(R.id.audio_player_play_pause_button);
        currentTimeTextView = (TextView) containingView.findViewById(R.id.audio_player_current_time_text_view);
        endTimeTextView = (TextView) containingView.findViewById(R.id.audio_player_end_time_text_view);
        seekBar = (SeekBar) containingView.findViewById(R.id.audio_player_progress);
    }

    private void setupListeners(){

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseClicked();
            }
        });

        mediaPlayer = MediaPlayer.create(context, R.raw.test_audio);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });

        endTimeTextView.setText(Long.toString(mediaPlayer.getDuration()));
        currentTimeTextView.setText("0");
    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            playPauseButton.setImageResource(R.drawable.pause);
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
            seekBar.setProgress(0);
        }
    }

    // This is event handler thumb moving event
    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }

    private void playPauseClicked(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
        }
        else{
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause);
            startPlayProgressUpdater();
        }
    }
}
