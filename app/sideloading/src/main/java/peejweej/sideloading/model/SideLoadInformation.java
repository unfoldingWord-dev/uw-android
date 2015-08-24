package peejweej.sideloading.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Fechner on 8/24/15.
 */
public class SideLoadInformation implements Serializable{

    public String fileExtension;
    public String file;
    public String fileName;
    private String fileUriString;
    public SideLoadVerifier fileVerifier;

    public SideLoadInformation(String fileExtension, SideLoadVerifier fileVerifier) {
        this.fileExtension = fileExtension;
        this.fileVerifier = fileVerifier;
    }

    public SideLoadInformation(String fileName, Uri fileUri) {
        this.fileName = fileName;
        this.fileUriString = fileUri.getPath();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

    }

    public Uri getUri(){
        return Uri.parse(fileUriString);
    }
}
