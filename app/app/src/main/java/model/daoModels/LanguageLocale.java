package model.daoModels;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.parsers.LanguageLocaleParser;
// KEEP INCLUDES END
/**
 * Entity mapped to table "LANGUAGE_LOCALE".
 */
public class LanguageLocale extends model.UWDatabaseModel  implements java.io.Serializable {

    private Long id;
    private String languageDirection;
    private String languageKey;
    private String languageName;
    private String cc;
    private String LanguageRegion;
    private Integer pk;
    private Boolean gw;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public LanguageLocale() {
    }

    public LanguageLocale(Long id) {
        this.id = id;
    }

    public LanguageLocale(Long id, String languageDirection, String languageKey, String languageName, String cc, String LanguageRegion, Integer pk, Boolean gw) {
        this.id = id;
        this.languageDirection = languageDirection;
        this.languageKey = languageKey;
        this.languageName = languageName;
        this.cc = cc;
        this.LanguageRegion = LanguageRegion;
        this.pk = pk;
        this.gw = gw;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguageDirection() {
        return languageDirection;
    }

    public void setLanguageDirection(String languageDirection) {
        this.languageDirection = languageDirection;
    }

    public String getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(String languageKey) {
        this.languageKey = languageKey;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getLanguageRegion() {
        return LanguageRegion;
    }

    public void setLanguageRegion(String LanguageRegion) {
        this.LanguageRegion = LanguageRegion;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Boolean getGw() {
        return gw;
    }

    public void setGw(Boolean gw) {
        this.gw = gw;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json) {

        try {
            return LanguageLocaleParser.parseLanguageLocale(json);
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UWDatabaseModel setupModelFromJson(JSONObject json, UWDatabaseModel parent) {
        return null;
    }

    @Override
    public String getUniqueSlug() {
        return languageKey;
    }

    @Override
    public boolean updateWithModel(UWDatabaseModel newModel) {

        LanguageLocale locale = (LanguageLocale) newModel;

        this.languageDirection = locale.languageDirection;
        this.languageKey = locale.languageKey;
        this.languageName = locale.languageName;
        this.cc = locale.cc;
        this.LanguageRegion = locale.LanguageRegion;
        this.pk = locale.pk;
        this.gw = locale.gw;

        return false;
    }

    @Override
    public void insertModel(DaoSession session) {

        session.getLanguageLocaleDao().insert(this);
    }

    public static LanguageLocale getLocalForKey(String key, DaoSession session){

        return session.getLanguageLocaleDao().queryBuilder()
                .where(LanguageLocaleDao.Properties.LanguageKey.eq(key))
                .unique();
    }
    // KEEP METHODS END

}
