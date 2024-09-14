package com.raito.zf_demo.domain.pay.factory;

import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.entity.Refund;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * @author raito
 * @since 2024/09/05
 */
public abstract class PayHttpFactory {
    public abstract HttpPost getQRCode(Order order);
    public abstract HttpPost getCancel(Order order);
    public abstract HttpGet getQryOrder(Order order);
    public abstract HttpPost getRefund(Refund refund);



    public final HttpPost buildHttpPost(String url, Map<String, Object> args) {
        HttpPost httpPost = new HttpPost(url);
        String json = JSONUtil.toJsonStr(args);
        StringEntity entity = new StringEntity(json, "utf-8");
        entity.setContentType("application/json");

        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        return httpPost;
    }

    public final HttpGet buildHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        return httpGet;
    }

}
