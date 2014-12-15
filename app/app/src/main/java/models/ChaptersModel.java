package models;

import java.io.Serializable;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class ChaptersModel extends LanguageModel implements Serializable {

    public String id = "";
    public String auto_id = "";
    public String imgUrl = "";
    public String text = "";
    public String loadedLanguage = "";
    public String number = "";
    public String references = "";
    public String title = "";
    public String jsonArray = "";
    public String cancel = "";
    public String chapters = "";
    public String languages = "";
    public String next_chapter = "";
    public String ok = "";
    public String remove_locally = "";
    public String remove_this_string = "";
    public String save_locally = "";
    public String save_this_string = "";
    public String select_a_language = "";


    @Override
    public String toString() {
        return "ChaptersModel{" +
                "id='" + id + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", text='" + text + '\'' +
                ", loadedLanguage='" + loadedLanguage + '\'' +
                ", number='" + number + '\'' +
                ", references='" + references + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
