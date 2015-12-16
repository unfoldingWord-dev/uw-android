package eventbusmodels;

import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * Created by Fechner on 12/15/15.
 */
public class DownloadTrackingModel {

    public final Version version;
    public final MediaType type;

    public DownloadTrackingModel(Version version, MediaType type) {
        this.type = type;
        this.version = version;
    }


    @Override
    public String toString() {
        return "DownloadTrackingModel{" +
                "type=" + type.toString() +
                ", version=" + version.getSlug() +
                '}';
    }
}
