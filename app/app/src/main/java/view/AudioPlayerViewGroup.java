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

/**
 * Created by Fechner on 9/18/15.
 */
public class AudioPlayerViewGroup {

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

    private boolean hasChangedMediaPlayers;

    public AudioPlayerViewGroup(Context context, MediaPlayer mediaPlayer, View containingView, AudioPlayerViewGroupListener listener) {
        this.context = context;
        this.listener = listener;
        getViews(containingView);
        setupListeners();
        setupForMediaPlayer();
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
    }

    private void setupForMediaPlayer(){

        if(listener.getMediaPlayer() != null){
            int duration = listener.getMediaPlayer().getDuration();
            seekBar.setProgress(0);
            seekBar.setMax(duration);

            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    seekChange(v);
                    return false;
                }
            });

            // avoid updating after a media player change since time can get weird.
            updateLabelsForTimes();
            playPauseButton.setImageResource(R.drawable.play);
        }
    }

    public void setupMediaPlayer() {
        hasChangedMediaPlayers = true;
        setupForMediaPlayer();
        setupListeners();
    }

    public void updatePlayProgress() {

        if(!hasChangedMediaPlayers) {
            seekBar.setProgress(listener.getMediaPlayer().getCurrentPosition());

            if (listener.getMediaPlayer().isPlaying()) {
                playPauseButton.setImageResource(R.drawable.pause);
                waitAndUpdatePlayProgress();
            }else{
                listener.getMediaPlayer().pause();
                playPauseButton.setImageResource(R.drawable.play);
                seekBar.setProgress(listener.getMediaPlayer().getCurrentPosition());
                this.listener.audioPlayerStateChanged(false);
            }

            updateLabelsForTimes();
        }
    }

    private void waitAndUpdatePlayProgress(){

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected  Void doInBackground(Void... params) {
                try {
                    synchronized (this) {
                        wait(1000);
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                updatePlayProgress();
            }
        }.execute();
    }

    // This is event handler thumb moving event
    private void seekChange(View v){
        if(listener.getMediaPlayer().isPlaying()){
            SeekBar sb = (SeekBar)v;
            listener.getMediaPlayer().seekTo(sb.getProgress());
        }
        updateLabelsForTimes();
    }

    private void playPauseClicked(){
        hasChangedMediaPlayers = false;
        if(listener.getMediaPlayer().isPlaying()){
            listener.getMediaPlayer().pause();
            playPauseButton.setImageResource(R.drawable.play);
            this.listener.audioPlayerStateChanged(false);
        }
        else{
            listener.getMediaPlayer().start();
            playPauseButton.setImageResource(R.drawable.pause);
            updatePlayProgress();
            this.listener.audioPlayerStateChanged(true);
        }
        updateLabelsForTimes();
    }

    private void updateLabelsForTimes() {
        if(listener.getMediaPlayer() != null){
            updateLabelsForTimes(listener.getMediaPlayer().getCurrentPosition(), listener.getMediaPlayer().getDuration());
        }
    }

    private void updateLabelsForTimes(long elapsedInMilli, long totalInMilli){

        long elapsed = elapsedInMilli / 1000;
        long total = totalInMilli / 1000;

        String currentTime = getTimeStringFromSeconds(elapsed);
        currentTimeTextView.setText(currentTime);
        String endTime = getTimeStringFromSeconds(total);
        endTimeTextView.setText(endTime);
    }

    private String getTimeStringFromSeconds(long seconds){
        long numOfSeconds = seconds % 60;
        String secondsText = (numOfSeconds < 10)? "0" + Long.toString(numOfSeconds) : Long.toString(numOfSeconds);
        return Long.toString((long) Math.floor(seconds / 60.0)) + ":" + secondsText;
    }

    public void pausePlayback(){

        if(listener.getMediaPlayer() != null) {
            listener.getMediaPlayer().pause();
        }
    }

    public void stopPlayback() {
        if(listener.getMediaPlayer() != null) {
            listener.getMediaPlayer().stop();
            seekBar.setProgress(0);
            hasChangedMediaPlayers = true;
            updateLabelsForTimes();
        }
    }

    public void resetViews(){
        controlsViewGroup.setVisibility(View.VISIBLE);
        downloadViewGroup.setVisibility(View.GONE);
    }

    public void setDownloading(){
        controlsViewGroup.setVisibility(View.GONE);
        downloadViewGroup.setVisibility(View.VISIBLE);
        downloadingAudioViewGroup.setVisibility(View.VISIBLE);
        downloadButton.setVisibility(View.GONE);
    }

    public void setNeedsToDownload(){

        downloadButton.setVisibility(View.VISIBLE);
        controlsViewGroup.setVisibility(View.GONE);
        downloadViewGroup.setVisibility(View.VISIBLE);
        downloadingAudioViewGroup.setVisibility(View.GONE);
    }

    public interface AudioPlayerViewGroupListener{
        void audioPlayerStateChanged(boolean isPlaying);
        void downloadClicked();
        MediaPlayer getMediaPlayer();
    }
}
