package be.nixekinder.testcoffeetogo;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dan on 13.10.16.
 */

public class ApiSecurity {
    private String secret;
    private String message;
    private String encodedHash;

    public void setMac() {
        try {
            String secret = getSecret();
            String message = geMessage();

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes("UTF-8")), Base64.DEFAULT); //encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
            setHash(hash);


        } catch (Exception e) {
            Log.i("HMAC", "main: ERROR");
        }

    }


    private static int intVal(byte b) {
        return 0;
    }

    private String geMessage() {
        return message;
    }

    public void setSecurity(String mMessage, String sSecret) {
        this.secret = sSecret;
        this.message = mMessage;
        setMac();
    }

    public String getSecret() {
        return this.secret;
    }

    public String getHash() {
        return encodedHash;
    }

    private void setHash(String hash) {
        encodedHash = hash;
    }
}
