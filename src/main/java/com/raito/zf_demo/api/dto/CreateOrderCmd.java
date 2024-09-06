package com.raito.zf_demo.api.dto;

import com.raito.zf_demo.domain.pay.enums.PayType;

/**
 * @author raito
 * @since 2024/09/05
 */
public record CreateOrderCmd(Long productId, PayType type) {

}
