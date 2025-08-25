package com.create.chacha.domains.seller.areas.main.dashboard.service.serviceimpl;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.OrderStatusCountResponseDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.repository.OrderInfoRepository;
import com.create.chacha.domains.seller.areas.main.dashboard.service.SellerMainStatusService;
import com.create.chacha.domains.shared.constants.OrderInfoStatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerMainStatusServiceImpl implements SellerMainStatusService {

    private final OrderInfoRepository orderInfoRepository;

    @Override
    public OrderStatusCountResponseDTO getOrderStatusCounts(String storeUrl) {
        return OrderStatusCountResponseDTO.builder()
                .newOrders(orderInfoRepository.countByStoreUrlAndStatus(storeUrl, OrderInfoStatusEnum.ORDER_OK))
                .delivered(orderInfoRepository.countByStoreUrlAndStatus(storeUrl, OrderInfoStatusEnum.DELIVERED))
                .cancelRequests(orderInfoRepository.countByStoreUrlAndStatus(storeUrl, OrderInfoStatusEnum.CANCEL_RQ))
                .refunds(orderInfoRepository.countByStoreUrlAndStatus(storeUrl, OrderInfoStatusEnum.REFUND_OK))
                .build();
    }
}
