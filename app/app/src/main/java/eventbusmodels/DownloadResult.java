package eventbusmodels;

/**
 * Created by Fechner on 12/15/15.
 */
public enum DownloadResult {

    DOWNLOAD_RESULT_NONE(0), DOWNLOAD_RESULT_SUCCESS(1), DOWNLOAD_RESULT_FAILED(2), DOWNLOAD_RESULT_CANCELED(3);


    private int num;

    DownloadResult(int num) {
        this.num = num;
    }
}
