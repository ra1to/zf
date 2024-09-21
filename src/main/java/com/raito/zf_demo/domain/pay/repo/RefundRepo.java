package com.raito.zf_demo.domain.pay.repo;

import com.raito.zf_demo.domain.pay.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author raito
 * @since 2024/09/05
 */
@Repository
public interface RefundRepo extends JpaRepository<Refund, Long> {
    Refund findByRefundNo(String refundNo);
}
