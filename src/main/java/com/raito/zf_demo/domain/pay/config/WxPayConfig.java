package com.raito.zf_demo.domain.pay.config;

import com.raito.zf_demo.infrastructure.exception.IocException;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

/**
 * @author raito
 * @since 2024/09/05
 */
@Configuration
@ConfigurationProperties(prefix = "wx-pay")
@Data
public class WxPayConfig {
    // 商户号
    private String mchId;

    // 商户API证书序列号
    private String mchSerialNo;

    // 商户私钥文件
    private String privateKeyPath;

    // APIv3密钥
    private String apiV3Key;

    // APPID
    private String appid;

    // 微信服务器地址
    private String domain;

    // 接收结果通知地址
    private String notifyDomain;

    // APIv2密钥
    private String partnerKey;

    private static volatile PrivateKey privateKey = null;

    /**
     * 获取商户的私钥文件
     *
     * @param path 文件路径
     * @return 秘钥
     */
    private PrivateKey getPrivateKey(String path) {

        try {
            if (privateKey == null) {
                synchronized (WxPayConfig.class) {
                    if (privateKey == null) {
                        privateKey = PemUtil.loadPrivateKey(new FileInputStream(path));
                    }
                }
            }
            return privateKey;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("私钥文件不存在", e);
        }

    }

    @Bean
    public CertificatesManager getCertificatesManager() throws GeneralSecurityException, IOException, HttpCodeException {
        // 获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);

        // 私钥签名对象
        PrivateKeySigner signer = new PrivateKeySigner(mchSerialNo, privateKey);

        // 身份认证对象
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(mchId, signer);

        // 获取证书管理器
        CertificatesManager certificatesManager = CertificatesManager.getInstance();

        // 添加需要自动更新平台证书的商户信息
        certificatesManager.putMerchant(mchId, wechatPay2Credentials, apiV3Key.getBytes());

        return certificatesManager;
    }

    @Bean(name = "wxPayClient")
    public CloseableHttpClient getWxPayClient(CertificatesManager manager) throws NotFoundException {
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
        return WechatPayHttpClientBuilder
                .create()
                .withMerchant(mchId, mchSerialNo, privateKey)
                .withValidator(new WechatPay2Validator(manager.getVerifier(mchId)))
                .build();
    }

    @Bean(name = "wxPayNoSignClient")
    public CloseableHttpClient getWxPayNoSignClient() {
        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);

        //用于构造HttpClient
        return WechatPayHttpClientBuilder.create()
                //设置商户信息
                .withMerchant(mchId, mchSerialNo, privateKey)
                //无需进行签名验证、通过withValidator((response) -> true)实现
                .withValidator((response) -> true)
                .build();
    }

    public Verifier getVerifier() {
        try {
            return this.getCertificatesManager().getVerifier(mchId);
        } catch (Exception e) {
            throw new IocException(e);
        }
    }
}
