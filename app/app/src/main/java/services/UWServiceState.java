package services;

/**
 * Created by Fechner on 6/7/15.
 */
public enum UWServiceState {

    STATE_NONE(0), STATE_RUNNING(1), STATE_FINISHED(2), STATE_ERROR(3);

    final int num;

    private UWServiceState(int num){
        this.num = num;
    }
}
