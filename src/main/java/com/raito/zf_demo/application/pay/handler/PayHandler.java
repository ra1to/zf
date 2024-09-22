package com.raito.zf_demo.application.pay.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.application.pay.validator.WxValidator;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.order.repo.OrderRepo;
import com.raito.zf_demo.domain.order.service.OrderService;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.entity.Refund;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.domain.pay.factory.PayBeanFactory;
import com.raito.zf_demo.domain.pay.repo.RefundRepo;
import com.raito.zf_demo.domain.pay.service.PayService;
import com.raito.zf_demo.domain.pay.service.PaymentService;
import com.raito.zf_demo.domain.pay.service.RefundService;
import com.raito.zf_demo.infrastructure.context.LoginContext;
import com.raito.zf_demo.infrastructure.factory.ChainContext;
import com.raito.zf_demo.infrastructure.factory.HandlerFactory;
import com.raito.zf_demo.infrastructure.util.EnumUtils;
import com.raito.zf_demo.infrastructure.util.LockUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author raito
 * @since 2024/09/05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayHandler {
    private final OrderService orderService;
    private final WxPayConfig config;
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;
    private final RefundService refundService;
    private final RefundRepo refundRepo;

    @Transactional
    public String getQRCode(Long productId, String type) {
        PayType payType = PayType.valueOf(type);
        Order order = orderService.createOrder(productId, payType);
        HandlerFactory.create()
                .addContext("order", order)
                .addContext("service", PayBeanFactory.getBean(type, PayService.class))
                .validator((context) -> context.get("order", Order.class).getCodeUrl() == null)
                .executor((context) -> context.get("service", PayService.class).getQRCode(context.get("order", Order.class)))
                .executeTransaction();
        return order.getCodeUrl();
    }

    public void cancelOrder(String orderNo, String type) {
        PayType payType = EnumUtils.getEnumByName(type, PayType.class);
        HandlerFactory
                .create()
                .validator((ctx) -> payType != null, ctx -> "支付类型不存在!")
                .validator((ctx) -> {
                    Order order = orderRepo.findByOrderNoAndStatus(orderNo, OrderStatus.NOT_PAY);
                    ctx.set("order", order);
                    return order != null;
                }, ctx -> "订单不存在或无法关闭!")
                .validator((ctx) -> Objects.equals(LoginContext.getUserId(), ctx.get("order", Order.class).getUserId()), ctx -> "无法关闭他人订单!")
                .executor((ctx) -> {
                    Order order = ctx.get("order", Order.class);
                    LockUtils.tryLock("cancelOrder:" + orderNo, () -> {
                        PayBeanFactory.getBean(type, PayService.class).closeOrder(order);
                        order.setStatus(OrderStatus.CANCEL);
                    });
                }).executeTransaction();
    }

    public String processNotify(JSONObject obj, HttpServletRequest request, HttpServletResponse response, PayType payType) {
        try {
            HandlerFactory.create()
                    .addContext(obj, request, payType, PayBeanFactory.getBean(payType.name(), PayService.class))
                    .validator(this::validateSign, ctx -> "通知验签失败!\nrequest_id:" + ctx.get(JSONObject.class).get("id"))
                    .executor(this::processOrder)
                    .executeTransaction();
            return processSuccess(response, payType);
        } catch (Exception e) {
            log.error("微信支付通知处理失败！", e);
            return processFail(response, payType, e);
        }
    }

    public String processRefundNotify(JSONObject obj, HttpServletRequest request, HttpServletResponse response, PayType payType) {
        try {
            HandlerFactory.create()
                    .addContext(obj, request, payType, PayBeanFactory.getBean(payType.name(), PayService.class))
                    .validator(this::validateSign, ctx -> "通知验签失败!\nrequest_id:" + ctx.get(JSONObject.class).get("id"))
                    .executor(this::processRefundNotify)
                    .executeTransaction();
            return processSuccess(response, payType);
        } catch (Exception e) {
            log.error("处理退款通知失败");
            return processFail(response, payType, e);
        }
    }

    private void processRefundNotify(ChainContext ctx) {
        Data data = new Data(ctx);
        LockUtils.tryLock("refund:" + data.orderNo, () -> HandlerFactory.create(ctx)
                .validator(context -> {
                    Order order = orderRepo.findByOrderNo(data.orderNo);
                    context.set("order", order);
                    return order != null && order.getStatus() == OrderStatus.REFUND_PROCESSING;
                }, context -> "订单异常!")
                .executor(context -> {
                    Order order = context.get(Order.class);
                    order.setStatus(OrderStatus.REFUND_SUCCESS);
                    refundService.updateRefund(order.getRefund(), data.bean, data.decrypt);
                })
                .executeTransaction());
    }

    public String queryOrder(String orderNo) {
        return HandlerFactory.create()
                .validator(context -> {
                    Order order = orderRepo.findByOrderNo(orderNo);
                    context.set("order", order);
                    return order != null;
                }, context -> "订单不存在!")
                .executor(context -> {
                    Order order = context.get("order", Order.class);
                    PayService service = PayBeanFactory.getBean(order.getPayType().name(), PayService.class);
                    context.set("result", service.queryOrder(order));
                })
                .execute()
                .get("result", String.class);
    }

    private void processOrder(ChainContext ctx) {
        Data data = new Data(ctx);
        LockUtils.tryLock("pay:" + data.orderNo, () -> HandlerFactory.create()
                .executor(context -> context.set("order", orderService.getOrder(data.orderNo)))
                .validator(context -> {
                    Order order = orderRepo.findByOrderNo(data.orderNo);
                    context.set("order", order);
                    return order != null && order.getStatus() == OrderStatus.NOT_PAY;
                }, context -> "订单异常!")
                .executor(context -> {
                    Order order = context.get("order", Order.class);
                    order.setStatus(OrderStatus.SUCCESS);
                    paymentService.createPayment(data.bean, data.decrypt, data.type);
                })
                .executeTransaction());
    }

    private String processSuccess(HttpServletResponse response, PayType payType) {
        response.setStatus(200);
        return JSONUtil.toJsonStr(Response.ok());
    }

    private String processFail(HttpServletResponse response, PayType payType, Exception e) {
        response.setStatus(500);
        return JSONUtil.toJsonStr(Response.fail(e));
    }

    public void refund(String orderNo, String reason) {
        HandlerFactory.create()
                .validator(context -> {
                    Order order = orderRepo.findByOrderNo(orderNo);
                    context.set("order", order);
                    return order != null;
                }, context -> "订单不存在!")
                .validator(ctx -> {
                    Order order = ctx.get(Order.class);
                    return Objects.equals(LoginContext.getUserId(), order.getUserId());
                }, ctx -> "无权操作他人订单!")
                .executor(context -> {
                    Order order = context.get("order", Order.class);
                    context.set("service", PayBeanFactory.getBean(order.getPayType().name(), PayService.class));
                    context.set("refund", refundService.createRefund(order, reason));
                })
                .executor(context -> context.get("service", PayService.class).refund(context.get("refund", Refund.class)))
                .executeTransaction();
    }

    public String queryRefund(String refundNo) {
        return HandlerFactory.create()
                .validator(ctx -> {
                    Refund refund = refundRepo.findByRefundNo(refundNo);
                    ctx.set("refund", refund);
                    return refund != null;
                }, ctx -> "退款单号不存在!")
                .executor(ctx -> {
                    Refund refund = ctx.get("refund", Refund.class);
                    PayService service = PayBeanFactory.getBean(refund.getPayType().name(), PayService.class);
                    ctx.set("result", service.queryRefund(refundNo));
                })
                .executeTransaction()
                .get("result", String.class);
    }

    protected final boolean validateSign(ChainContext ctx) {
        PayType payType = ctx.get(PayType.class);
        if (payType == PayType.WX_PAY) {
            return new WxValidator(ctx.get(JSONObject.class), ctx.get(HttpServletRequest.class), config.getVerifier()).validate();
        } else if (payType == PayType.ALIPAY) {
            return false;
        }
        return false;
    }


    private record Response(String code, String message) {
        public static Response ok() {
            return new Response("SUCCESS", "成功");
        }

        public static Response fail(Exception e) {
            return new Response("ERROR", e.getMessage());
        }


        public static Response fail(String code, String message) {
            return new Response(code, message);
        }
    }

    @lombok.Data
    private static class Data {
        private PayService service;
        private JSONObject obj;
        private String decrypt;
        private PayType type;
        private JSONObject bean;
        private String orderNo;

        public Data(ChainContext ctx) {
            service = ctx.get(PayService.class);
            obj = ctx.get(JSONObject.class);
            decrypt = service.decrypt(obj);
            type = ctx.get(PayType.class);
            log.debug("{}退款通知内容:\n{}", type.getDesc(), decrypt);
            bean = JSONUtil.toBean(decrypt, JSONObject.class);
            orderNo = bean.getStr("out_trade_no");
        }
    }
}
