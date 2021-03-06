/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model.parsers;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

import model.DataFileManager;
import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.Language;
import model.daoModels.Version;
import utils.FileUtil;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class for parsing Version JSON
 */
public class VersionParser extends UWDataParser{

    private static final String TAG = "VersionParser";

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

    //region basic Parsing

    public static Version parseVersion(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Version newModel = new Version();

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

    //endregion

    //region Side Loading Handling

    /**
     * Creates SideLoaded JSON for the passed Version
     * @param context Context to use
     * @param version Version with which JSON will be created
     * @return JSONObject for SideLoading the passd Version
     */
    public static JSONObject getAsSideLoadJson(Context context, Version version){

        try {
            JSONObject languageJson = LanguageParser.getLanguageAsJson(version.getLanguage(), true);
            languageJson.put(LanguageParser.VERSION_JSON_KEY, new JSONArray().put(getVersionAsJson(version)));

            JSONObject projectJson = ProjectParser.getProjectAsJson(version.getLanguage().getProject(), true);
            projectJson.put(ProjectParser.LANGUAGES_JSON_KEY, new JSONArray().put(languageJson));

            JSONObject sideLoadJson = new JSONObject();
            sideLoadJson.put("top", projectJson);
            sideLoadJson.put("sources", getSourcesAsJson(context, version));
            return sideLoadJson;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param context Context to use
     * @param version Version of which you want the sources JSON
     * @return JSON of all the passed Version's sources (USFM or JSON)
     */
    private static JSONObject getSourcesAsJson(Context context, Version version){

        JSONObject sourcesObject = new JSONObject();

        int length = version.getBooks().size();
        int i = 0;
        for(Book book : version.getBooks()){
            try {
                Uri sourceUri = DataFileManager.getUri(context, book, MediaType.MEDIA_TYPE_TEXT, book.getSourceUrl());
                Uri signatureUri = DataFileManager.getUri(context, book, MediaType.MEDIA_TYPE_TEXT, book.getSignatureUrl());

                byte[] file = FileUtil.getBytesFromFile(new File(sourceUri.getPath()));
                if(file != null) {
                    try {
                        sourcesObject.put(book.getSourceUrl(), new String(file, "UTF-8"));
                        sourcesObject.put(book.getSignatureUrl(), FileUtil.getStringFromFile(new File(signatureUri.getPath())));
//                        Log.i(TAG, "added sharing file to JSON " + ++i + " of " + length);
                    }
                    catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return sourcesObject;
    }


    //endregion
}
