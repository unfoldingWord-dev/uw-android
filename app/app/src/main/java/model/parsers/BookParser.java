package model.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.DownloadState;
import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.Version;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class to parse JSON for Books
 */
public class BookParser extends UWDataParser{

    private static final String DESCRIPTION_JSON_KEY = "desc";
    private static final String MODIFIED_JSON_KEY = "mod";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String SOURCE_URL_JSON_KEY = "src";
    private static final String SIGNATURE_URL_JSON_KEY = "src_sig";
    private static final String TITLE_JSON_KEY = "title";
    private static final String MEDIA_JSON_KEY = "media";
    private static final String AUDIO_JSON_KEY = "audio";
    private static final String VIDEO_JSON_KEY = "video";

    public static Book parseBook(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Book newModel = new Book();

        newModel.setDescription(jsonObject.getString(DESCRIPTION_JSON_KEY));
        newModel.setModified(getDateFromSecondString(jsonObject.getString(MODIFIED_JSON_KEY)));
        newModel.setSlug(jsonObject.getString(SLUG_JSON_KEY));
        newModel.setUniqueSlug(parent.getUniqueSlug() + newModel.getSlug());

        newModel.setSourceUrl(jsonObject.getString(SOURCE_URL_JSON_KEY));
        newModel.setSignatureUrl(jsonObject.getString(SIGNATURE_URL_JSON_KEY));
        newModel.setTitle(jsonObject.getString(TITLE_JSON_KEY));
        newModel.setVersionId(((Version) parent).getId());
        newModel.setAudioSaveState(DownloadState.DOWNLOAD_STATE_NONE.ordinal());
        newModel.setVideoSaveState(DownloadState.DOWNLOAD_STATE_NONE.ordinal());

        return newModel;
    }

    public static JSONArray getBooksJsonForVersion(Version version) throws JSONException{

        JSONArray jsonArray = new JSONArray();

        for(Book book : version.getBooks()){
            jsonArray.put(getBookAsJson(book));
        }
        return jsonArray;
    }

    public static JSONObject getBookAsJson(Book model) throws JSONException{

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(DESCRIPTION_JSON_KEY, model.getDescription());
        jsonModel.put(MODIFIED_JSON_KEY, model.getModified().getTime());
        jsonModel.put(SLUG_JSON_KEY, model.getSlug());
        jsonModel.put(SOURCE_URL_JSON_KEY, model.getSourceUrl());
        jsonModel.put(SIGNATURE_URL_JSON_KEY, model.getSignatureUrl());
        jsonModel.put(TITLE_JSON_KEY, model.getTitle());
        jsonModel.put(MEDIA_JSON_KEY, getMediaJson(model));

        return jsonModel;
    }

    private static JSONObject getMediaJson(Book model) throws JSONException{

        JSONObject jsonModel = new JSONObject();
        jsonModel.put(AUDIO_JSON_KEY, AudioBookParser.getAudioBookAsJson(model.getAudioBook()));
        jsonModel.put(VIDEO_JSON_KEY, new JSONObject());
        return jsonModel;
    }
}
