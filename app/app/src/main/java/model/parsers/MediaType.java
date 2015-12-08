package model.parsers;

import org.unfoldingword.mobile.R;

import java.io.File;

/**
 * Created by Fechner on 11/23/15.
 */
public enum MediaType {

    MEDIA_TYPE_NONE(0), MEDIA_TYPE_TEXT(1), MEDIA_TYPE_AUDIO(2), MEDIA_TYPE_VIDEO(3);
    int num;



    MediaType(int num) {
        this.num = num;
    }

    private static final String PATH_FOR_TEXT =  "text";
    private static final String PATH_FOR_AUDIO = "audio";
    private static final String PATH_FOR_VIDEO = "video";

    public String getPathForType(){

        switch (this){
            case MEDIA_TYPE_TEXT: return PATH_FOR_TEXT;
            case MEDIA_TYPE_AUDIO: return PATH_FOR_AUDIO;
            case MEDIA_TYPE_VIDEO: return PATH_FOR_VIDEO;
            default: return  "";
        }
    }

    public static int getImageResourceForType(MediaType type){

        switch (type){
            case MEDIA_TYPE_AUDIO: return R.drawable.audio_icon;
            case MEDIA_TYPE_VIDEO: return R.drawable.video_icon;
            default: return -1;
        }
    }

    public String getTitle(){

        switch (this) {
            case MEDIA_TYPE_AUDIO:
                return "Audio";
            case MEDIA_TYPE_VIDEO:
                return "Video";
            case MEDIA_TYPE_TEXT:
            default:
                return "";
        }
    }

}
