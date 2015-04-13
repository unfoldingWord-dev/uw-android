package model.modelClasses.mainData;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONObject;

import model.datasource.LanguageLocaleDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/9/15.
 */
public class LanguageLocaleModel extends AMDatabaseModelAbstractObject {

    private class LanguageLocaleJsonModel {

        boolean gw;
        String ld;
        String lc;
        String ln;
        String[] cc;
        String lr;
    }
    
    private static final String TAG = "PageModel";

    public boolean gw;
    public String languageDirection;
    public String languageKey;
    public String languageName;
    public String cc;
    public String languageRegion;


    public LanguageLocaleModel() {
        super();
    }

    public LanguageLocaleModel(JSONObject jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public LanguageLocaleModel(JSONObject jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }


    public LanguageLocaleDataSource getDataSource(Context context) {
        return new LanguageLocaleDataSource(context);
    }

    @Override
    public void initModelFromJson(JSONObject json, boolean sideLoaded) {

        if(sideLoaded){
            return;
        }
        LanguageLocaleJsonModel model = new Gson().fromJson(json.toString(), LanguageLocaleJsonModel.class);

        this.gw = model.gw;
        this.languageDirection = model.ld;
        this.languageKey = model.lc;
        this.languageName = model.ln;
        this.cc = model.cc[0];
        this.languageRegion = model.lr;

        this.uid = -1;
        this.slug = model.lc;
    }

    @Override
    public void initModelFromJson(JSONObject json, long parentId, boolean sideLoaded) {

        if(sideLoaded){
            return;
        }

        this.initModelFromJson(json, sideLoaded);

        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "LanguageLocaleModel{" +
                "gw=" + gw +
                ", languageDirection='" + languageDirection + '\'' +
                ", languageKey='" + languageKey + '\'' +
                ", languageName='" + languageName + '\'' +
                ", cc='" + cc + '\'' +
                ", languageRegion='" + languageRegion + '\'' +
                "} " + super.toString();
    }


}








