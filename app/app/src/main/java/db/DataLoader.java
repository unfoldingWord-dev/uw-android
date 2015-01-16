package db;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import models.LanguageModel;

/**
 * Created by Fechner on 1/11/15.
 */
public class DataLoader {

    private static Map<String,  LanguageModel> languageMap = null;
    public static Map<String,  LanguageModel> getLanguageMap(Context context){

        if(languageMap == null){
            languageMap = DBManager.getInstance(context).getAllLanguagesAsMap();
        }

        return languageMap;
    }

    public static void invalidateCache(){
        languageMap = null;
    }
}
