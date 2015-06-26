package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.Project;
import model.daoModels.StoriesChapter;

/**
 * Created by Fechner on 6/22/15.
 */
public class StoriesChapterParser extends UWDataParser{

    private static final String NUMBER_JSON_KEY = "number";
    private static final String REFERENCE_JSON_KEY = "reference";
    private static final String TITLE_JSON_KEY = "title";


    public static StoriesChapter parseStoriesChapter(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        StoriesChapter newModel = new StoriesChapter();

        newModel.setNumber(jsonObject.getString(NUMBER_JSON_KEY));
        newModel.setRef(jsonObject.getString(REFERENCE_JSON_KEY));
        newModel.setTitle(jsonObject.getString(TITLE_JSON_KEY));
        newModel.setSlug(parent.getSlug() + newModel.getNumber());
        newModel.setBookId(((Book) parent).getId());

        return newModel;
    }
}
