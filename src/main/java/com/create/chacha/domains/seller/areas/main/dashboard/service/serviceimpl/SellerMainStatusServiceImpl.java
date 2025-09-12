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

 // Impl
    @Override
    public OrderStatusCountResponseDTO getOrderStatusCounts(String storeUrl, String jsessionId) {
        var main = legacyAPIUtil.fetchLegacyMain(storeUrl, jsessionId); // 아래 B절의 새 메서드
        var list = main.getStatusList() == null ? List.<LegacyOrderStatusResponseDTO>of() : main.getStatusList();
        long newOrders=0, delivered=0, cancelRequests=0, refunds=0;
        for (var r : list) {
            switch (r.getStatus()) {
                case "ORDER_OK"  -> newOrders = r.getCount();
                case "DELIVERED" -> delivered = r.getCount();
                case "CANCEL_RQ" -> cancelRequests = r.getCount();
                case "REFUND_OK" -> refunds = r.getCount();
            }
        }
        return OrderStatusCountResponseDTO.builder()
                .newOrders(newOrders).delivered(delivered)
                .cancelRequests(cancelRequests).refunds(refunds).build();
    }

}
