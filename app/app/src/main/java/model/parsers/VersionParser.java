package model.parsers;

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

    private static final String MODIFIED_JSON_KEY = "mod";
    private static final String NAME_JSON_KEY = "name";
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
        newModel.setSlug(parent.getSlug() + jsonObject.getString(SLUG_JSON_KEY));

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
}
