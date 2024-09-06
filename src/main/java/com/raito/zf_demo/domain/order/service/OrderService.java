package com.raito.zf_demo.domain.order.service;

import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.enums.PayType;

/**
 * @author raito
 * @since 2024/09/05
 */
public interface OrderService {
    Order createOrder(Long productId, PayType type);

    Order getOrder(String orderNo);
}
