package com.create.chacha.domains.buyer.areas.mypage.service.serviceImpl;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.domains.buyer.areas.mypage.repository.OrderCancelRepository;
import com.create.chacha.domains.buyer.areas.mypage.repository.OrderRefundRepository;
import com.create.chacha.domains.buyer.areas.mypage.service.CancelRefundService;
import com.create.chacha.domains.buyer.exception.mypage.orderlist.OrderCancelException;
import com.create.chacha.domains.shared.entity.order.OrderCancelEntity;
import com.create.chacha.domains.shared.entity.order.OrderDetailEntity;
import com.create.chacha.domains.shared.entity.order.OrderRefundEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CancelRefundServiceImpl implements CancelRefundService {

    private final OrderCancelRepository cancelRepository;
    private final OrderRefundRepository refundRepository;
    private final LegacyAPIUtil legacyAPIUtil;

    @Override
    public boolean requestCancel(Long orderDetailId, Integer amount, String content) {
        OrderDetailEntity orderDetail = new OrderDetailEntity();
        orderDetail.setId(orderDetailId);

        try {
            OrderCancelEntity cancel = new OrderCancelEntity();
            cancel.setOrderDetail(orderDetail);
            cancel.setAmount(amount);
            cancel.setContent(content);
            cancel.setCreatedAt(LocalDateTime.now());

            cancelRepository.save(cancel);
            legacyAPIUtil.updateLegacyOrderStatus(orderDetailId, "CANCEL_RQ");

            return true;
        } catch (Exception e) {
            throw new OrderCancelException("주문 취소 요청 중 오류 발생", e);
        }
    }

    @Override
    public boolean requestRefund(Long orderDetailId, Integer amount, String content) {
        OrderDetailEntity orderDetail = new OrderDetailEntity();
        orderDetail.setId(orderDetailId);

        try {
            OrderRefundEntity refund = new OrderRefundEntity();
            refund.setOrderDetail(orderDetail);
            refund.setAmount(amount);
            refund.setContent(content);
            refund.setCreatedAt(LocalDateTime.now());

            refundRepository.save(refund);
            legacyAPIUtil.updateLegacyOrderStatus(orderDetailId, "REFUND_RQ");

            return true;
        } catch (Exception e) {
            throw new OrderCancelException("주문 환불 요청 중 오류 발생", e);
        }
    }
}
