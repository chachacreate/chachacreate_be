package com.create.chacha.domains.seller.areas.main.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.OrderStatusCountResponseDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.service.SellerMainStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/seller/{storeUrl}/main")
@RequiredArgsConstructor
@Slf4j
public class SellerMainStatusController {

    private final SellerMainStatusService sellerMainStatusService;

    /**
     * 판매자 메인페이지 - 주문 처리 상태 건수 조회
     */
    @GetMapping("/status")
    public ResponseEntity<OrderStatusCountResponseDTO> getOrderStatus(@PathVariable String storeUrl) {
        log.info("판매자 메인 주문 상태 조회 - storeUrl: {}", storeUrl);
        OrderStatusCountResponseDTO response = sellerMainStatusService.getOrderStatusCounts(storeUrl);
        return ResponseEntity.ok(response);
    }
}