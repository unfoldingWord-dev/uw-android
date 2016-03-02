/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package utils;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Acts Media Inc on 3/12/14.
 */
public class URLDownloadUtil {

    private static String TAG = "URLDownloadUtil";
//    static int connectionTimeout = 200000;
//    static int socketTimeout = 10000;
//
//    static public HttpResponse downloadUrl(String url) throws IOException {
//
//        Log.i(TAG, "Will download url: " + url);
//
//        HttpParams httpParameters = new BasicHttpParams();
//
//        HttpConnectionParams.setConnectionTimeout(httpParameters,
//                connectionTimeout);
//        HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
//
//        HttpClient httpClient = new DefaultHttpClient(httpParameters);
//        HttpGet get = new HttpGet(url);
//        HttpResponse response = httpClient.execute(get);
//        return response;
//    }
    /**
     * Download JSON data from url
     *
     * @param url
     * @return
     */
    @Nullable
    public static String downloadString(String url){

        byte[] downloadedBytes = downloadBytes(url);
        if (downloadedBytes != null){
            return new String(downloadedBytes);
        }
        else{
            return null;
        }
    }

    @Nullable
    public static byte[] downloadBytes(String url) {

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return response.body().bytes();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
