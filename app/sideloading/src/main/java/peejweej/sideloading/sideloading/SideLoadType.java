package peejweej.sideloading.sideloading;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import peejweej.sideloading.R;

/**
 * Created by Fechner on 7/6/15.
 */
public enum SideLoadType {

    SIDE_LOAD_TYPE_NONE(0),  SIDE_LOAD_TYPE_BLUETOOTH(1), SIDE_LOAD_TYPE_NFC(2), SIDE_LOAD_TYPE_WIFI(3),
    SIDE_LOAD_TYPE_STORAGE(4), SIDE_LOAD_TYPE_QR_CODE(5), SIDE_LOAD_TYPE_SD_CARD(6), SIDE_LOAD_TYPE_AUTO_FIND(7),
    SIDE_LOAD_TYPE_FILE(8), SIDE_LOAD_TYPE_OTHER(9);


    private int num;


    SideLoadType(int num) {
        this.num = num;
    }

    public static int getSideLoadName(SideLoadType type){

        switch (type){
            case SIDE_LOAD_TYPE_BLUETOOTH:
                return  R.string.bluetooth_load_title;
            case SIDE_LOAD_TYPE_NFC:
                return R.string.nfc_load_title;
            case SIDE_LOAD_TYPE_WIFI:
                return R.string.wi_fi_direct_load_title;
            case SIDE_LOAD_TYPE_STORAGE:
                return R.string.choose_directory_load_title;
            case SIDE_LOAD_TYPE_QR_CODE:
                return R.string.qr_code_load_title;
            case SIDE_LOAD_TYPE_SD_CARD:
                return R.string.sd_card_load_title;
            case SIDE_LOAD_TYPE_AUTO_FIND:
                return R.string.auto_find_load_title;
            case SIDE_LOAD_TYPE_FILE:
                return R.string.choose_file_load_title;
            default:
                return R.string.other_load_title;

        }
    }

    public static List<SideLoadType> getListOfSideLoadTypes(Context context, boolean loading){

        List<SideLoadType> sideLoadList = new ArrayList<>();

        sideLoadList.add((loading)? SIDE_LOAD_TYPE_AUTO_FIND : SIDE_LOAD_TYPE_SD_CARD);
        sideLoadList.add((loading)? SIDE_LOAD_TYPE_FILE : SIDE_LOAD_TYPE_STORAGE);

        if(hasBluetooth(context) && !loading){
            sideLoadList.add(SIDE_LOAD_TYPE_BLUETOOTH);
        }
        if(hasWiFi(context)){
            sideLoadList.add(SIDE_LOAD_TYPE_WIFI);
        }
        if(!loading){
            sideLoadList.add(SIDE_LOAD_TYPE_OTHER);
        }

        return sideLoadList;
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
