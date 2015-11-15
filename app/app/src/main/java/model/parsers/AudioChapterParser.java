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
    private static final String SOURCE_JSON_KEY = "src";
    private static final String SOURCE_SIGNATURE_JSON_KEY = "src_sig";
    private static final String CHAPTER_JSON_KEY = "chap";

    public static AudioChapter parseAudioChapter(JSONObject jsonObject, AudioBook parent) throws JSONException {

        AudioChapter newModel = new AudioChapter();

        newModel.setChapter(Integer.parseInt(jsonObject.getString(CHAPTER_JSON_KEY)));
        newModel.setBitrateJson(jsonObject.getJSONArray(BITRATE_JSON_KEY).toString());
        newModel.setLength(jsonObject.getInt(LENGTH_JSON_KEY));
        newModel.setSource(jsonObject.getString(SOURCE_JSON_KEY));
        newModel.setSourceSignature(jsonObject.getString(SOURCE_SIGNATURE_JSON_KEY));
        newModel.setUniqueSlug(parent.getUniqueSlug() + Integer.toString(newModel.getChapter()));
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

        jsonModel.put(BITRATE_JSON_KEY, model.getBitrateJson());
        jsonModel.put(LENGTH_JSON_KEY, model.getLength());
        jsonModel.put(SOURCE_JSON_KEY, model.getSource());
        jsonModel.put(SOURCE_SIGNATURE_JSON_KEY, model.getSourceSignature());

        JSONObject parentModel = new JSONObject();
        String chapter = (model.getChapter() < 10)? "0" + Integer.toString(model.getChapter()) : Integer.toString(model.getChapter());
        parentModel.put(chapter, jsonModel);

        return parentModel;
    }
}
