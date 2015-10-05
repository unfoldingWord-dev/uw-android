package model;

/**
 * Created by Fechner on 10/4/15.
 */
public class AudioMarker {

    private long startTime;
    private long duration;

    public AudioMarker(long startTime, long duration) {
        this.duration = duration;
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime(){
        return startTime + duration;
    }
}
