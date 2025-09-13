package com.create.chacha.domains.buyer.areas.mypage.repository;

import com.create.chacha.domains.shared.entity.order.OrderCancelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderCancelRepository extends JpaRepository<OrderCancelEntity, Long> {
}
