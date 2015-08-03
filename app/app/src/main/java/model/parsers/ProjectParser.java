package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.daoModels.Project;

/**
 * Created by Fechner on 6/22/15.
 */
public class ProjectParser extends UWDataParser{

    private static final String SLUG_JSON_KEY = "slug";
    private static final String TITLE_JSON_KEY = "title";

    public static Project parseProject(JSONObject jsonObject) throws JSONException{

        Project newModel = new Project();
        newModel.setSlug(jsonObject.getString(SLUG_JSON_KEY));
        newModel.setUniqueSlug(newModel.getSlug());
        newModel.setTitle(jsonObject.getString(TITLE_JSON_KEY));
        return newModel;
    }
}
