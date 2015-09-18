package view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import java.util.zip.Inflater;

/**
 * Created by Fechner on 8/9/15.
 */
public class ReadingTabBar extends UWTabBar{

    private MediaPlayer mediaPlayer;
    private ViewGroup playerLayout;
    private AudioPlayerViewGroup audioPlayer;

    public ReadingTabBar(Context context, int[] buttonImages, ViewGroup layout, BottomBarListener listener) {
        super(context, buttonImages, layout, listener);
    }

    public void showAudioPlayer(){

        if(playerLayout == null){
            addAudioPlayer();
        }
    }

    private void addAudioPlayer(){

        playerLayout = (ViewGroup) View.inflate(getContext(), R.layout.audio_player_layout, getParentLayout());
        audioPlayer = new AudioPlayerViewGroup(playerLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(playerLayout.getLayoutParams());
        params.addRule(RelativeLayout.ABOVE, getBaseLayout().getId());

        ViewGroup.LayoutParams parentParams = getParentLayout().getLayoutParams();
        parentParams.height = getSizeForDp(90);
        getParentLayout().setLayoutParams(parentParams);
    }

    public void showTextSizeChooser(){

    }

    private class AudioPlayerViewGroup {

        private ImageButton playPauseButton;
        private TextView currentTimeTextView;
        private TextView endTimeTextView;

        private SeekBar seekBar;

        private final Handler handler = new Handler();

        public AudioPlayerViewGroup(View containingView) {

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

            mediaPlayer = MediaPlayer.create(getContext(), R.raw.test_audio);
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    seekChange(v);
                    return false;
                }
            });
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

    private int getSizeForDp(int sizeInDP){
        return (int) (sizeInDP * getContext().getResources().getDisplayMetrics().density + 0.5f) ;
    }
}
