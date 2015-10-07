package view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.AudioMarker;
import model.DownloadState;
import singletons.UWAudioPlayer;

/**
 * Created by Fechner on 9/18/15.
 */
public class AudioPlayerViewGroup implements UWAudioPlayer.UWAudioPlayerListener{

    private ViewGroup downloadViewGroup;
    private ViewGroup downloadingAudioViewGroup;
    private ViewGroup controlsViewGroup;
    private ImageButton playPauseButton;
    private TextView currentTimeTextView;
    private TextView endTimeTextView;
    private Button downloadButton;
    private Context context;

    private SeekBar seekBar;
    private AudioPlayerViewGroupListener listener;

    public AudioPlayerViewGroup(Context context, View containingView, AudioPlayerViewGroupListener listener) {
        this.context = context;
        this.listener = listener;
        getViews(containingView);
        setupListeners();
        updateViews();
    }

    public void resume(){
        UWAudioPlayer.getInstance(context).addListener(this);
    }

    public void onPause(){
        UWAudioPlayer.getInstance(context).removeListener(this);
    }

    private void getViews(View containingView){

        playPauseButton = (ImageButton) containingView.findViewById(R.id.audio_player_play_pause_button);
        currentTimeTextView = (TextView) containingView.findViewById(R.id.audio_player_current_time_text_view);
        endTimeTextView = (TextView) containingView.findViewById(R.id.audio_player_end_time_text_view);
        seekBar = (SeekBar) containingView.findViewById(R.id.audio_player_progress);
        downloadButton = (Button) containingView.findViewById(R.id.reading_audio_download_button);

        downloadViewGroup = (ViewGroup) containingView.findViewById(R.id.download_audio_layout);
        downloadingAudioViewGroup = (ViewGroup) containingView.findViewById(R.id.downloading_audio_layout);
        controlsViewGroup = (ViewGroup) containingView.findViewById(R.id.audio_player_controls);
    }

    private void setupListeners(){

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.downloadClicked();
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseClicked();
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange();
                return false;
            }
        });
        UWAudioPlayer.getInstance(context).addListener(this);
    }

    private void updateViews(){
        updateSeekBar();
        updatePlayPause();
    }

    private void updateSeekBar(){
        AudioMarker marker = UWAudioPlayer.getInstance(context).getCurrentMarker();
        int progress = UWAudioPlayer.getInstance(context).getCurrentTime();
        if(marker != null && progress >= 0){
            seekBar.setMax(marker.getDuration());
            seekBar.setProgress(progress);
            updateLabelsForTimes(progress, marker.getEndTime());
        }
    }

    private void updateSeekBar(int duration, int progress){
        seekBar.setMax(duration);
        seekBar.setProgress(progress);
        updateLabelsForTimes(progress, duration);
    }

    private void updatePlayPause(){

        boolean playing = UWAudioPlayer.getInstance(context).isPlaying();
        playPauseButton.setImageResource((playing) ? R.drawable.pause : R.drawable.play);
    }

    private void seekChange(){
        UWAudioPlayer.getInstance(context).seekTo(seekBar.getProgress());
    }

    private void playPauseClicked(){

        UWAudioPlayer.getInstance(context).togglePlay();
    }

    private void updateLabelsForTimes(long elapsedInSeconds, long totalInSeconds){

        String currentTime = getTimeStringFromSeconds(elapsedInSeconds);
        currentTimeTextView.setText(currentTime);
        String endTime = getTimeStringFromSeconds(totalInSeconds);
        endTimeTextView.setText(endTime);
    }

    private String getTimeStringFromSeconds(long seconds){
        long numOfSeconds = seconds % 60;
        String secondsText = (numOfSeconds < 10)? "0" + Long.toString(numOfSeconds) : Long.toString(numOfSeconds);
        return Long.toString((long) Math.floor(seconds / 60.0)) + ":" + secondsText;
    }

    public void resetViews(){
        controlsViewGroup.setVisibility(View.VISIBLE);
        downloadViewGroup.setVisibility(View.GONE);
    }

    public void handleDownloadState(DownloadState state){

        if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADED){
            resetViews();
        }
        else{
            controlsViewGroup.setVisibility(View.GONE);
            downloadViewGroup.setVisibility(View.VISIBLE);
            downloadingAudioViewGroup.setVisibility((state == DownloadState.DOWNLOAD_STATE_DOWNLOADING)? View.VISIBLE : View.GONE);
            downloadButton.setVisibility((state == DownloadState.DOWNLOAD_STATE_DOWNLOADING)? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void paused() {
        updatePlayPause();
    }

    @Override
    public void update(long duration, long progress) {
        updateLabelsForTimes(duration, progress);
    }

    @Override
    public void started() {
        updatePlayPause();
    }

    public interface AudioPlayerViewGroupListener{
        void downloadClicked();
    }
}
