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
import com.raito.zf_demo.infrastructure.factory.ChainFactory;
import com.raito.zf_demo.infrastructure.factory.Wrapper;
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
        final Wrapper<Order> wrapper = Wrapper.create(orderRepo.findOrder(productId, type));
        Product product = productRepo.findById(productId).orElseThrow(() -> new ValidateException("商品不存在!"));
        ChainFactory.create()
                .validator(() -> wrapper.getData() == null)
                .executor(() -> wrapper.setData(Order.builder()
                        .title(product.getTitle())
                        .orderNo(OrderNoUtils.getOrderNo())
                        .product(product)
                        .userId(LoginContext.getUserId())
                        .totalFee(product.getPrice())
                        .status(OrderStatus.NOT_PAY)
                        .payType(type)
                        .build()))
                .executor(() -> orderRepo.save(wrapper.getData()))
                .execute();
        return wrapper.getData();
    }

    @Override
    public Order getOrder(String orderNo) {
        return orderRepo.findByOrderNo(orderNo);
    }
}
