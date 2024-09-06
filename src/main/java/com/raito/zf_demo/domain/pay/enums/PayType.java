package com.raito.zf_demo.domain.pay.enums;

import com.raito.zf_demo.infrastructure.util.EnumUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/09/05
 */
@RequiredArgsConstructor
@Getter
public enum PayType implements EnumUtils {
    WX_PAY("微信支付"),
    ALIPAY("支付宝支付");

    private final String desc;
}
