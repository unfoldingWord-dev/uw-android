/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.github.peejweej.androidsideloading.activities.SideLoadActivity;
import com.github.peejweej.androidsideloading.activities.SideShareActivity;
import com.github.peejweej.androidsideloading.model.SideLoadInformation;
import com.github.peejweej.androidsideloading.model.SideLoadVerifier;

import model.daoModels.Version;
import model.parsers.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fechner on 8/24/15.
 */
public class SharingHelper {

    private static final String FILE_EXTENSION = ".ufw";

    public static SideLoadInformation getLoadInformation(){
        return new SideLoadInformation(FILE_EXTENSION, new SideLoadVerifier() {
            @Override
            public boolean fileIsValid(String file) {
                return true;
            }

            @Override
            public boolean fileIsValid(Uri file) {
                return true;
            }
        });
    }

    private static SideLoadInformation getShareInformation(Uri fileUri, String fileName){
        return new SideLoadInformation(fileName, fileUri);
    }

    public static SideLoadInformation getShareInformation(Context context, Version version, List<MediaType> types){

        Uri fileUri = DataFileManager.createUriForSideLoad(context, version, types);

        return getShareInformation(fileUri, getFileNameForVersion(version));
    }

//    private static Uri getFileForVersion(Context context, Version version){
//
//        FileUtilities.compressText(version.getAsPreloadJson(context).toString());
//        return FileUtil.createTemporaryFile(context, FileUtilities.compressText(version.getAsPreloadJson(context).toString()), getFileNameForVersion(version));
//    }

    public static String getFileNameForVersion(Version version){
        return version.getName() + " (" + version.getLanguage().getLanguageAbbreviation() + ")" + FILE_EXTENSION;
    }

    public static Intent getIntentForLoading(Context context){
        return new Intent(context, SideLoadActivity.class).putExtra(SideLoadActivity.SIDE_LOAD_INFORMATION_PARAM, getLoadInformation());
    }

    public static Intent getIntentForSharing(Context context, Version version){

        return new Intent(context, SideShareActivity.class).putExtra(SideShareActivity.SIDE_LOAD_INFORMATION_PARAM, getShareInformation(context, version, new ArrayList<MediaType>()));
    }
}