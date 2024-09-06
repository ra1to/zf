package com.raito.zf_demo.application.pay.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.raito.zf_demo.application.pay.validator.WxValidator;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.order.service.OrderService;
import com.raito.zf_demo.domain.pay.config.WxPayConfig;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.domain.pay.factory.PayBeanFactory;
import com.raito.zf_demo.domain.pay.service.PayService;
import com.raito.zf_demo.domain.pay.service.PaymentService;
import com.raito.zf_demo.infrastructure.exception.ConcurrentException;
import com.raito.zf_demo.infrastructure.factory.ChainFactory;
import com.raito.zf_demo.infrastructure.factory.Wrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final static Cache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();


    @Transactional
    public String getQRCode(Long productId, String type) {
        PayType payType = PayType.valueOf(type);
        Order order = orderService.createOrder(productId, payType);
        ChainFactory.create()
                .validator(() -> order.getCodeUrl() == null)
                .executor(() -> PayBeanFactory.getBean(type, PayService.class).getQRCode(order))
                .executeTransaction();
        return order.getCodeUrl();
    }

    public String processNotify(JSONObject obj, HttpServletRequest request, HttpServletResponse response, PayType payType) {
        try {
            ChainFactory.create()
                    .validator(payType == PayType.WX_PAY ? new WxValidator(obj, request, config.getVerifier()) : null)
                    .executor(() -> this.processOrder(obj, payType))
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
                    Wrapper<Order> orderWrapper = new Wrapper<>();
                    ChainFactory.create()
                            .executor(() -> orderWrapper.setData(orderService.getOrder(orderNo)))
                            .validator(() -> orderWrapper.getData() != null && orderWrapper.getData().getStatus() == OrderStatus.NOT_PAY)
                            .executor(() -> orderWrapper.getData().setStatus(OrderStatus.SUCCESS))
                            .executor(() -> paymentService.createPayment(bean, decrypt, payType))
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
