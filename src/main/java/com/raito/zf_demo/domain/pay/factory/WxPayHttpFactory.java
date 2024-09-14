package com.raito.zf_demo.domain.pay.factory;

import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.entity.Refund;
import com.raito.zf_demo.domain.pay.enums.wxpay.WxApi;
import com.raito.zf_demo.domain.pay.enums.wxpay.WxNotify;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpGet;
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
        String url = wxPayConfig.getDomain().concat(WxApi.NATIVE_PAY.getUrl());
        Map<String, Object> args = buildPayArgs(order);
        return buildHttpPost(url, args);
    }

    @Override
    public HttpPost getCancel(Order order) {
        String url = wxPayConfig.getDomain().concat(String.format(WxApi.CLOSE_ORDER_BY_NO.getUrl(), order.getOrderNo()));
        Map<String, Object> args = buildCancelArgs();
        return buildHttpPost(url, args);
    }

    @Override
    public HttpGet getQryOrder(Order order) {
        String str = String.format(WxApi.ORDER_QUERY_BY_NO.getUrl(), order.getOrderNo());
        String url = wxPayConfig.getDomain().concat(str).concat("?mchid=").concat(wxPayConfig.getMchId());
        return buildHttpGet(url);
    }

    @Override
    public HttpPost getRefund(Refund refund) {
        String url = wxPayConfig.getDomain().concat(WxApi.DOMESTIC_REFUNDS.getUrl());
        Map<String, Object> args = buildRefundArgs(refund);
        return buildHttpPost(url, args);
    }

    protected final Map<String, Object> buildRefundArgs(Refund refund) {
        Map<String, Object> args = new HashMap<>();
        args.put("out_trade_no", refund.getOrder().getOrderNo());//订单编号
        args.put("out_refund_no", refund.getRefundNo());//退款单编号
        args.put("reason",refund.getReason());//退款原因
        args.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotify.REFUND_NOTIFY.getUrl()));//退款通知地址

        Map<String, Object> amount = new HashMap<>();
        amount.put("refund", refund.getRefund());//退款金额
        amount.put("total", refund.getTotalFee());//原订单金额
        amount.put("currency", "CNY");//退款币种
        args.put("amount", amount);
        return args;
    }

    protected final Map<String, Object> buildCancelArgs() {
        return new HashMap<>(Map.of(
                "mchid", wxPayConfig.getMchId()
        ));
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
