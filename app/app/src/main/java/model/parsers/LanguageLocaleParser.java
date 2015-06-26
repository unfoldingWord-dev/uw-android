package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.daoModels.LanguageLocale;

/**
 * Created by Fechner on 6/22/15.
 */
public class LanguageLocaleParser extends UWDataParser{

    private static final String GW_JSON_TAG = "gw";
    private static final String LANGUAGE_DIRECTION_JSON_KEY = "ld";
    private static final String LANGUAGE_KEY_JSON_KEY = "lc";
    private static final String LANGUAGE_NAME_JSON_KEY = "lc";
    private static final String CC_JSON_KEY = "cc";
    private static final String LANGUAGE_REGION_JSON_KEY = "lr";
    private static final String PK_JSON_KEY = "pk";

    public static LanguageLocale parseLanguageLocale(JSONObject jsonObject) throws JSONException{

        LanguageLocale newModel = new LanguageLocale();

        newModel.setGw(jsonObject.getBoolean(GW_JSON_TAG));
        newModel.setLanguageDirection(jsonObject.getString(LANGUAGE_DIRECTION_JSON_KEY));
        newModel.setLanguageKey(jsonObject.getString(LANGUAGE_KEY_JSON_KEY));
        newModel.setLanguageName(jsonObject.getString(LANGUAGE_NAME_JSON_KEY));

        newModel.setCc(getCCString(jsonObject));
        newModel.setLanguageRegion(jsonObject.getString(LANGUAGE_REGION_JSON_KEY));
        newModel.setPk(jsonObject.getInt(PK_JSON_KEY));

        return newModel;
    }

    private static String getCCString(JSONObject jsonObject) throws JSONException{

        String finalText = "";
        String[] ccStrings = (String[]) jsonObject.get(CC_JSON_KEY);

        for(String ccString : ccStrings){
            finalText += ccString + ",";
        }

        if(finalText.length() > 0){
            return finalText.substring(0, finalText.length() - 1); // -1 to delete last ","
        }
        else{
            return "";
        }
    }
}
