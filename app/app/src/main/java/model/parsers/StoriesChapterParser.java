/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Book;
import model.daoModels.StoriesChapter;

/**
 * Created by PJ Fechner on 6/22/15.
 * Class for parsing StoriesChapter JSON
 */
public class StoriesChapterParser extends UWDataParser{

    private static final String NUMBER_JSON_KEY = "number";
    private static final String REFERENCE_JSON_KEY = "ref";
    private static final String TITLE_JSON_KEY = "title";


    public static StoriesChapter parseStoriesChapter(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        StoriesChapter newModel = new StoriesChapter();

        newModel.setNumber(jsonObject.getString(NUMBER_JSON_KEY).trim());
        newModel.setRef(jsonObject.getString(REFERENCE_JSON_KEY));
        newModel.setTitle(jsonObject.getString(TITLE_JSON_KEY));
        newModel.setSlug(newModel.getNumber());
        newModel.setUniqueSlug(parent.getUniqueSlug() + newModel.getSlug());
        newModel.setBookId(((Book) parent).getId());

        return newModel;
    }
}
