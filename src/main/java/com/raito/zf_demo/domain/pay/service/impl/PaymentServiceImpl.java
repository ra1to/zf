package com.raito.zf_demo.domain.pay.service.impl;

import cn.hutool.json.JSONObject;
import com.raito.zf_demo.domain.pay.entity.Payment;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.domain.pay.repo.PaymentRepo;
import com.raito.zf_demo.domain.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author raito
 * @since 2024/09/06
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    @Override
    public void createPayment(JSONObject bean, String decrypt, PayType payType) {
        JSONObject amount = bean.get("amount", JSONObject.class);
        Payment payment = Payment.builder()
                .orderNo(bean.getStr("out_trade_no"))
                .paymentType(bean.getStr("trade_type"))
                .transactionId(bean.getStr("transaction_id"))
                .tradeType(bean.getStr("trade_type"))
                .tradeState(bean.getStr("trade_state"))
                .payerTotal(Integer.parseInt(amount.getStr("payer_total")))
                .content(decrypt)
                .payType(payType)
                .build();
        paymentRepo.save(payment);
    }
}
