package com.raito.zf_demo.domain.pay.entity;

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
@Comment("支付信息")
@Entity
@Table(name = "zf_payments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseEntity {
    @Comment("订单编号")
    private String orderNo;

    @Comment("支付系统交易编号")
    private String transactionId;

    @Comment("支付类型")
    private String paymentType;

    @Comment("交易类型")
    private String tradeType;

    @Comment("交易状态")
    private String tradeState;

    @Comment("支付金额(分)")
    private Integer payerTotal;

    @Comment("通知参数")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Comment("支付类型")
    private PayType payType;
}
