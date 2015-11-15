/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.daoModels.Project;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class for parsing Project JSON
 */
public class ProjectParser extends UWDataParser{

    public static final String LANGUAGES_JSON_KEY = "langs";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String TITLE_JSON_KEY = "title";


    public static Project parseProject(JSONObject jsonObject) throws JSONException{

        Project newModel = new Project();
        newModel.setSlug(jsonObject.getString(SLUG_JSON_KEY));
        newModel.setUniqueSlug(newModel.getSlug());
        newModel.setTitle(jsonObject.getString(TITLE_JSON_KEY));
        return newModel;
    }

    public static JSONObject getProjectAsJson(Project model, boolean onlyCurrentModel) throws JSONException {

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(SLUG_JSON_KEY, model.getSlug());
        jsonModel.put(TITLE_JSON_KEY, model.getTitle());

        if (!onlyCurrentModel){
            jsonModel.put(LANGUAGES_JSON_KEY, LanguageParser.getLanguageJsonForProject(model));
        }

        return jsonModel;
    }
}
