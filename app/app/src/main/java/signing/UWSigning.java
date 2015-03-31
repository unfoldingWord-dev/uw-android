package signing;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;

import utils.URLDownloadUtil;

/**
 * Created by Fechner on 3/13/15.
 */
public class UWSigning {

    private static final String signatureJsonKey = "sig";
    private static final String signingEntityUrl = "https://pki.unfoldingword.org/uW-vk.pem";

    public static Status getStatusForSigUrl(Context context, String url, byte[] signingData) throws IOException, JSONException{

        InputStream uwKeyFile = context.getAssets().open("certs/ca.pub");
        PublicKey uwPublicKey = Crypto.loadPublicECDSAKey(uwKeyFile);

        byte[] keyData = URLDownloadUtil.downloadBytes(signingEntityUrl);
        InputStream signingStream = new ByteArrayInputStream(keyData);
        SigningEntity signingEntity = SigningEntity.generateFromIdentity(uwPublicKey, signingStream);

        String sigData =  URLDownloadUtil.downloadString(url);

        try {

            JSONArray sigArray = new JSONArray(sigData);
            JSONObject sigObj = sigArray.getJSONObject(0);
            String sig = sigObj.getString(signatureJsonKey);


            Status sigStatus = signingEntity.verifyContent(sig, signingData);
            return sigStatus;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }
}
