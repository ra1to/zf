package com.raito.zf_demo.domain.order.repo;

import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.pay.enums.PayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author raito
 * @since 2024/09/05
 */
@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.product.id = :productId and o.payType = :type and o.userId = :userId")
    Order findOrder(@Param("productId") Long productId, @Param("type") PayType type, @Param("userId") Long userId);

    Order findByOrderNo(String orderNo);

    Order findByOrderNoAndStatus(String orderNo, OrderStatus status);
}
