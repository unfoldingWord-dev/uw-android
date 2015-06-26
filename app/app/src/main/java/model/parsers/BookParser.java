package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.Version;

/**
 * Created by Fechner on 6/22/15.
 */
public class BookParser extends UWDataParser{

    private static final String DESCRIPTION_JSON_KEY = "desc";
    private static final String MODIFIED_JSON_KEY = "mod";
    private static final String SLUG_JSON_KEY = "slug";
    private static final String SOURCE_URL_JSON_KEY = "src";
    private static final String SIGNATURE_URL_JSON_KEY = "src_sig";
    private static final String TITLE_JSON_KEY = "title";



    public static Book parseBook(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Book newModel = new Book();

        newModel.setDescription(jsonObject.getString(DESCRIPTION_JSON_KEY));
        newModel.setModified(getDateFromSecondString(jsonObject.getString(MODIFIED_JSON_KEY)));
        newModel.setSlug(parent.getSlug() + jsonObject.getString(SLUG_JSON_KEY));

        newModel.setSourceUrl(jsonObject.getString(SOURCE_URL_JSON_KEY));
        newModel.setSignatureUrl(jsonObject.getString(SIGNATURE_URL_JSON_KEY));
        newModel.setTitle(jsonObject.getString(TITLE_JSON_KEY));
        newModel.setVersionId(((Version) parent).getId());

        return newModel;
    }
}
