package com.raito.zf_demo.domain.pay.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author raito
 * @since 2024/09/05
 */
@ConfigurationProperties(prefix = "alipay")
@Configuration
@Data
public class AlipayConfig {
    /**
     * 应用ID,APPID，收款账号既是APPID对应支付宝账号
     */
    private String appId;

    /**
     * 商户PID,卖家支付宝账号ID
     */
    private String sellerId;

    /**
     * 支付宝网关
     */
    private String gatewayUrl;

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    private String merchantPrivateKey;

    /**
     * 应用公钥证书
     */
    private String appCertPath;

    /**
     * 支付宝公钥证书
     */
    private String alipayCertPath;

    /**
     * 支付宝根证书路径
     */
    private String alipayRootCertPath;
    /**
     * 接口内容加密秘钥，对称秘钥
     */
    private String contentKey;

    /**
     * 页面跳转同步通知页面路径
     */
    private String returnUrl;

    /**
     * 服务器异步通知页面路径, 必须外网可以正常访问
     */
    private String notifyUrl;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        var config = new com.alipay.api.AlipayConfig();
        config.setServerUrl(gatewayUrl);
        config.setAppId(appId);
        config.setPrivateKey(merchantPrivateKey);
        config.setAlipayPublicCertPath(alipayCertPath);
        config.setAppCertPath(appCertPath);
        config.setRootCertPath(alipayRootCertPath);
        config.setCharset(AlipayConstants.CHARSET_UTF8);
        config.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
        config.setFormat(AlipayConstants.FORMAT_JSON);
        return new DefaultAlipayClient(config);
    }

    public void setMerchantPrivateKey(String merchantPrivateKey) throws IOException {
        try (FileInputStream fs = new FileInputStream(merchantPrivateKey)) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            byte[] buffer = new byte[1024];
            for (int length; (length = fs.read(buffer)) != -1; ) {
                os.write(buffer, 0, length);
            }
            this.merchantPrivateKey = os.toString(StandardCharsets.UTF_8);
        }
    }
}
