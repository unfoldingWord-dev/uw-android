package eventbusmodels;

import android.support.annotation.Nullable;

import java.util.HashMap;
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

    /**
     * @param version
     * @param type
     * @return true if the model was added
     */
    private boolean addModel(Version version, MediaType type){

        DownloadTrackingModel model = getModels().put(getKey(version, type), new DownloadTrackingModel(version, type));
        return (model == null);
    }

    private boolean removeModel(Version version, MediaType type){

        return (getModels().remove(getKey(version, type)) != null);
    }

    public static boolean containsModel(Version version, MediaType type){

        DownloadingVersionsEvent event = EventBus.getDefault().getStickyEvent(DownloadingVersionsEvent.class);
        if(event == null){
            return false;
        }
        else{
            return event.getModels().containsKey(getKey(version, type));
        }

    }

    private static String getKey(Version version, MediaType type){
        return version.getSlug() + type.getName();
    }

    @Nullable
    public static DownloadingVersionsEvent getEventAdding(Version version, MediaType type){

        DownloadingVersionsEvent event = EventBus.getDefault().getStickyEvent(DownloadingVersionsEvent.class);
        if(event == null){
            event = new DownloadingVersionsEvent();
        }
        return (event.addModel(version, type))? event : null;
    }

    @Nullable
    public static DownloadingVersionsEvent getEventRemoving(Version version, MediaType type){

        DownloadingVersionsEvent event = EventBus.getDefault().getStickyEvent(DownloadingVersionsEvent.class);
        if(event == null){
            return new DownloadingVersionsEvent();
        }
        return (event.removeModel(version, type))? event : null;
    }

    @Override
    public String toString() {
        return "DownloadingVersionsEvent{" +
                "models=" + getModelsAsString(models) +
                '}';
    }

    private static String getModelsAsString(Map<String, DownloadTrackingModel> models){

        String text = "{";
        if(models != null) {
            for (Map.Entry<String, DownloadTrackingModel> entry : models.entrySet()) {
                text += "\n{" + entry.getKey() + " : " + entry.getValue().toString() + "}";
            }
        }
        return text + "}";
    }
}
