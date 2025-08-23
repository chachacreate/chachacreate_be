package com.create.chacha.domains.seller.areas.main.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.create.chacha.domains.shared.constants.OrderInfoStatusEnum;
import com.create.chacha.domains.shared.entity.order.OrderInfoEntity;

public interface OrderInfoRepository extends JpaRepository<OrderInfoEntity, String>{

	long countByStatus(OrderInfoStatusEnum status);
}
