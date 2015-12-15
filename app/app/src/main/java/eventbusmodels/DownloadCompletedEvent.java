package eventbusmodels;

import android.support.annotation.Nullable;

import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * Created by Fechner on 12/15/15.
 */
public class DownloadCompletedEvent {

    public final DownloadResult result;

    @Nullable
    public final Version version;

    @Nullable
    public final MediaType type;

    public DownloadCompletedEvent(DownloadResult result, @Nullable MediaType typeOrNull, @Nullable Version versionOrNull) {
        this.result = result;
        this.type = typeOrNull;
        this.version = versionOrNull;
    }
}
