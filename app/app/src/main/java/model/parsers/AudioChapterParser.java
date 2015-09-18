package model.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import model.daoModels.AudioBook;
import model.daoModels.AudioChapter;
import model.daoModels.Book;

/**
 * Created by Fechner on 9/18/15.
 */
public class AudioChapterParser extends UWDataParser{

    private static final String BITRATE_JSON_KEY = "br";
    private static final String LENGTH_JSON_KEY = "length";
    private static final String MODIFIED_JSON_KEY = "mod";
    private static final String SIZE_JSON_KEY = "size";
    private static final String SOURCE_JSON_KEY = "src";
    private static final String SOURCE_SIGNATURE_JSON_KEY = "src_sig";

    public static AudioChapter parseAudioChapter(JSONObject jsonObject, AudioBook parent) throws JSONException {

        Iterator<?> keys = jsonObject.keys();

        String key = (String)keys.next();
        return parseAudioChapter(jsonObject.getJSONObject(key), parent, Integer.parseInt(key));
    }

    public static AudioChapter parseAudioChapter(JSONObject jsonObject, AudioBook parent, int chapter) throws JSONException {

        AudioChapter newModel = new AudioChapter();

        newModel.setChapter(chapter);
        newModel.setBitRate(jsonObject.getInt(BITRATE_JSON_KEY));
        newModel.setLength(jsonObject.getInt(LENGTH_JSON_KEY));
        newModel.setModified(getDate(jsonObject.getLong(MODIFIED_JSON_KEY)));
        newModel.setSize(jsonObject.getInt(SIZE_JSON_KEY));
        newModel.setSource(jsonObject.getString(SOURCE_JSON_KEY));
        newModel.setSourceSignature(jsonObject.getString(SOURCE_SIGNATURE_JSON_KEY));
        newModel.setUniqueSlug(parent.getUniqueSlug() + Integer.toString(chapter));
        newModel.setAudioBookId(parent.getId());

        return newModel;
    }



    public static JSONArray getBooksJsonForVersion(AudioBook audioBook) throws JSONException{

        JSONArray jsonArray = new JSONArray();

        for(AudioChapter chapter : audioBook.getAudioChapters()){
            jsonArray.put(getAudioChapterAsJson(chapter));
        }
        return jsonArray;
    }


    private static JSONObject getAudioChapterAsJson(AudioChapter model) throws JSONException{

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(BITRATE_JSON_KEY, model.getBitRate());
        jsonModel.put(LENGTH_JSON_KEY, model.getLength());
        jsonModel.put(MODIFIED_JSON_KEY, model.getModified().getTime());
        jsonModel.put(SIZE_JSON_KEY, model.getSize());
        jsonModel.put(SOURCE_JSON_KEY, model.getSource());
        jsonModel.put(SOURCE_SIGNATURE_JSON_KEY, model.getSourceSignature());

        JSONObject parentModel = new JSONObject();
        String chapter = (model.getChapter() < 10)? "0" + Integer.toString(model.getChapter()) : Integer.toString(model.getChapter());
        parentModel.put(chapter, jsonModel);

        return parentModel;
    }
}
