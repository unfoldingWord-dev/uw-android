package utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Acts Media Inc on 3/12/14.
 */
public class URLDownloadUtil {

    private static String TAG = "URLDownloadUtil";
    static int connectionTimeout = 20000;
    static int socketTimeout = 10000;

    static public HttpResponse downloadUrl(String url) throws IOException {

        Log.i(TAG, "Will download url: " + url);

        HttpParams httpParameters = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParameters,
                connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpGet get = new HttpGet(url);
        HttpResponse response = httpClient.execute(get);
        return response;
    }
    /**
     * Download JSON data from url
     *
     * @param url
     * @return
     */
    public static String downloadString(String url){

        try {
            HttpResponse response = downloadUrl(url);

            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] downloadBytes(String url) {

        try {
            HttpResponse response = downloadUrl(url);

            return EntityUtils.toByteArray(response.getEntity());
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }
}
