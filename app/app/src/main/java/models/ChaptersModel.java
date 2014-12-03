package models;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class ChaptersModel {

    public String id = "";
    public String imgUrl = "";
    public String text = "";
    public String loadedLanguage = "";
    public String number = "";
    public String references = "";
    public String title = "";
    public String jsonArray = "";


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
