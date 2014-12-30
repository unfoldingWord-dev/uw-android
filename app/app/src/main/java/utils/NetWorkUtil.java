package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Check device's network connectivity
 *
 * @author Created by Acts Media Inc
 */
public class NetWorkUtil {

    private static String TAG = "NetWorkUtil";
    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        NetworkInfo info = NetWorkUtil.getNetworkInfo(context);

        boolean isConnected = (info != null && info.isConnected());
        Log.i(TAG, "is connected to web: " + isConnected);

        return isConnected;
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @param
     * @return
     */
    public static boolean isConnectedWifi(Context context) {

        Log.i(TAG, "Connected to wifi");
        NetworkInfo info = NetWorkUtil.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @param
     * @return
     */
    public static boolean isConnectedMobile(Context context) {
        Log.i(TAG, "Connected to mobile web");
        NetworkInfo info = NetWorkUtil.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }


}