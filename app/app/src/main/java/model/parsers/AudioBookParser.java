package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.AudioBook;
import model.daoModels.Book;

/**
 * Created by Fechner on 9/18/15.
 */
public class AudioBookParser extends UWDataParser{

    private static final String CONTRIBUTORS_JSON_KEY = "contributors";
    private static final String REVISION_JSON_KEY = "rev";
    private static final String TEXT_VERSION_JSON_KEY = "txt_ver";
    private static final String SOURCE_LIST_JSON_KEY = "src_list";

    public static AudioBook parseAudioBook(JSONObject jsonObject, Book parent) throws JSONException {

        AudioBook newModel = new AudioBook();

        newModel.setContributors(jsonObject.getString(CONTRIBUTORS_JSON_KEY));
        newModel.setRevision(jsonObject.getString(REVISION_JSON_KEY));
        newModel.setTextVersion(jsonObject.getString(TEXT_VERSION_JSON_KEY));
        newModel.setUniqueSlug(parent.getUniqueSlug() + AudioBook.class.toString());
        newModel.setBookId(parent.getId());

        return newModel;
    }


    public static JSONObject getAudioBookAsJson(AudioBook model) throws JSONException{

        JSONObject jsonModel = new JSONObject();

        if(model != null){
            jsonModel.put(CONTRIBUTORS_JSON_KEY, model.getContributors());
            jsonModel.put(REVISION_JSON_KEY, model.getRevision());
            jsonModel.put(TEXT_VERSION_JSON_KEY, model.getTextVersion());
            jsonModel.put(SOURCE_LIST_JSON_KEY, AudioChapterParser.getBooksJsonForVersion(model));
        }

        return jsonModel;
    }
}
