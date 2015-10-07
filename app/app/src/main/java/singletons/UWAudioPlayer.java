package singletons;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.AudioMarker;
import model.daoModels.AudioChapter;
import model.daoModels.BibleChapter;
import model.daoModels.StoryPage;
import model.parsers.AudioMarkerParser;
import utils.UWFileUtils;
import utils.UWPreferenceDataAccessor;

/**
 * Created by Fechner on 10/5/15.
 */
public class UWAudioPlayer implements UWPreferenceDataAccessor.PreferencesBibleChapterChangedListener, UWPreferenceDataAccessor.PreferencesStoryPageChangedListener {

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
            mediaPlayer.seekTo(timeInSeconds * 1000);
            updatePlayProgress();
        }
    }

    private void resetMediaPLayer(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
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
            return (mediaPlayer.getCurrentPosition() / 1000) - currentMarker.getStartTime();
        }

        else return -1;
    }

    public void setupAudio(StoryPage page){

        AudioChapter chapter = page.getStoriesChapter().getAudioForChapter();
        if(chapter != null){
            File audioFile = UWFileUtils.loadSourceFile(chapter.getAudioUrl(), context);
            Uri uri = Uri.fromFile(audioFile);

            List<AudioMarker> markers = AudioMarkerParser.createAudioMarkers(uri, chapter.getLength());
            currentModel = page;
            startAudio(uri, markers.get(Integer.parseInt(page.getNumber()) - 1));
        }
    }

    private void startAudio(Uri audioUri, AudioMarker marker){

        boolean isPlaying = mediaPlayer.isPlaying();
        currentMarker = marker;
        if(currentModel == null || !audioUri.getPath().equalsIgnoreCase(currentUri.getPath())) {
            resetMediaPLayer();
            mediaPlayer = MediaPlayer.create(context, audioUri);
            currentUri = audioUri;
        }

        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;

        // seek to start time if the current time isn't within a second of the start time
        if(currentPosition >= marker.getStartTime() + 1 || currentPosition <= marker.getStartTime() -1){
            mediaPlayer.seekTo((int) marker.getStartTime() * 1000);
        }

        if(isPlaying){
            mediaPlayer.start();
            updatePlayProgress();
        }
    }

    private void updatePlayProgress(){

        long currentPosition = currentMarker.getEndTime() - mediaPlayer.getCurrentPosition();
        long duration = currentMarker.getDuration();
        boolean markerIsComplete = currentPosition <= 0;

        if(markerIsComplete){
            goToNextPage();
        }
        else {
            for (UWAudioPlayerListener listener : listeners) {
                if (listener != null) {
                    listener.update(duration, currentPosition);
                }
            }
            waitAndUpdatePlayProgress();
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
        StoryPage newPage = currentModel.getStoriesChapter().getStoryPageForNumber(Integer.toString(Integer.parseInt(currentModel.getNumber()) + 1));

        if(newPage != null){
            UWPreferenceDataAccessor.changedToNewStoriesPage(context, newPage, false);
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

    @Override
    public void bibleChapterChanged(BibleChapter mainChapter, BibleChapter secondaryChapter) {

    }

    @Override
    public void storyPageChanged(StoryPage mainPage, StoryPage secondaryPage) {
        setupAudio(mainPage);
    }

    public interface UWAudioPlayerListener{

        void update(long duration, long progress);
        void started();
        void paused();
    }
}
