package signing;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import model.DaoDBHelper;
import model.daoModels.Book;
import model.daoModels.SigningOrganization;
import model.daoModels.Verification;
import model.datasource.SigningOrganizationDataSource;
import model.datasource.VerificationDataSource;
import model.modelClasses.mainData.BookModel;
import model.modelClasses.mainData.SigningOrganizationModel;
import model.modelClasses.mainData.VerificationModel;
import utils.URLDownloadUtil;

/**
 * Created by Fechner on 3/13/15.
 */
public class UWSigning {
    private static final String TAG = "UWSigning";

    private static final String signatureJsonKey = "sig";
    private static final String signingEntityUrl = "https://pki.unfoldingword.org/uW-vk.pem";
    private static final String stockCert = "certs/uW_vk_2.pem";
    private static final String stockCertPub = "certs/ca.pub";

    public static void addAndVerifySignatureForBook(Context context, BookModel book, byte[] text) throws IOException{

        try {
            String sigData = URLDownloadUtil.downloadString(book.signatureUrl);

            ArrayList<VerificationModel> verifications = new ArrayList<VerificationModel>();

            if(sigData.contains("404")){
                VerificationModel errorModel = new VerificationModel();
                errorModel.verificationStatus = 2;
                verifications.add(errorModel);
                updateVerifications(context, verifications, book.uid);
                return;
            }

            JSONArray sigArray = new JSONArray(sigData);
            for (int i = 0; i < sigArray.length(); i++) {
                JSONObject obj = sigArray.getJSONObject(i);
                VerificationModel model = new VerificationModel(obj, book.uid, false);

                SigningEntity signingEntity = getSigningEntity(context);

                Status sigStatus = signingEntity.verifyContent(model.signature, text);
                if (sigStatus != Status.VERIFIED) {
                    Log.e(TAG, "Signature not verified: " + sigStatus.toString());
                }
                model.verificationStatus = sigStatus.ordinal();

                verifications.add(model);
            }

            updateVerifications(context, verifications, book.uid);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    public static void updateVerification(Context context, Book book, byte[] text, String sigData) throws IOException{

        try {
//            String sigData = URLDownloadUtil.downloadString(book.getSignatureUrl());

            ArrayList<Verification> verifications = new ArrayList<Verification>();

            if(sigData.contains("404")){
                Verification errorModel = new Verification();
                errorModel.setStatus( 2);
                verifications.add(errorModel);
                updateVerifications(context, verifications, book.getId());
                return;
            }

            JSONArray sigArray = new JSONArray(sigData);
            for (int i = 0; i < sigArray.length(); i++) {
                JSONObject obj = sigArray.getJSONObject(i);
                Verification model = new Verification();
                model = (Verification) model.setupModelFromJson(obj);

                if(model == null){

                    Verification errorModel = new Verification();
                    errorModel.setStatus(2);
                    verifications.add(errorModel);
                    updateVerifications(context, verifications, book.getId());
                    return;
                }

                SigningEntity signingEntity = getSigningEntity(context);

                Status sigStatus = signingEntity.verifyContent(model.getSignature(), text);
                if (sigStatus != Status.VERIFIED) {
                    Log.e(TAG, "Signature not verified: " + sigStatus.toString());
                }
                model.setStatus(sigStatus.ordinal());

                verifications.add(model);
            }

            updateVerifications(context, verifications, book.getId());
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private static void updateVerifications(Context context, List<Verification> newModels, long bookId){

        List<Verification> currentVerifications = Verification.getModelForBookId(bookId, DaoDBHelper.getDaoSession(context));

        for(Verification oldModel : currentVerifications){
            oldModel.delete();
        }

        for(Verification newModel : newModels){
            newModel.insertModel(DaoDBHelper.getDaoSession(context));
        }
    }

    private static void updateVerifications(Context context, ArrayList<VerificationModel> newModels, long bookId){
        VerificationDataSource dataSource = new VerificationDataSource(context);
        ArrayList<VerificationModel> currentVerifications = dataSource.getVerificationsForParentId(Long.toString(bookId));

        for(VerificationModel oldModel : currentVerifications){
            dataSource.deleteModel(oldModel);
        }

        for(VerificationModel newModel : newModels){
            dataSource.createOrUpdateDatabaseModel(newModel);
        }
    }


    private static SigningEntity getSigningEntity(Context context) throws IOException{

        InputStream uwKeyFile = context.getAssets().open(stockCertPub);
        PublicKey uwPublicKey = Crypto.loadPublicECDSAKey(uwKeyFile);

//        byte[] keyData = URLDownloadUtil.downloadBytes(signingEntityUrl);
        InputStream signingStream = context.getAssets().open(stockCert);
        SigningEntity entity = SigningEntity.generateFromIdentity(uwPublicKey, signingStream);

        updateOrganization(context, entity.organization);


        return entity;
    }

    private static void updateOrganization(Context context, Organization org){

        SigningOrganization oldOrg = SigningOrganization.getModelForSlug(org.slug, DaoDBHelper.getDaoSession(context));

        if(oldOrg == null){
            SigningOrganization signingOrg = new SigningOrganization();
            signingOrg.createWithOrganization(org, DaoDBHelper.getDaoSession(context));
        }
        else{
            oldOrg.updateWithOrganization(org, DaoDBHelper.getDaoSession(context));
        }
    }

//    private static void updateOrganization(Context context, Organization org){
//
//        SigningOrganizationModel oldOrg = new SigningOrganizationDataSource(context).getModelForSlug(org.slug);
//
//        if(oldOrg == null){
//            SigningOrganizationModel signingOrg = new SigningOrganizationModel(org);
//            new SigningOrganizationDataSource(context).createOrUpdateDatabaseModel(signingOrg);
//        }
//    }
}
