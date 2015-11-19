package enums;

import org.unfoldingword.mobile.R;

import model.daoModels.Version;

/**
 * Created by Fechner on 11/19/15.
 */
public enum ResourceType {

    RESOURCE_TYPE_NONE(0), RESOURCE_TYPE_TEXT(1), RESOURCE_TYPE_AUDIO(2), RESOURCE_TYPE_VIDEO(3);

    int num;

    ResourceType(int num) {
        this.num = num;
    }

    public static int getImageResourceForType(ResourceType type){

        switch (type){
            case RESOURCE_TYPE_AUDIO: return R.drawable.audio_active;
            case RESOURCE_TYPE_VIDEO: return R.drawable.video_active;
            default: return -1;
        }
    }

    public static String getTitle(ResourceType type){

        switch (type) {
            case RESOURCE_TYPE_AUDIO:
                return "Audio";
            case RESOURCE_TYPE_VIDEO:
                return "Video";
            default:
                return "";
        }
    }

}
