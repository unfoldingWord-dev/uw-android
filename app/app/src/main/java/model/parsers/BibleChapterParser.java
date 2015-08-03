package model.parsers;

import org.json.JSONException;

import model.daoModels.BibleChapter;
import model.daoModels.Book;

/**
 * Created by Fechner on 6/22/15.
 */
public class BibleChapterParser extends UWDataParser{

    private static final String ID_JSON_KEY = "id";
    private static final String IMAGE_JSON_KEY = "img";
    private static final String TEXT_JSON_KEY = "text";

    public static BibleChapter parseBibleChapter(Book parent, String number, String text) throws JSONException{

        number = number.trim();
        BibleChapter chapter = new BibleChapter();
        chapter.setNumber(number);
        chapter.setSlug(number);
        chapter.setUniqueSlug(parent.getUniqueSlug() + number);
        chapter.setText(text);
        chapter.setBookId(parent.getId());


        return chapter;
    }
}
