package com.raito.zf_demo.domain.order.entity;

import com.raito.zf_demo.domain.order.enums.OrderStatus;
import com.raito.zf_demo.domain.pay.entity.Refund;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * @author raito
 * @since 2024/09/05
 */
@EqualsAndHashCode(callSuper = true)
@Table(name = "zf_orders")
@Entity
@Comment("订单信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Order extends BaseEntity {
    @Comment("订单标题")
    private String title;

    @Comment("订单号")
    @Column(name = "order_no", unique = true, nullable = false)
    private String orderNo;

    @Comment("用户id")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Comment("商品id")
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @ManyToOne
    private Product product;

    @Comment("订单金额, 精确到分")
    private Integer totalFee;

    @Comment("订单二维码链接")
    private String codeUrl;

    @Comment("订单状态")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @OneToOne(mappedBy = "order")
    private Refund refund;

    @Comment("支付类型")
    @Enumerated(value = EnumType.STRING)
    private PayType payType;
}
