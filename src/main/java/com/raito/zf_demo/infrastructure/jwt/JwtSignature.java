package com.raito.zf_demo.infrastructure.jwt;

import com.raito.zf_demo.infrastructure.util.SpringContextUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author raito
 * @since 2024/09/06
 */
public class JwtSignature {
    private static volatile Mac sha256Hmac = null;
    public static String create(String header, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        String data = header + "." + payload;
        byte[] signature = getMac().doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(signature);
    }

    protected static Mac getMac() throws NoSuchAlgorithmException, InvalidKeyException {
        if (sha256Hmac == null) {
            synchronized (JwtSignature.class) {
                if (sha256Hmac == null) {
                    sha256Hmac = Mac.getInstance("HmacSHA256");
                    Key key = new SecretKeySpec(SpringContextUtils.getBean(JwtConfig.class).getKey().getBytes(), "HmacSHA256");
                    sha256Hmac.init(key);
                }
            }
        }
        return sha256Hmac;
    }
}
