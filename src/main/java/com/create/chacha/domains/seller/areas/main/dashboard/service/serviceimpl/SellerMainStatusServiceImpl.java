package com.create.chacha.domains.seller.areas.main.dashboard.service.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.LegacyOrderStatusResponseDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.OrderStatusCountResponseDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.service.SellerMainStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerMainStatusServiceImpl implements SellerMainStatusService {

    private final LegacyAPIUtil legacyAPIUtil;

    @Override
    public OrderStatusCountResponseDTO getOrderStatusCounts(String storeUrl) {
        List<LegacyOrderStatusResponseDTO> statusList = legacyAPIUtil.getLegacyStatusList(storeUrl);

        long newOrders = 0L;
        long delivered = 0L;
        long cancelRequests = 0L;
        long refunds = 0L;

        for (LegacyOrderStatusResponseDTO dto : statusList) {
            log.info("Legacy status row: {}", dto);

            switch (dto.getStatus()) {
                case "ORDER_OK" -> newOrders = dto.getCount();
                case "DELIVERED" -> delivered = dto.getCount();
                case "CANCEL_RQ" -> cancelRequests = dto.getCount();
                case "REFUND_OK" -> refunds = dto.getCount();
                default -> log.warn("알 수 없는 status 값: {}", dto.getStatus());
            }
        }

        return OrderStatusCountResponseDTO.builder()
                .newOrders(newOrders)
                .delivered(delivered)
                .cancelRequests(cancelRequests)
                .refunds(refunds)
                .build();
    }
}
