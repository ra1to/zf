package com.raito.zf_demo.domain.pay.service;

import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.entity.Refund;

/**
 * @author raito
 * @since 2024/09/14
 */
public interface RefundService {
    Refund createRefund(Order order, String reason);

    void updateRefund(Refund refund, String body);
}
