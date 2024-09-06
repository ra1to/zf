package com.raito.zf_demo.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/09/05
 */
@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    NOT_PAY("未支付"),
    /**
     * 支付成功
     */
    SUCCESS("支付成功"),

    /**
     * 已关闭
     */
    CLOSED("超时已关闭"),

    /**
     * 已取消
     */
    CANCEL("用户已取消"),

    /**
     * 退款中
     */
    REFUND_PROCESSING("退款中"),

    /**
     * 已退款
     */
    REFUND_SUCCESS("已退款"),

    /**
     * 退款异常
     */
    REFUND_ABNORMAL("退款异常");

    /**
     * 类型
     */
    private final String desc;

}