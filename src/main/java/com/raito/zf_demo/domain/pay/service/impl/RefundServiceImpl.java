package com.raito.zf_demo.domain.pay.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.domain.order.entity.Order;
import com.raito.zf_demo.domain.pay.entity.Refund;
import com.raito.zf_demo.domain.pay.repo.RefundRepo;
import com.raito.zf_demo.domain.pay.service.RefundService;
import com.raito.zf_demo.infrastructure.exception.RemoteException;
import com.raito.zf_demo.infrastructure.util.OrderNoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author raito
 * @since 2024/09/14
 */
@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRepo repo;

    @Override
    public Refund createRefund(Order order, String reason) {
        return repo.save(Refund.builder()
                .order(order)
                .refundNo(OrderNoUtils.getRefundNo())
                .totalFee(order.getTotalFee())
                .refund(order.getTotalFee())
                .reason(reason)
                .payType(order.getPayType())
                .build());
    }

    @Transactional
    @Override
    public void updateRefund(Refund refund, String body) {
        Map<String, Object> args = JSONUtil.toBean(body, new TypeReference<>() {
        }, false);
        String orderNo = refund.getOrder().getOrderNo();
        if (!orderNo.equals(args.get("out_trade_no").toString())) {
            throw new RemoteException("订单号不匹配");
        }

        refund.setRefundId(args.get("refund_id").toString());
        if (args.get("status") != null) {
            refund.setRefundStatus(args.get("status").toString());
            refund.setContentReturn(body);
        }
        if (args.get("refund_status") != null) {
            refund.setRefundStatus(args.get("refund_status").toString());
            refund.setContentNotify(body);
        }
    }
}
