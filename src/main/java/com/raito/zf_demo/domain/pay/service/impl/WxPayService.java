package com.raito.zf_demo.domain.pay.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.entity.Refund;
import com.raito.zf_demo.domain.pay.factory.PayHttpFactory;
import com.raito.zf_demo.domain.pay.service.PayService;
import com.raito.zf_demo.domain.pay.service.RefundService;
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
    private final RefundService refundService;
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

    @Override
    public void closeOrder(Order order) {
        try (CloseableHttpResponse response = wxPayClient.execute(payHttpFactory.getCancel(order))) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) {
                log.debug("关单成功");
            } else {
                log.error("取消订单失败,响应码 = " + statusCode);
                throw new IOException("request failed");
            }
        } catch (IOException e) {
            throw new RemoteException("取消订单失败", e);
        }
    }

    @Override
    public String queryOrder(Order order) {
        log.info("查单接口调用 ===> {}", order.getOrderNo());
        try (CloseableHttpResponse response = wxPayClient.execute(payHttpFactory.getQryOrder(order))) {
            String body = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200 -> log.info("查单接口调用成功, 返回结果 = " + body);
                case 204 -> log.info("成功");
                default -> {
                    log.info("查单接口调用,响应码 = " + statusCode + ",返回结果 = " + body);
                    throw new IOException("request failed");
                }
            }
            return body;
        } catch (IOException e) {
            throw new RemoteException(e);
        }
    }

    @Override
    @Transactional
    public void refund(Refund refund) {
        try (CloseableHttpResponse response = wxPayClient.execute(payHttpFactory.getRefund(refund))) {
            String body = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200 -> log.info("退款成功, 返回结果 = " + body);
                case 204 -> log.info("退款成功");
                default -> throw new RemoteException("退款接口调用,响应码 = " + statusCode + ",返回结果 = " + body);
            }
            refund.getOrder().setStatus(OrderStatus.REFUND_PROCESSING);
            refundService.updateRefund(refund, body);
        } catch (Exception e) {
            throw new RemoteException(e);
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
