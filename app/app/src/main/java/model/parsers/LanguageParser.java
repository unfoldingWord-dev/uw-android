/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Language;
import model.daoModels.Project;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class for parsing Language JSON
 */
public class LanguageParser extends UWDataParser{

    private static final String LANGUAGE_CODE_JSON_KEY = "lc";
    private static final String MODIFIED_JSON_KEY = "mod";
    public static final String VERSION_JSON_KEY = "vers";

    public static Language parseLanguage(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Language newModel = new Language();
        newModel.setLanguageAbbreviation(jsonObject.getString(LANGUAGE_CODE_JSON_KEY));
        newModel.setModified(getDateFromSecondString(jsonObject.getString(MODIFIED_JSON_KEY)));
        newModel.setSlug(newModel.getLanguageAbbreviation().trim());
        newModel.setUniqueSlug(parent.getUniqueSlug() + newModel.getSlug());
        newModel.setProjectId(((Project) parent).getId());

        return newModel;
    }

    public static JSONArray getLanguageJsonForProject(Project project) throws JSONException{

        JSONArray jsonArray = new JSONArray();

        for(Language language : project.getLanguages()){
            jsonArray.put(getLanguageAsJson(language, false));
        }
        return jsonArray;
    }

    public static JSONObject getLanguageAsJson(Language model, boolean onlyCurrentModel) throws JSONException{

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(LANGUAGE_CODE_JSON_KEY, model.getLanguageAbbreviation());
        jsonModel.put(MODIFIED_JSON_KEY, model.getModified().getTime());

        if(!onlyCurrentModel) {
            jsonModel.put(VERSION_JSON_KEY, VersionParser.getVersionsForLanguage(model));
        }

        return jsonModel;
    }
}
