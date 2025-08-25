package com.create.chacha.domains.seller.areas.main.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.constants.OrderInfoStatusEnum;
import com.create.chacha.domains.shared.entity.order.OrderInfoEntity;

public interface OrderInfoRepository extends JpaRepository<OrderInfoEntity, String> {

    /**
     * 특정 스토어(store.url) 기준 주문 상태 카운트
     * - order_info  ← (order_detail) ← product ← store(url)
     * - DISTINCT 로 주문(헤더) 단위 카운트
     */
    @Query(value = """
        SELECT COUNT(DISTINCT oi.id)
          FROM order_info oi
          JOIN order_detail od ON od.order_info_id = oi.id
          JOIN product p       ON p.id = od.product_id
          JOIN store s         ON s.seller_id = p.seller_id
         WHERE s.url = :storeUrl
           AND oi.status = :status
        """, nativeQuery = true)
    long countByStoreUrlAndStatus(@Param("storeUrl") String storeUrl,
                                  @Param("status") OrderInfoStatusEnum status);
}
