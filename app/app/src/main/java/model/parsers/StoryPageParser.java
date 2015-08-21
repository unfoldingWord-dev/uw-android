package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.StoriesChapter;
import model.daoModels.StoryPage;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class for parsing StoryPage JSON
 */
public class StoryPageParser extends UWDataParser{

    private static final String ID_JSON_KEY = "id";
    private static final String IMAGE_JSON_KEY = "img";
    private static final String TEXT_JSON_KEY = "text";


    public static StoryPage parseStoryPage(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        StoryPage newModel = new StoryPage();

        String idString = jsonObject.getString(ID_JSON_KEY);

        String[] splitString = idString.split("-");
        newModel.setNumber(splitString[1].trim());

        newModel.setImageUrl(jsonObject.getString(IMAGE_JSON_KEY));
        newModel.setText(jsonObject.getString(TEXT_JSON_KEY));
        newModel.setSlug(newModel.getNumber());
        newModel.setUniqueSlug(parent.getUniqueSlug() + newModel.getSlug());
        newModel.setStoryChapterId(((StoriesChapter) parent).getId());

        return newModel;
    }
}
