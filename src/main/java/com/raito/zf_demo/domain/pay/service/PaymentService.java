package com.raito.zf_demo.domain.pay.service;

import cn.hutool.json.JSONObject;
import com.raito.zf_demo.domain.pay.enums.PayType;

/**
 * @author raito
 * @since 2024/09/06
 */
public interface PaymentService {
    void createPayment(JSONObject bean, String decrypt, PayType payType);
}
