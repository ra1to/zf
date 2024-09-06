package com.raito.zf_demo.domain.order.repo;

import com.raito.zf_demo.domain.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author raito
 * @since 2024/09/05
 */
@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
}
