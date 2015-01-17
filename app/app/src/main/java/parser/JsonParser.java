package parser;

import org.apache.http.util.LangUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import model.modelClasses.LanguageModel;
import model.modelClasses.PageModel;

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


//    public static ArrayList<ChapterModel> parseStory(String jsonArray) throws JSONException {
//        ArrayList<ChapterModel> models = new ArrayList<ChapterModel>();
//        JSONArray array = new JSONArray(jsonArray);
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject jsonObject = array.getJSONObject(i);
//            ChapterModel model = new ChapterModel();
//            model.id = jsonObject.has(JsonUtils.ID) ? jsonObject.getString(JsonUtils.ID) : "";
//            model.ima = jsonObject.has(JsonUtils.IMAGE_URL) ? jsonObject.getString(JsonUtils.IMAGE_URL) : "";
//            model.text = jsonObject.has("text") ? jsonObject.getString("text") : "";
//            models.add(model);
//        }
//        return models;
//    }




    /**
     * Getting Languages info from json
     *
     * @param json json string
     * @return ArrayList instance
     * @throws JSONException
     */
    public static Map<String, LanguageModel> getLanguagesInfo(String json) throws JSONException {

        Map<String, LanguageModel> languageMap = new HashMap<String, LanguageModel>();

        JSONArray array = new JSONArray(json);
        for (int pos = 0; pos < array.length(); pos++) {

            JSONObject jObject = array.getJSONObject(pos);

            LanguageModel model = new LanguageModel();
            model.initModelFromJsonObject(jObject);
            languageMap.put(model.language, model);
        }

        return languageMap;
    }



//    /**
//     * Getting chapters info based on json data
//     *
//     * @param
//     * @param language type of language
//     * @param jsonData json string from web
//     * @return ArrayList<ChapterModel> instances
//     * @throws JSONException
//     */
//    public ArrayList<BookModel> getBookForLanguage(LanguageModel language, String jsonData) throws JSONException {
//        JSONObject object = new JSONObject(jsonData);
//        JSONObject detailsJson = object.getJSONObject(JsonUtils.APP_WORDS);
//
//        ArrayList<ChapterModel> models = new ArrayList<ChapterModel>();
//
//        for (int pos = 0; pos < jsonArray.length(); pos++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(pos);
//            BookModel book = BookModel.getBookModelFromJsonObject(jsonObject, language);
//            models.add(model);
//        }
//        return models;
//    }

//    public ArrayList<LanguageModel> getIfChangedData(int date, String languages) throws IOException, JSONException {
//        String json = URLDownloadUtil.downloadJson(URLUtils.LANGUAGE_INFO);
//        ArrayList<LanguageModel> info = getLanguagesInfo(json);
//
//        for (int i = 0; i < info.size(); i++) {
//            LanguageModel languageMod = info.get(i);
//            if (languageMod.dateModified < date && languages.equals(languageMod.language)) {
//
//                String chapters = URLDownloadUtil.downloadJson(URLUtils.CHAPTER_INFO + languages + "/obs-" + languages + ".json");
//                JSONObject object = new JSONObject(chapters);
//                BookModel book = BookModel.getBookModelFromJsonObject(object);
//                languageMod..books.add(book);
//            }
//        }
//
//        return info;
//    }

    public static long getSecondsFromDateString(String date){
//        20141207
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        GregorianCalendar calDate = new GregorianCalendar(gmt);

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        calDate.set(year, month, day);

        long seconds = calDate.getTimeInMillis();
        return  Long.parseLong(date);
    }
}
