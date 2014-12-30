package models;

/**
 * Created by Acts Media Inc. on 3/12/14.
 */
public class LanguageModel {

    public String id = "";
    public String auto_id = "";
    public int dateModified;
    public String direction = "";
    public String language = "";
    public String checkingEntity = "";
    public String checkingLevel = "";
    public String comments = "";
    public String contributors = "";
    public String publishDate = "";
    public String sourceText = "";
    public String sourceTextVersion = "";
    public String version = "";
    public String languageName = "";

    @Override
    public String toString() {
        return "LanguageModel{" +
                "id='" + id + '\'' +
                ", dateModified='" + dateModified + '\'' +
                ", direction='" + direction + '\'' +
                ", language='" + language + '\'' +
                ", checkingEntity='" + checkingEntity + '\'' +
                ", checkingLevel='" + checkingLevel + '\'' +
                ", comments='" + comments + '\'' +
                ", contributors='" + contributors + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", sourceText='" + sourceText + '\'' +
                ", sourceTextVersion='" + sourceTextVersion + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
