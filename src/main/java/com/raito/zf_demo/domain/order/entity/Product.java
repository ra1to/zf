package com.raito.zf_demo.domain.order.entity;

import com.raito.zf_demo.infrastructure.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

/**
 * @author raito
 * @since 2024/09/05
 */
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_products")
@Entity
@Data
@Comment("商品信息")
public class Product extends BaseEntity {

    @Comment("商品名称")
    private String title;

    @Comment("商品价格(分)")
    private Integer price;

}