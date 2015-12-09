/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package singletons;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.AudioMarker;
import model.DataFileManager;
import model.DownloadState;
import model.daoModels.AudioChapter;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;
import model.parsers.AudioMarkerParser;
import model.parsers.MediaType;
import utils.UWFileUtils;
import utils.UWPreferenceDataAccessor;

/**
 * Created by Fechner on 10/5/15.
 */
public class UWAudioPlayer implements UWPreferenceDataAccessor.PreferencesBibleChapterChangedListener, UWPreferenceDataAccessor.PreferencesStoryPageChangedListener {

    private static final String TAG = "UWAudioPlayer";

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
        UWPreferenceDataAccessor.addBibleChapterListener(this);
        UWPreferenceDataAccessor.addStoryPageListener(this);
        listeners = new ArrayList<>();
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

        UWPreferenceDataAccessor.removeBibleChapterListener(this);
        UWPreferenceDataAccessor.removeStoryPageListener(this);
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


        DataFileManager.getStateOfContent(context, page.getStoriesChapter().getBook().getVersion(), MediaType.MEDIA_TYPE_AUDIO, new DataFileManager.GetDownloadStateResponse() {
            @Override
            public void foundDownloadState(DownloadState state) {
                AudioChapter chapter = page.getStoriesChapter().getAudioForChapter();
                if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADED){
//            File audioFile = UWFileUtils.loadSourceFile(chapter.getAudioUrl(), context);
//            Uri uri = Uri.fromFile(audioFile);
                    Uri uri = DataFileManager.getUri(context, page.getStoriesChapter().getBook().getVersion(),
                            MediaType.MEDIA_TYPE_AUDIO, chapter.getDownloadedAudioUrl(context));

                    List<AudioMarker> markers = AudioMarkerParser.createAudioMarkers(uri, chapter.getLength() * 1000);
                    currentModel = page;
                    setupAudio(uri, markers.get(Integer.parseInt(page.getNumber()) - 1));
                }
            }
        });

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
            waitAndUpdatePlayProgress();
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
            UWPreferenceDataAccessor.changedToNewStoriesPage(context, newPage, false);
            UWPreferenceDataAccessor.getOurInstance().updateStoryListeners();
        }
    }

    private void waitAndUpdatePlayProgress(){

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected  Void doInBackground(Void... params) {
                try {
                    synchronized (this) {
                        wait(100);
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

    @Override
    public void bibleChapterChanged(BibleChapter mainChapter, BibleChapter secondaryChapter) {

    }

    @Override
    public void storyPageChanged(StoryPage mainPage, StoryPage secondaryPage) {
        prepareAudio(mainPage);
    }

    public interface UWAudioPlayerListener{

        void update(long duration, long progress);
        void started();
        void paused();
    }
}
