package utils;

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
    static int connectiontimeout = 20000;
    static int sockettimeout = 10000;

    /**
     * Download JSON data from url
     *
     * @param url
     * @return
     */
    public static String downloadJson(String url) throws IOException {
        HttpParams httpparameters = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpparameters,
                connectiontimeout);
        HttpConnectionParams.setSoTimeout(httpparameters, sockettimeout);

        HttpClient httpClient = new DefaultHttpClient(httpparameters);
        HttpGet get = new HttpGet(url);
        HttpResponse response = httpClient.execute(get);
        return EntityUtils.toString(response.getEntity());
    }
}
