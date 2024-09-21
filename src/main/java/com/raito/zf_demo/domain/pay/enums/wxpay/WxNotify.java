package com.raito.zf_demo.domain.pay.enums.wxpay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/08/27
 */
@RequiredArgsConstructor
@Getter
public enum WxNotify {

    /**
     * 支付通知
     */
    NATIVE_NOTIFY("/api/pay/wx/zf/notify"),

    /**
     * 支付通知
     */
    NATIVE_NOTIFY_V2("/api/wx-pay-v2/native/notify"),


    /**
     * 退款结果通知
     */
    REFUND_NOTIFY("/api/pay/wx/refunds/notify");

    /**
     * 类型
     */
    private final String url;

}
