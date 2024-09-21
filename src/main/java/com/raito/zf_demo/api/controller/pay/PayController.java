package com.raito.zf_demo.api.controller.pay;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.api.vo.Res;
import com.raito.zf_demo.application.pay.handler.PayHandler;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.infrastructure.util.HttpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author raito
 * @since 2024/09/05
 */
@RestController
@RequestMapping("/api/pay")
@Tag(name = "pay-api")
@RequiredArgsConstructor
@Slf4j
public class PayController {

    private final PayHandler handler;

    @Operation(summary = "二维码支付", parameters = {
            @Parameter(name = "type", required = true, description = "支付类型[WX_PAY, ALIPAY]")
    })
    @GetMapping("/qrcode")
    public Res<String> getQRCode(@RequestParam("productId") Long productId, @RequestParam("type") String type) {
        return Res.ok(handler.getQRCode(productId, type));
    }

    @Operation(summary = "微信支付统一回调接口", hidden = true)
    @PostMapping("/wx/zf/notify")
    public String notify(HttpServletRequest request, HttpServletResponse response) {
        String body = HttpUtils.read(request);
        JSONObject obj = JSONUtil.parseObj(body);
        log.debug("微信支付通知 =======\n{}", body);
        return handler.processNotify(obj, request, response, PayType.WX_PAY);
    }

    @PostMapping("/cancel/{orderNo}")
    @Operation(summary = "取消订单")
    public Res<Void> cancel(@PathVariable("orderNo") String orderNo, @RequestParam("type") String type) {
        handler.cancelOrder(orderNo, type);
        return Res.message("关单成功");
    }

    @Operation(summary = "查询订单")
    @GetMapping("/qry_order/{orderNo}")
    public Res<String> qryOrder(@PathVariable("orderNo") String orderNo) {
        return Res.ok(handler.queryOrder(orderNo));
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{orderNo}/{reason}")
    public Res<Void> refund(@PathVariable("orderNo") String orderNo, @PathVariable("reason") String reason) {
        handler.refund(orderNo, reason);
        return Res.message("退款成功");
    }

    @Operation(summary = "查询退款")
    @GetMapping("/qry_refund/{refundNo}")
    public Res<String> qryRefund(@PathVariable("refundNo") String refundNo) {
        return Res.ok("查询退款成功", handler.queryRefund(refundNo));
    }

    @Operation(summary = "微信退款结果通知回调接口")
    @PostMapping("/wx/refunds/notify")
    public String refundsNotify(HttpServletRequest request, HttpServletResponse response) {
        String body = HttpUtils.read(request);
        JSONObject obj = JSONUtil.parseObj(body);
        log.debug("微信退款通知 =======\n{}", body);
        return handler.processRefundNotify(obj, request, response, PayType.WX_PAY);
    }
}
