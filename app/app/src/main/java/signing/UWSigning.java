package signing;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.daoModels.Book;
import model.daoModels.SigningOrganization;
import model.daoModels.Verification;

/**
 * Created by PJ Fechner on 3/13/15.
 * Class to deal with the signing for UW
 */
public class UWSigning {
    private static final String TAG = "UWSigning";

//    private static final String signatureJsonKey = "sig";
//    private static final String signingEntityUrl = "https://pki.unfoldingword.org/uW-vk.pem";
    private static final String stockCert = "certs/uW_vk_2.pem";
    private static final String stockCertPub = "certs/ca.pub";


    public static void updateBookVerification(Context context, Book book, byte[] text, String sigData) throws IOException{

        ArrayList<Verification> verifications = getVerifications(context, text, sigData);
        if(verifications  != null) {
            updateBookVerifications(context, verifications, book.getId());
        }
    }

    private static ArrayList<Verification> getVerifications(Context context, byte[] text, String sigData){

        try {
//            String sigData = URLDownloadUtil.downloadString(book.getSignatureUrl());

            ArrayList<Verification> verifications = new ArrayList<>();

            if(sigData.contains("404")){
                Verification errorModel = new Verification();
                errorModel.setStatus(2);
                verifications.add(errorModel);
            }
            else {

                JSONArray sigArray = new JSONArray(sigData);
                for (int i = 0; i < sigArray.length(); i++) {
                    JSONObject obj = sigArray.getJSONObject(i);
                    Verification model = new Verification();
                    model = (Verification) model.setupModelFromJson(obj);

                    if (model == null) {

                        Verification errorModel = new Verification();
                        errorModel.setStatus(2);
                        verifications.add(errorModel);
                        break;
                    }

                    SigningEntity signingEntity = getSigningEntity(context);

                    Status sigStatus = signingEntity.verifyContent(model.getSignature(), text);
                    if (sigStatus != Status.VERIFIED) {
                        Log.e(TAG, "Signature not verified: " + sigStatus.toString());
                    } else {
                        Log.i(TAG, "Signature status: " + sigStatus.toString());
                    }
                    model.setStatus(sigStatus.ordinal());

                    verifications.add(model);
                }
            }

            return verifications;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static void updateBookVerifications(Context context, List<Verification> newModels, long bookId){

        List<Verification> currentVerifications = Verification.getModelForBookId(bookId, DaoDBHelper.getDaoSession(context));

        for(Verification oldModel : currentVerifications){
            oldModel.delete();
        }

        for(Verification newModel : newModels){
            newModel.insertModel(DaoDBHelper.getDaoSession(context));
        }
    }

    private static SigningEntity getSigningEntity(Context context) throws IOException{

        InputStream uwKeyFile = context.getAssets().open(stockCertPub);
        PublicKey uwPublicKey = Crypto.loadPublicECDSAKey(uwKeyFile);

//        byte[] keyData = URLDownloadUtil.downloadBytes(signingEntityUrl);
        InputStream signingStream = context.getAssets().open(stockCert);
        SigningEntity entity = SigningEntity.generateFromIdentity(uwPublicKey, signingStream);

        if(entity != null) {
            updateOrganization(context, entity.organization);
        }

        return entity;
    }

    synchronized private static void updateOrganization(Context context, Organization org){

        SigningOrganization oldOrg = SigningOrganization.getModelForUniqueSlug(org.slug, DaoDBHelper.getDaoSession(context));

        if(oldOrg == null){
            SigningOrganization signingOrg = new SigningOrganization();
            signingOrg.updateWithOrganization(org, DaoDBHelper.getDaoSession(context));
        }
        else{
            oldOrg.updateWithOrganization(org, DaoDBHelper.getDaoSession(context));
        }
    }
}
