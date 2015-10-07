package model;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Fechner on 10/4/15.
 */
public class AudioMarker implements Comparable<AudioMarker>{

    private int startTime;
    private int duration;

    public AudioMarker(int startTime, int duration) {
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime(){
        return startTime + duration;
    }

    @Override
    public int compareTo(@NonNull AudioMarker another) {
        return (int) (startTime - another.startTime);
    }

    public static List<AudioMarker> createLengths(List<AudioMarker> markers, long totalLength){

        for(int i = 0; i < markers.size(); i++){
            AudioMarker marker = markers.get(i);
            if(i == 0){
                marker.startTime = 0;
            }
            if(i == (markers.size() - 1)){
                marker.duration = (int) (totalLength - marker.startTime);
            }
            else {
                marker.duration = markers.get(i + 1).startTime - marker.startTime;
            }
        }
        return markers;
    }
}
