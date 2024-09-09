package com.raito.zf_demo.application.pay.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.raito.zf_demo.application.pay.validator.WxValidator;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.order.repo.OrderRepo;
import com.raito.zf_demo.domain.order.service.OrderService;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.domain.pay.factory.PayBeanFactory;
import com.raito.zf_demo.domain.pay.service.PayService;
import com.raito.zf_demo.domain.pay.service.PaymentService;
import com.raito.zf_demo.infrastructure.context.LoginContext;
import com.raito.zf_demo.infrastructure.exception.ConcurrentException;
import com.raito.zf_demo.infrastructure.factory.HandlerFactory;
import com.raito.zf_demo.infrastructure.util.EnumUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private final PayService payService;
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;
    private final static Cache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();


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
                    try {
                        ReentrantLock lock = locks.get(orderNo, ReentrantLock::new);
                        if(lock.tryLock()) {
                            PayBeanFactory.getBean(type, PayService.class).closeOrder(order);
                            order.setStatus(OrderStatus.CANCEL);
                        }
                    } catch (ExecutionException e) {
                        throw new ConcurrentException(e);
                    }
                }).executeTransaction();
    }

    public String processNotify(JSONObject obj, HttpServletRequest request, HttpServletResponse response, PayType payType) {
        try {
            HandlerFactory.create()
                    .validator(context -> payType == PayType.WX_PAY ? new WxValidator(obj, request, config.getVerifier()).validate() : false)
                    .executor(context -> this.processOrder(obj, payType))
                    .executeTransaction();
            return processSuccess(response, payType);
        } catch (Exception e) {
            log.error("微信支付通知处理失败！", e);
            return processFail(response, payType, e);
        }
    }

    private void processOrder(JSONObject obj, PayType payType) {
        PayService service = PayBeanFactory.getBean(payType.name(), PayService.class);
        String decrypt = service.decrypt(obj);
        log.info("微信支付通知内容：{}", decrypt);
        JSONObject bean = JSONUtil.toBean(decrypt, JSONObject.class);
        String orderNo = bean.getStr("out_trade_no");
        try {
            Lock lock = locks.get(orderNo, ReentrantLock::new);
            if (lock.tryLock()) {
                try {
                    HandlerFactory.create()
                            .executor(context -> context.set("order", orderService.getOrder(orderNo)))
                            .validator(context -> {
                                Order order = context.get("order", Order.class);
                                if (order != null && order.getStatus() == OrderStatus.NOT_PAY) {
                                    order.setStatus(OrderStatus.SUCCESS);
                                    return true;
                                }
                                return false;
                            })
                            .executor((context) -> paymentService.createPayment(bean, decrypt, payType))
                            .executeTransaction();
                } finally {
                    lock.unlock();
                }
            }

        } catch (ExecutionException e) {
            throw new ConcurrentException(e);
        }

    }

    private String processSuccess(HttpServletResponse response, PayType payType) {
        response.setStatus(200);
        return JSONUtil.toJsonStr(Response.ok());
    }

    private String processFail(HttpServletResponse response, PayType payType, Exception e) {
        response.setStatus(500);
        return JSONUtil.toJsonStr(Response.fail(e));
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
}
