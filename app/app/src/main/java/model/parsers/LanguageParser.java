package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Language;
import model.daoModels.Project;

/**
 * Created by Fechner on 6/22/15.
 */
public class LanguageParser extends UWDataParser{

    private static final String LANGUAGE_CODE_JSON_KEY = "lc";
    private static final String MODIFIED_JSON_KEY = "mod";

    public static Language parseLanguage(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Language newModel = new Language();
        newModel.setLanguageAbbreviation(jsonObject.getString(LANGUAGE_CODE_JSON_KEY));
        newModel.setModified(getDateFromSecondString(jsonObject.getString(MODIFIED_JSON_KEY)));
        newModel.setSlug(newModel.getLanguageAbbreviation().trim());
        newModel.setUniqueSlug(parent.getUniqueSlug() + newModel.getSlug());
        newModel.setProjectId(((Project) parent).getId());

        return newModel;
    }
}
