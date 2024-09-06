package com.raito.zf_demo.infrastructure.util;

import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raito
 * @since 2024/08/30
 */
@RequiredArgsConstructor
@Slf4j
public class SecretUtils {

    private static final ConcurrentHashMap<String, SecretUtils> instances = new ConcurrentHashMap<>();
    private final byte[] key;
    private final AesUtil aesUtil;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static SecretUtils create(String key) {
        return instances.computeIfAbsent(key, SecretUtils::new);
    }

    public SecretUtils(String key) {
        this.key = key.getBytes(CHARSET);
        this.aesUtil = new AesUtil(this.key);
    }

    @SuppressWarnings("unchecked")
    public String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {
        //通知数据
        Map<String, String> resourceMap = (Map<String, String>) bodyMap.get("resource");
        //数据密文
        String ciphertext = resourceMap.get("ciphertext");
        //随机串
        String nonce = resourceMap.get("nonce");
        //附加数据
        String associatedData = resourceMap.get("associated_data");
        return aesUtil.decryptToString(associatedData.getBytes(CHARSET),
                nonce.getBytes(CHARSET),
                ciphertext);
    }

    public static PublicKey getPublicKey(String certPath) throws FileNotFoundException, CertificateException {
        FileInputStream stream = new FileInputStream(certPath);
        Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(stream);
        return certificate.getPublicKey();
    }

}
