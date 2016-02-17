/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package singletons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import eventbusmodels.BiblePagingEvent;
import eventbusmodels.StoriesPagingEvent;
import model.AudioMarker;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.AudioChapter;
import model.daoModels.StoryPage;
import model.parsers.AudioMarkerParser;
import model.parsers.MediaType;

/**
 * Created by Fechner on 10/5/15.
 */
public class UWAudioPlayer {

    private static final String TAG = "UWAudioPlayer";

    private static final int REFRESH_TIME_IN_MILLI = 200;

    private static UWAudioPlayer ourInstance;
    public static UWAudioPlayer getInstance(Context context) {

        if(ourInstance == null){
            ourInstance = new UWAudioPlayer(context);
        }
        return ourInstance;
    }

    private StoryPage currentModel;

    private Context context;
    private List<UWAudioPlayerListener> listeners;
    private MediaPlayer mediaPlayer;
    private AudioMarker currentMarker;
    private Uri currentUri;

    private UWAudioPlayer(Context context) {

        this.context = context;
        listeners = new ArrayList<>();
        registerEventListeners();
    }

    private void registerEventListeners(){
        EventBus.getDefault().register(this, 1);
    }

    public void unregisterEventListeners(){
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BiblePagingEvent event){

    }

    public void onEvent(StoriesPagingEvent event){

        prepareAudio(event.mainStoryPage);
    }

    public void addListener(UWAudioPlayerListener listener){

        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(UWAudioPlayerListener listener){

        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    public void reset(){

        unregisterEventListeners();
        resetMediaPLayer();
        ourInstance = null;
    }

    public boolean isPlaying(){
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void seekTo(int timeInSeconds){
        if(mediaPlayer != null){
            mediaPlayer.seekTo((int) (timeInSeconds + currentMarker.getStartTime()));
            updatePlayProgress(false);
        }
    }

    private void resetMediaPLayer(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public AudioMarker getCurrentMarker() {
        return currentMarker;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void play(){
        if(mediaPlayer != null) {
            mediaPlayer.start();
            notifyPlay();
            updatePlayProgress(true);
        }
    }

    public void pause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            notifyPause();
        }
    }

    public void updatePlayPause(){
        if(isPlaying()){
            notifyPause();
        }
        else{
            notifyPlay();
        }
    }

    public void togglePlay(){

        if(isPlaying()){
            pause();
        }
        else{
            play();
        }
    }

    public int getCurrentTime(){

        if(mediaPlayer != null && currentMarker != null){
            return mediaPlayer.getCurrentPosition() - (int) currentMarker.getStartTime();
        }
        else {
            return -1;
        }
    }

    public void prepareAudio(final StoryPage page){

        if(currentModel == null || !page.getId().equals(currentModel.getId())) {

            DataFileManager.getStateOfContent(context, page.getStoriesChapter().getBook().getVersion(), MediaType.MEDIA_TYPE_AUDIO, new DataFileManager.GetDownloadStateResponse() {
                @Override
                public void foundDownloadState(DownloadState state) {
                    AudioChapter chapter = page.getStoriesChapter().getAudioForChapter();
                    if (state == DownloadState.DOWNLOAD_STATE_DOWNLOADED) {
//            File audioFile = UWFileUtils.loadSourceFile(chapter.getAudioUrl(), context);
//            Uri uri = Uri.fromFile(audioFile);
                        Uri uri = DataFileManager.getUri(context, page.getStoriesChapter().getBook(),
                                MediaType.MEDIA_TYPE_AUDIO, chapter.getDownloadedAudioUrl(context));

                        List<AudioMarker> markers = AudioMarkerParser.createAudioMarkers(uri, chapter.getLength() * 1000);
                        currentModel = page;
                        setupAudio(uri, markers.get(Integer.parseInt(page.getNumber()) - 1));
                    }
                }
            });
        }
    }

    private void setupAudio(Uri audioUri, AudioMarker marker){

        boolean wasPlaying = mediaPlayer != null && mediaPlayer.isPlaying();
        currentMarker = marker;
        if(currentModel == null || currentUri == null || !audioUri.getPath().equalsIgnoreCase(currentUri.getPath())) {
            resetMediaPLayer();
            mediaPlayer = MediaPlayer.create(context, audioUri);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    notifyPause();
                    currentUri = null;
                    prepareAudio(currentModel);
                }
            });

            currentUri = audioUri;
        }

        int currentPosition = mediaPlayer.getCurrentPosition();

        // seek to start time if the current time isn't within a second of the start time
        if(currentPosition >= marker.getStartTime() + 1000 || currentPosition <= marker.getStartTime() - 1000){
            mediaPlayer.seekTo((int) marker.getStartTime());
//            int currentTime = mediaPlayer.getCurrentPosition();
//            Log.i(TAG, "current  time: " + currentTime);
        }

        if(wasPlaying){
            mediaPlayer.start();
        }
        updatePlayProgress(false);
    }

    private void updatePlayProgress(boolean autoUpdate){

        if(mediaPlayer == null){
            return;
        }
        long currentPosition = (mediaPlayer.getCurrentPosition()) - currentMarker.getStartTime();
        long duration = currentMarker.getDuration();
        boolean markerIsComplete = currentPosition >= duration;

        if(markerIsComplete && autoUpdate){
            goToNextPage();
        }
        else {
            updateListeners(duration, currentPosition);
            if(isPlaying()) {
                waitAndUpdatePlayProgress();
            }
            else{
                notifyPause();
            }
        }
    }

    private void updateListeners(long duration, long currentPosition){

        for (UWAudioPlayerListener listener : listeners) {
            if (listener != null) {
                listener.update(duration, currentPosition);
            }
        }
    }

    private void notifyPlay(){

        for (UWAudioPlayerListener listener : listeners) {
            if (listener != null) {
                listener.started();
            }
        }
    }

    private void notifyPause(){

        for (UWAudioPlayerListener listener : listeners) {
            if (listener != null) {
                listener.paused();
            }
        }
    }

    private void goToNextPage(){
        StoryPage newPage = currentModel.getNextStoryPage();

        if(newPage != null){
            StoriesPagingEvent event = StoriesPagingEvent.getStickyEvent(context);
            EventBus.getDefault().postSticky(new StoriesPagingEvent(newPage, event.secondaryStoryPage));
        }
    }

    private void waitAndUpdatePlayProgress(){

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected  Void doInBackground(Void... params) {
                try {
                    synchronized (this) {
                        wait(REFRESH_TIME_IN_MILLI);
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
                updatePlayProgress(true);
            }
        }.execute();
    }

    public interface UWAudioPlayerListener{

        void update(long duration, long progress);
        void started();
        void paused();
    }


    public static class RemoteControlReceiver extends BroadcastReceiver {
        static boolean clickedHeadPhone = false;

        public RemoteControlReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (event != null) {
                    int keyCode = event.getKeyCode();
                    if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY || keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {

                        if(keyCode == KeyEvent.KEYCODE_HEADSETHOOK){
                            clickedHeadPhone = !clickedHeadPhone;
                            if(clickedHeadPhone){
                                return;
                            }
                        }

                        if(ourInstance != null) {
                            ourInstance.togglePlay();
                        }
//                        if (player != null) {
//                            if(player.isPlaying()) {
//                                player.pause();
//                            } else {
//                                player.start();
//                            }
//                        }
                    }
                }
            }
        }
    }
}
