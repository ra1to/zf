package com.raito.zf_demo.domain.pay.enums.wxpay;

import com.raito.zf_demo.infrastructure.util.EnumUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/09/02
 */
@RequiredArgsConstructor
@Getter
@SuppressWarnings("SpellCheckingInspection")
public enum WxTradeState implements EnumUtils {
    /**
     * 支付成功
     */
    SUCCESS("SUCCESS"),

    /**
     * 未支付
     */
    NOT_PAY("NOTPAY"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED"),

    /**
     * 转入退款
     */
    REFUND("REFUND");

    /**
     * 类型
     */
    private final String type;
}
