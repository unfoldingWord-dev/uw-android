package parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import models.ChaptersModel;
import models.LanguageModel;
import utils.JsonUtils;

/**
 * Created by thasleem on 3/12/14.
 */
public class JsonParser {

    public static JsonParser jsonParser;

    /**
     * This is a single ton class
     * <p/>
     * return the instance of $JsonParser
     *
     * @return
     */
    public static JsonParser getInstance() {
        if (jsonParser == null) {
            jsonParser = new JsonParser();
        }
        return jsonParser;
    }

    private void JsonParser() {

    }

    /**
     * Getting Languages info from json
     *
     * @param json json string
     * @return ArrayList instance
     * @throws JSONException
     */
    public ArrayList<LanguageModel> getLanguagesInfo(String json) throws JSONException {
        JSONArray array = new JSONArray(json);
        ArrayList<LanguageModel> models = new ArrayList<LanguageModel>();
        for (int pos = 0; pos < array.length(); pos++) {
            LanguageModel model = new LanguageModel();
            JSONObject object = array.getJSONObject(pos);
            model.dateModified = object.has(JsonUtils.DATE_MODIFIED) ? object.getString(JsonUtils.DATE_MODIFIED) : "";
            model.direction = object.has(JsonUtils.DIRECTION) ? object.getString(JsonUtils.DIRECTION) : "";
            model.language = object.has(JsonUtils.LANGUAGE) ? object.getString(JsonUtils.LANGUAGE) : "";
            model.languageName = object.has(JsonUtils.LANGUAGE_NAME) ? object.getString(JsonUtils.LANGUAGE_NAME) : "";

            // inside status
            JSONObject statusJsonObject = object.getJSONObject(JsonUtils.STATUS);
            model.checkingEntity = object.has(JsonUtils.CHECKING_ENTITY) ? object.getString(JsonUtils.CHECKING_ENTITY) : "";
            model.checkingLevel = object.has(JsonUtils.CHECKING_LEVEL) ? object.getString(JsonUtils.CHECKING_LEVEL) : "";
            model.checkingLevel = object.has(JsonUtils.CHECKING_LEVEL) ? object.getString(JsonUtils.CHECKING_LEVEL) : "";
            model.comments = object.has(JsonUtils.COMMENTS) ? object.getString(JsonUtils.COMMENTS) : "";
            model.contributors = object.has(JsonUtils.CONTRIBUTORS) ? object.getString(JsonUtils.CONTRIBUTORS) : "";
            model.publishDate = object.has(JsonUtils.PUBLISH_DATE) ? object.getString(JsonUtils.PUBLISH_DATE) : "";
            model.sourceText = object.has(JsonUtils.SOURCE_TEXT) ? object.getString(JsonUtils.SOURCE_TEXT) : "";
            model.sourceTextVersion = object.has(JsonUtils.SOURCE_TEXT_VERSION) ? object.getString(JsonUtils.SOURCE_TEXT_VERSION) : "";
            model.version = object.has(JsonUtils.VERSION) ? object.getString(JsonUtils.VERSION) : "";
            models.add(model);
        }
        return models;
    }

    /**
     * Getting chapters info based on json data
     *
     * @param language type of language
     * @param jsonData json string from web
     * @return ArrayList<ChaptersModel> instances
     * @throws JSONException
     */
    public ArrayList<ChaptersModel> getChapterFromLanguage(String language, String jsonData) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        JSONArray jsonArray = object.getJSONArray(JsonUtils.CHAPTERS);
        ArrayList<ChaptersModel> models = new ArrayList<ChaptersModel>();
        for (int pos = 0; pos < jsonArray.length(); pos++) {
            ChaptersModel model = new ChaptersModel();
            JSONObject jsonObject = jsonArray.getJSONObject(pos);
            model.number = jsonObject.has(JsonUtils.NUMBER) ? jsonObject.getString(JsonUtils.NUMBER) : "";
            model.references = jsonObject.has(JsonUtils.REF) ? jsonObject.getString(JsonUtils.REF) : "";
            model.title = jsonObject.has(JsonUtils.TITLE) ? jsonObject.getString(JsonUtils.TITLE) : "";
            model.jsonArray = jsonObject.has(JsonUtils.FRAMES) ? jsonObject.getString(JsonUtils.FRAMES) : "";
            model.loadedLanguage = language;
            models.add(model);
        }
        return models;
    }
}
