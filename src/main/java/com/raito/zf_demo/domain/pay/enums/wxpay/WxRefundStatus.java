package com.raito.zf_demo.domain.pay.enums.wxpay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/09/03
 */
@RequiredArgsConstructor
@Getter
public enum WxRefundStatus {
    /**
     * 退款成功
     */
    SUCCESS("SUCCESS"),

    /**
     * 退款关闭
     */
    CLOSED("CLOSED"),

    /**
     * 退款处理中
     */
    PROCESSING("PROCESSING"),

    /**
     * 退款异常
     */
    ABNORMAL("ABNORMAL");

    /**
     * 类型
     */
    private final String type;
}
