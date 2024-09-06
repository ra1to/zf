package com.raito.zf_demo.domain.pay.factory;

import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.enums.wxpay.WxApi;
import com.raito.zf_demo.domain.pay.enums.wxpay.WxNotify;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpPost;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author raito
 * @since 2024/09/05
 */
@Component
@DependsOn("wxPayConfig")
@SuppressWarnings("SpellCheckingInspection")
@RequiredArgsConstructor
public class WxPayHttpFactory extends PayHttpFactory {
    private final WxPayConfig wxPayConfig;
    @Override
    public HttpPost getQRCode(Order order) {
        HttpPost httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApi.NATIVE_PAY.getUrl()));
        Map<String, Object> args = buildPayArgs(order);
        return buildHttpPost(httpPost, args);
    }

    protected final Map<String, Object> buildPayArgs(Order order) {
        Map<String, Object> args = new HashMap<>(Map.of(
                "appid", wxPayConfig.getAppid(),
                "mchid", wxPayConfig.getMchId(),
                "description", order.getTitle(),
                "out_trade_no", order.getOrderNo(),
                "notify_url", wxPayConfig.getNotifyDomain().concat(WxNotify.NATIVE_NOTIFY.getUrl())
        ));
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", order.getTotalFee());
        amount.put("currency", "CNY");
        args.put("amount", amount);
        return args;
    }
}
