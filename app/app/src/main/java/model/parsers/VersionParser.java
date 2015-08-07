package model.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.DownloadState;
import model.UWDatabaseModel;
import model.daoModels.Language;
import model.daoModels.Version;

/**
 * Created by Fechner on 6/22/15.
 */
public class VersionParser extends UWDataParser{

    public static final String BOOKS_JSON_KEY = "toc";
    private static final String MODIFIED_JSON_KEY = "mod";
    public static final String NAME_JSON_KEY = "name";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String STATUS_JSON_KEY = "status";

    private static final String STATUS_CHECKING_ENTITY_JSON_KEY = "checking_entity";
    private static final String STATUS_CHECKING_LEVEL_JSON_KEY = "checking_level";
    private static final String STATUS_COMMENTS_JSON_KEY = "comments";
    private static final String STATUS_CONTRIBUTORS_JSON_KEY = "contributors";
    private static final String STATUS_PUBLISH_DATE_JSON_KEY = "publish_date";
    private static final String STATUS_SOURCE_TEXT_JSON_KEY = "source_text";
    private static final String STATUS_VERSION_SOURCE_TEXT_JSON_KEY = "source_text_version";
    private static final String STATUS_VERSION_JSON_KEY = "version";

    public static Version parseVersion(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Version newModel = new Version();

        newModel.setSaveState(DownloadState.DOWNLOAD_STATE_NONE.ordinal());
        newModel.setModified(getDateFromSecondString(jsonObject.getString(MODIFIED_JSON_KEY)));
        newModel.setName(jsonObject.getString(NAME_JSON_KEY));
        newModel.setSlug(jsonObject.getString(SLUG_JSON_KEY));
        newModel.setUniqueSlug(parent.getUniqueSlug() + newModel.getSlug());

        JSONObject statusObject = jsonObject.getJSONObject(STATUS_JSON_KEY);

        newModel.setStatusCheckingEntity(statusObject.getString(STATUS_CHECKING_ENTITY_JSON_KEY));
        newModel.setStatusCheckingLevel(statusObject.getString(STATUS_CHECKING_LEVEL_JSON_KEY));
        newModel.setStatusComments(statusObject.getString(STATUS_COMMENTS_JSON_KEY));
        newModel.setStatusContributors(statusObject.getString(STATUS_CONTRIBUTORS_JSON_KEY));

        newModel.setStatusPublishDate(statusObject.getString(STATUS_PUBLISH_DATE_JSON_KEY));
        newModel.setStatusSourceText(statusObject.getString(STATUS_SOURCE_TEXT_JSON_KEY));
        newModel.setStatusSourceTextVersion(statusObject.getString(STATUS_VERSION_SOURCE_TEXT_JSON_KEY));
        newModel.setStatusVersion(statusObject.getString(STATUS_VERSION_JSON_KEY));
        newModel.setLanguageId(((Language) parent).getId());

        return newModel;
    }

    public static JSONArray getVersionsForLanguage(Language language) throws JSONException{

        JSONArray jsonArray = new JSONArray();

        for(Version version : language.getVersions()){
            jsonArray.put(getVersionAsJson(version));
        }
        return jsonArray;
    }

    public static JSONObject getVersionAsJson(Version model) throws JSONException{

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(MODIFIED_JSON_KEY, model.getModified().getTime());
        jsonModel.put(NAME_JSON_KEY, model.getName());
        jsonModel.put(SLUG_JSON_KEY, model.getSlug());
        jsonModel.put(STATUS_JSON_KEY, getVersionStatusAsJson(model));

        jsonModel.put(BOOKS_JSON_KEY, BookParser.getBooksJsonForVersion(model));

        return jsonModel;
    }

    private static JSONObject getVersionStatusAsJson(Version model) throws JSONException{

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(STATUS_CHECKING_ENTITY_JSON_KEY, model.getStatusCheckingEntity());
        jsonModel.put(STATUS_CHECKING_LEVEL_JSON_KEY, model.getStatusCheckingLevel());
        jsonModel.put(STATUS_COMMENTS_JSON_KEY, model.getStatusComments());
        jsonModel.put(STATUS_CONTRIBUTORS_JSON_KEY, model.getStatusContributors());

        jsonModel.put(STATUS_SOURCE_TEXT_JSON_KEY, model.getStatusSourceText());
        jsonModel.put(STATUS_PUBLISH_DATE_JSON_KEY, model.getStatusPublishDate());
        jsonModel.put(STATUS_VERSION_SOURCE_TEXT_JSON_KEY, model.getStatusSourceTextVersion());
        jsonModel.put(STATUS_VERSION_JSON_KEY, model.getStatusVersion());
        return jsonModel;
    }
}
