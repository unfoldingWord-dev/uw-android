/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package signing;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;

/**
 * A Signing Identity represents an organization that has been registered to sign content.
 *
 */
public class SigningEntity {

    private static final String TAG = "SigningEntity";
    private final PublicKey mCAPublicKey;
    private final PublicKey mPublicKey;
    public final Organization organization;
    private final byte[] mData;
    private final byte[] mSignature;
    private Status mStatus;



    /**
     * Creates a new Signing Entity
     * @param caPublicKey The Certificate Authority's public key
     * @param publicKey The Signing Entity's public key
     * @param organization The entity's organization info
     * @param keyOrgData The concated public key and organization data
     * @param keyOrgSig The signature of the entity's concated public key and organization
     */
    public SigningEntity(PublicKey caPublicKey, PublicKey publicKey, Organization organization, byte[] keyOrgData, byte[] keyOrgSig) {
        mCAPublicKey = caPublicKey;
        mPublicKey = publicKey;
        mSignature = keyOrgSig;
        // this techncially duplicates the key and org data, but we pass it along so we don't convert everything to bytes again
        // and possibly introduce additional points of error
        mData = keyOrgData;
        this.organization = organization;
    }

    /**
     * Checks the validation status of this Signing Entity
     * @return
     */
    public Status status() {
        if(mStatus == null) {
            mStatus = Crypto.verifyECDSASignature(mCAPublicKey, mSignature, mData);
            if(mStatus == Status.VERIFIED) {
                // check if expired
                if(new Date().after(organization.expiresAt)) {
                    mStatus = Status.EXPIRED;
                }
            }
        }
        return mStatus;
    }

    /**
     * Checks the validation status of the signed content.
     * @param signature The signature of hte data as signed by the Se
     * @param data The data that will be validated against the signature (the source translation)
     * @return
     */
    public Status verifyContent(String signature, byte[] data) {

        if(signature == null){
            Log.e(TAG, "signature Error");
            return Status.ERROR;
        }

        byte[] sig = Base64.decode(signature, Base64.NO_WRAP);
        Status contentStatus = verifyContent(sig, data);

        // always return the most severe status
        if(contentStatus.weight() > status().weight()) {
            return contentStatus;
        } else {
            return status();
        }
    }

    /**
     * Checks the validation status of the signed content.
     * @param signature The signature of the data as signed by the SE
     * @param data The data that will be validated against the signature (the source translation)
     * @return
     */
    public Status verifyContent(byte[] signature,  byte[] data) {
        Status contentStatus = Crypto.verifyECDSASignature(mPublicKey, signature, data);

        // always return the most severe status
        if(contentStatus.weight() > status().weight()) {
            return contentStatus;
        } else {
            return status();
        }
    }

    /**
     * Generates a new signing entity from the signing identity
     * @param caPublicKey The The Certificate Authority's public key
     * @param signingIdentity An input stream to the Signing Identity
     * @return
     */
    public static SigningEntity generateFromIdentity(PublicKey caPublicKey, InputStream signingIdentity) {
        BufferedReader br = new BufferedReader(new InputStreamReader(signingIdentity));
        StringBuilder pkBuilder = new StringBuilder();
        StringBuilder orgBuilder = new StringBuilder();
        StringBuilder sigBuilder = new StringBuilder();
        StringBuilder dataBuilder = new StringBuilder();

        // read Signing Identity
        try {
            String section = null;
            String line;
            while((line = br.readLine()) != null) {
                if(line.startsWith("-----")) {
                    // start/end section
                    section = line;
                } else if(!line.trim().isEmpty()){
                    // build sections
                    if(section.equals("-----BEGIN PUBLIC KEY-----")) {
                        pkBuilder.append(line.trim());
                    } else if(section.equals("-----BEGIN ORG INFO-----")) {
                        orgBuilder.append(line.trim());
                    } else if(section.equals("-----BEGIN SIG-----")) {
                        sigBuilder.append(line.trim());
                    }
                }

                // store everything but the signature for verification
                if(!section.equals("-----BEGIN SIG-----") && !section.equals("-----END SIG-----")) {
                    // TRICKY: we intentionally close with a trailing new line
                    dataBuilder.append(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Assemble Signing Entity
        if(dataBuilder.length() > 0 && pkBuilder.length() > 0 && orgBuilder.length() > 0 && sigBuilder.length() > 0) {
            byte[] keyOrgData;
            try {
                keyOrgData = dataBuilder.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            byte[] keyBytes;
            try {
                keyBytes = Base64.decode(pkBuilder.toString().getBytes("UTF-8"), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            PublicKey key = Crypto.loadPublicECDSAKey(keyBytes);
            byte[] signature = Base64.decode(sigBuilder.toString(), Base64.NO_WRAP);
            String orgJsonString;
            try {
                orgJsonString = new String(Base64.decode(orgBuilder.toString(), Base64.NO_WRAP));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            Organization org = Organization.generate(orgJsonString);
            if(org != null) {
                return new SigningEntity(caPublicKey, key, org, keyOrgData, signature);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "SigningEntity{" +
                "mCAPublicKey=" + mCAPublicKey +
                ", mPublicKey=" + mPublicKey +
                ", organization=" + organization +
                ", mData=" + Arrays.toString(mData) +
                ", mSignature=" + Arrays.toString(mSignature) +
                ", mStatus=" + mStatus +
                '}';
    }
}
