package com.raito.zf_demo.domain.pay.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.factory.PayHttpFactory;
import com.raito.zf_demo.domain.pay.service.PayService;
import com.raito.zf_demo.infrastructure.exception.KeyException;
import com.raito.zf_demo.infrastructure.exception.RemoteException;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author raito
 * @since 2024/09/05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxPayService implements PayService {
    private final PayHttpFactory payHttpFactory;
    private final CloseableHttpClient wxPayClient;
    private final WxPayConfig config;
    private volatile AesUtil aesUtil = null;
    @Override
    @Transactional
    public String getQRCode(Order order) {
        try (CloseableHttpResponse response = wxPayClient.execute(payHttpFactory.getQRCode(order))) {
            String body = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case 200 -> log.info("成功, 返回结果 = " + body);
                case 204 -> log.info("成功");
                default -> {
                    log.info("Native下单失败,响应码 = " + statusCode + ",返回结果 = " + body);
                    throw new RemoteException("Native下单失败,响应码 = " + statusCode + ",返回结果 = " + body);
                }
            }
            JSONObject result = JSONUtil.parseObj(body);
            String codeUrl = result.get("code_url", String.class);
            order.setCodeUrl(codeUrl);
            return codeUrl;
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public String decrypt(JSONObject obj) {
        try {
            JSONObject resource = obj.get("resource", JSONObject.class);
            // 数据密文
            String ciphertext = resource.getStr("ciphertext");
            // 随机串
            String nonce = resource.getStr("nonce");
            // 附加数据
            String associatedData = resource.getStr("associated_data");
            return getAesUtil().decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8),
                    ciphertext);
        } catch (Exception e) {
            throw new KeyException(e);
        }
    }

    protected final AesUtil getAesUtil() {
        if (aesUtil == null) {
            synchronized (this) {
                if (aesUtil == null) {
                    aesUtil = new AesUtil(config.getApiV3Key().getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        return aesUtil;
    }
}
