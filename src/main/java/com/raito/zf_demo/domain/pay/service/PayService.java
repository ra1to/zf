package com.raito.zf_demo.domain.pay.service;

import cn.hutool.json.JSONObject;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.entity.Refund;

/**
 * @author raito
 * @since 2024/09/05
 */
public interface PayService {
    String getQRCode(Order order);

    String decrypt(JSONObject obj);

    void closeOrder(Order order);

    String queryOrder(Order order);

    void refund(Refund refund);

    String queryRefund(String refundNo);
}
