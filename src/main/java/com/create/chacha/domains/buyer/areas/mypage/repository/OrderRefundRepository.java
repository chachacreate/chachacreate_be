package com.create.chacha.domains.buyer.areas.mypage.repository;

import com.create.chacha.domains.shared.entity.order.OrderRefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRefundRepository extends JpaRepository<OrderRefundEntity, Long> {
}
