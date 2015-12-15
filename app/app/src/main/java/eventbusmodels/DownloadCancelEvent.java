package eventbusmodels;

import android.support.annotation.Nullable;

import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * Created by Fechner on 12/15/15.
 */
public class DownloadCancelEvent {

    @Nullable
    public final Version version;

    @Nullable
    public final MediaType type;

    public final boolean haltAll;

    public DownloadCancelEvent(boolean haltAll, MediaType type, Version version) {
        this.haltAll = haltAll;
        this.type = type;
        this.version = version;
    }
}
