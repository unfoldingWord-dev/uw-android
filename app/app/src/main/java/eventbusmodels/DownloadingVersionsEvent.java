package eventbusmodels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * Created by Fechner on 12/15/15.
 */
public class DownloadingVersionsEvent {

    private Map<String, DownloadTrackingModel> models;

    public DownloadingVersionsEvent() {
    }

    public Map<String, DownloadTrackingModel> getModels() {
        if(models == null){
            models = new HashMap<>();
        }
        return models;
    }

    private void addModel(Version version, MediaType type){
        getModels().put(getKey(version, type), new DownloadTrackingModel(version, type));
    }

    private void removeModel(Version version, MediaType type){

        getModels().remove(getKey(version, type));
    }

    private boolean containsModel(Version version, MediaType type){
        return getModels().containsKey(getKey(version, type));
    }

    private static String getKey(Version version, MediaType type){
        return version.getSlug() + type.getName();
    }

    public static DownloadingVersionsEvent getEventAdding(Version version, MediaType type){

        DownloadingVersionsEvent event = EventBus.getDefault().getStickyEvent(DownloadingVersionsEvent.class);
        if(event == null){
            event = new DownloadingVersionsEvent();
        }
        event.addModel(version, type);
        return event;
    }

    public static DownloadingVersionsEvent getEventRemoving(Version version, MediaType type){

        DownloadingVersionsEvent event = EventBus.getDefault().getStickyEvent(DownloadingVersionsEvent.class);
        if(event == null){
            return new DownloadingVersionsEvent();
        }
        event.removeModel(version, type);
        return event;
    }
}
