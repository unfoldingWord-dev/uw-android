package peejweej.sideloading.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Fechner on 8/24/15.
 */
public class SideLoadInformation implements Serializable{

    public String fileExtension;
    public String file;
    public String fileName;
    public Uri fileUri;
    public SideLoadVerifier fileVerifier;

    public SideLoadInformation(String file, String fileExtension, Uri fileUri, SideLoadVerifier fileVerifier) {
        this.file = file;
        this.fileExtension = fileExtension;
        this.fileUri = fileUri;
        this.fileVerifier = fileVerifier;
    }
}
