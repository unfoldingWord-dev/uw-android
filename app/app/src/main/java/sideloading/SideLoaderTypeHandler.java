package sideloading;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fechner on 7/22/15.
 */
public class SideLoaderTypeHandler {

    public static List<String> getListOfSideLoadTypes(Context context, boolean loading){

        List<String> sideLoadList = new ArrayList<String>();

        sideLoadList.add(context.getResources().getString(R.string.qr_code_title));

        sideLoadList.add(context.getResources().getString((loading)? R.string.auto_find_title : R.string.external_storage_title));
        sideLoadList.add(context.getResources().getString((loading)? R.string.choose_file_title : R.string.choose_directory_title));

        if(hasBluetooth(context) && !loading){
            sideLoadList.add(context.getResources().getString(R.string.bluetooth_title));
        }
        if(hasWiFi(context)){
            sideLoadList.add(context.getResources().getString(R.string.wi_fi_direct_title));
        }
        if(!loading){
            sideLoadList.add(context.getResources().getString(R.string.other_title));
        }

        return sideLoadList;
    }

    public static SideLoadType getTypeForIndex(Context context, int index, boolean loading){

        switch (index){
            case 0:{
                return SideLoadType.SIDE_LOAD_TYPE_QR_CODE;
            }
            case 1:{
                return (loading)? SideLoadType.SIDE_LOAD_TYPE_AUTO_FIND : SideLoadType.SIDE_LOAD_TYPE_SD_CARD;
            }
            case 2:{
                return SideLoadType.SIDE_LOAD_TYPE_STORAGE;
            }
            case 3:{
                if(hasBluetooth(context) && !loading){
                    return SideLoadType.SIDE_LOAD_TYPE_BLUETOOTH;
                }
                else if(hasWiFi(context)){
                    return SideLoadType.SIDE_LOAD_TYPE_WIFI;
                }
                else{
                    return SideLoadType.SIDE_LOAD_TYPE_OTHER;
                }
            }
            case 4:{
                if(hasWiFi(context)){
                    return SideLoadType.SIDE_LOAD_TYPE_WIFI;
                }
                else{
                    return SideLoadType.SIDE_LOAD_TYPE_OTHER;
                }
            }
            default:{
                return SideLoadType.SIDE_LOAD_TYPE_OTHER;
            }
        }
    }

    private static boolean hasBluetooth(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    private static boolean hasWiFi(Context context){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
        }
        else{
            return false;
        }
    }
}
