package parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import models.ChaptersModel;
import models.LanguageModel;
import utils.JsonUtils;
import utils.URLDownloadUtil;
import utils.URLUtils;

/**
 * Created by Acts Media Inc on 3/12/14.
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

    public static ArrayList<ChaptersModel> parseStory(String jsonArray) throws JSONException {
        ArrayList<ChaptersModel> models = new ArrayList<ChaptersModel>();
        JSONArray array = new JSONArray(jsonArray);
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            ChaptersModel model = new ChaptersModel();
            model.id = jsonObject.has(JsonUtils.ID) ? jsonObject.getString(JsonUtils.ID) : "";
            model.imgUrl = jsonObject.has(JsonUtils.IMAGE_URL) ? jsonObject.getString(JsonUtils.IMAGE_URL) : "";
            model.text = jsonObject.has("text") ? jsonObject.getString("text") : "";
            models.add(model);
        }
        return models;
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
            model.checkingEntity = statusJsonObject.has(JsonUtils.CHECKING_ENTITY) ? statusJsonObject.getString(JsonUtils.CHECKING_ENTITY) : "";
            model.checkingLevel = statusJsonObject.has(JsonUtils.CHECKING_LEVEL) ? statusJsonObject.getString(JsonUtils.CHECKING_LEVEL) : "";
            model.checkingLevel = statusJsonObject.has(JsonUtils.CHECKING_LEVEL) ? statusJsonObject.getString(JsonUtils.CHECKING_LEVEL) : "";
            model.comments = statusJsonObject.has(JsonUtils.COMMENTS) ? statusJsonObject.getString(JsonUtils.COMMENTS) : "";
            model.contributors = statusJsonObject.has(JsonUtils.CONTRIBUTORS) ? statusJsonObject.getString(JsonUtils.CONTRIBUTORS) : "";
            model.publishDate = statusJsonObject.has(JsonUtils.PUBLISH_DATE) ? statusJsonObject.getString(JsonUtils.PUBLISH_DATE) : "";
            model.sourceText = statusJsonObject.has(JsonUtils.SOURCE_TEXT) ? statusJsonObject.getString(JsonUtils.SOURCE_TEXT) : "";
            model.sourceTextVersion = statusJsonObject.has(JsonUtils.SOURCE_TEXT_VERSION) ? statusJsonObject.getString(JsonUtils.SOURCE_TEXT_VERSION) : "";
            model.version = statusJsonObject.has(JsonUtils.VERSION) ? statusJsonObject.getString(JsonUtils.VERSION) : "";
            models.add(model);
        }
        return models;
    }

    /**
     * Getting chapters info based on json data
     *
     * @param
     * @param language type of language
     * @param jsonData json string from web
     * @return ArrayList<ChaptersModel> instances
     * @throws JSONException
     */
    public ArrayList<ChaptersModel> getChapterFromLanguage(String language, String jsonData) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        JSONObject detailsJson = object.getJSONObject(JsonUtils.APP_WORDS);

        JSONArray jsonArray = object.getJSONArray(JsonUtils.CHAPTERS);
        ArrayList<ChaptersModel> models = new ArrayList<ChaptersModel>();
        for (int pos = 0; pos < jsonArray.length(); pos++) {
            ChaptersModel model = new ChaptersModel();

            JSONObject jsonObject = jsonArray.getJSONObject(pos);
            model.cancel = detailsJson.has(JsonUtils.CANCEL) ? detailsJson.getString(JsonUtils.CANCEL) : "";
            model.chapters = detailsJson.has(JsonUtils.CHAPTERS) ? detailsJson.getString(JsonUtils.CHAPTERS) : "";
            model.languages = detailsJson.has(JsonUtils.LANGUAGES) ? detailsJson.getString(JsonUtils.LANGUAGES) : "";
            model.next_chapter = detailsJson.has(JsonUtils.NEXT_CHAPTER) ? detailsJson.getString(JsonUtils.NEXT_CHAPTER) : "";
            model.remove_locally = detailsJson.has(JsonUtils.REMOVE_LOCALLY) ? detailsJson.getString(JsonUtils.REMOVE_LOCALLY) : "";
            model.remove_this_string = detailsJson.has(JsonUtils.REMOVE_THIS_STRING) ? detailsJson.getString(JsonUtils.REMOVE_THIS_STRING) : "";
            model.save_locally = detailsJson.has(JsonUtils.SAVE_LOCALLY) ? detailsJson.getString(JsonUtils.SAVE_LOCALLY) : "";
            model.save_this_string = detailsJson.has(JsonUtils.SAVE_THIS_STRING) ? detailsJson.getString(JsonUtils.SAVE_THIS_STRING) : "";
            model.select_a_language = detailsJson.has(JsonUtils.SELECT_A_LANGUAGE) ? detailsJson.getString(JsonUtils.SELECT_A_LANGUAGE) : "";
            model.number = jsonObject.has(JsonUtils.NUMBER) ? jsonObject.getString(JsonUtils.NUMBER) : "";
            model.references = jsonObject.has(JsonUtils.REF) ? jsonObject.getString(JsonUtils.REF) : "";
            model.title = jsonObject.has(JsonUtils.TITLE) ? jsonObject.getString(JsonUtils.TITLE) : "";
            model.jsonArray = jsonObject.has(JsonUtils.FRAMES) ? jsonObject.getString(JsonUtils.FRAMES) : "";
            model.loadedLanguage = language;
            models.add(model);
        }
        return models;
    }

    public ArrayList<ArrayList<ChaptersModel>> getIfChangedData(String date, String languages) throws IOException, JSONException {
        String json = URLDownloadUtil.downloadJson(URLUtils.LANGUAGE_INFO);
        ArrayList<LanguageModel> info = getLanguagesInfo(json);
        ArrayList<ArrayList<ChaptersModel>> list = new ArrayList<ArrayList<ChaptersModel>>();

        for (int i = 0; i < info.size(); i++) {
            if (!info.get(i).dateModified.equals(date) && languages.equals(info.get(i).language)) {
                String chapters = URLDownloadUtil.downloadJson(URLUtils.CHAPTER_INFO + languages + "/obs-" + languages + ".json");
                ArrayList<ChaptersModel> chapterFromLanguage = getChapterFromLanguage(languages, chapters);
                list.add(chapterFromLanguage);
            }
        }

        return list;
    }
}
