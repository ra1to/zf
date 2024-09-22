package com.raito.zf_demo.domain.pay.entity;

import com.raito.zf_demo.domain.order.entity.Order;
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
@Data
@Entity
@Table(name = "zf_refunds")
@Comment("退款信息")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund extends BaseEntity {
    @Comment("订单编号")
    @OneToOne
    @JoinColumn(name = "order_no", referencedColumnName = "order_no")
    private Order order;//商品订单编号

    @Comment("退款单编号")
    private String refundNo;//退款单编号

    @Comment("支付系统退款单号")
    private String refundId;//支付系统退款单号

    @Comment("原订单金额(分)")
    private Integer totalFee;//原订单金额(分)

    @Comment("退款金额(分)")
    private Integer refund;//退款金额(分)

    @Comment("退款原因")
    private String reason;//退款原因

    @Comment("退款单状态")
    private String refundStatus;//退款单状态

    @Comment("申请退款返回参数")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String contentReturn;//申请退款返回参数

    @Comment("退款结果通知参数")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String contentNotify;//退款结果通知参数

    @Enumerated(EnumType.STRING)
    @Comment("支付类型")
    private PayType payType;
}
