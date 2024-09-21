package com.raito.zf_demo.domain.order.service.impl;

import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.order.entity.Product;
import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.order.repo.OrderRepo;
import com.raito.zf_demo.domain.order.repo.ProductRepo;
import com.raito.zf_demo.domain.order.service.OrderService;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.infrastructure.context.LoginContext;
import com.raito.zf_demo.infrastructure.exception.ValidateException;
import com.raito.zf_demo.infrastructure.factory.ChainContext;
import com.raito.zf_demo.infrastructure.factory.HandlerFactory;
import com.raito.zf_demo.infrastructure.util.OrderNoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author raito
 * @since 2024/09/05
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @Override
    public Order createOrder(Long productId, PayType type) {
        ChainContext context = new ChainContext().set("order", orderRepo.findOrder(productId, type, LoginContext.getUserId()));
        Product product = productRepo.findById(productId).orElseThrow(() -> new ValidateException("商品不存在!"));
        return HandlerFactory.create(context)
                .validator(ctx -> ctx.get("order", Order.class) == null)
                .executor(ctx -> {
                    Order order = Order.builder()
                            .title(product.getTitle())
                            .orderNo(OrderNoUtils.getOrderNo())
                            .product(product)
                            .userId(LoginContext.getUserId())
                            .totalFee(product.getPrice())
                            .status(OrderStatus.NOT_PAY)
                            .payType(type)
                            .build();
                    ctx.set("order", order);
                    orderRepo.save(order);
                })
                .executeTransaction()
                .get("order", Order.class);
    }

    @Override
    public Order getOrder(String orderNo) {
        return orderRepo.findByOrderNo(orderNo);
    }
}
