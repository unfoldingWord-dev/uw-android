package model.modelClasses.mainData;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import model.datasource.PageDataSource;
import model.datasource.VerificationDataSource;
import model.modelClasses.AMDatabase.AMDatabaseModelAbstractObject;

/**
 * Created by Fechner on 1/9/15.
 */
public class VerificationModel extends AMDatabaseModelAbstractObject {

    private static final String TAG = "PageModel";

    private static final String SI_JSON_KEY = "si";
    private static final String SIG_JSON_KEY = "sig";

    public String signingInstitution;
    public String signature;
    public int verificationStatus;


    private BookModel parent = null;
    public BookModel getParent(Context context){

        if(parent == null){
            parent = (BookModel) this.getDataSource(context).loadParentModelFromDatabase(this);
        }

        return parent;
    }
    public void setParent(BookModel parent){
        this.parent = parent;
    }

    public VerificationModel() {
        super();
    }

    public VerificationModel(JSONObject jsonObject, boolean sideLoaded) {
        super(jsonObject, sideLoaded);
    }

    public VerificationModel(JSONObject jsonObject, long parentId, boolean sideLoaded) {
        super(jsonObject, parentId, sideLoaded);
    }


    public VerificationDataSource getDataSource(Context context) {
        return new VerificationDataSource(context);
    }

    @Override
    public void initModelFromJson(JSONObject json, boolean sideLoaded) {

        try{
            signingInstitution = (json.has(SI_JSON_KEY))? json.getString(SI_JSON_KEY) : "";
            signature = (json.has(SIG_JSON_KEY))? json.getString(SIG_JSON_KEY) : "";
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        this.uid = -1;
    }

    @Override
    public void initModelFromJson(JSONObject json, long parentId, boolean sideLoaded) {
        this.initModelFromJson(json, sideLoaded);
        this.parentId = parentId;

    }
}








