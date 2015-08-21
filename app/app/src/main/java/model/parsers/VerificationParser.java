package model.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import model.UWDatabaseModel;
import model.daoModels.Verification;

/**
 * Created by Fechner on 6/22/15.
 */
public class VerificationParser extends UWDataParser{

    private static final String SIG_INST_JSON_KEY = "si";
    private static final String SIGNATURE_JSON_KEY = "sig";


    public static Verification parseVerification(JSONObject jsonObject, UWDatabaseModel parent) throws JSONException{

        Verification newModel = new Verification();

        newModel.setSigningInstitution(jsonObject.getString(SIG_INST_JSON_KEY));
        newModel.setSignature(jsonObject.getString(SIGNATURE_JSON_KEY));

        return newModel;
    }
}
