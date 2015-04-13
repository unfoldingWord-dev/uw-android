package signing;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;

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

    private static final String signatureJsonKey = "sig";
    private static final String signingEntityUrl = "https://pki.unfoldingword.org/uW-vk.pem";
    private static final String stockCert = "certs/uW_vk_2.pem";
    private static final String stockCertPub = "certs/ca.pub";

    public static void addAndVerifySignatureForBook(Context context, BookModel book, byte[] text) throws JSONException, IOException{

        String sigData =  URLDownloadUtil.downloadString(book.signatureUrl);
        JSONArray sigArray = new JSONArray(sigData);
        ArrayList<VerificationModel> verifications = new ArrayList<VerificationModel>();
        for(int i = 0; i < sigArray.length(); i++){
            JSONObject obj = sigArray.getJSONObject(i);
            VerificationModel model = new VerificationModel(obj, book.uid, false);

            SigningEntity signingEntity = getSigningEntity(context);

            Status sigStatus = signingEntity.verifyContent(model.signature, text);
            model.verificationStatus = sigStatus.ordinal();

            verifications.add(model);
        }

        updateVerifications(context, verifications, book.uid);


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

        SigningOrganizationModel oldOrg = new SigningOrganizationDataSource(context).getModelForSlug(org.slug);

        if(oldOrg == null){
            SigningOrganizationModel signingOrg = new SigningOrganizationModel(org);
            new SigningOrganizationDataSource(context).createOrUpdateDatabaseModel(signingOrg);
        }
    }
}
