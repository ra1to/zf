package com.raito.zf_demo.domain.pay.service;

import cn.hutool.json.JSONObject;
import com.raito.zf_demo.domain.order.entity.Order;

/**
 * @author raito
 * @since 2024/09/05
 */
public interface PayService {
    String getQRCode(Order order);

    String decrypt(JSONObject obj);
}
