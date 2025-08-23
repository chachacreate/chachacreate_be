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
                .newOrders(orderInfoRepository.countByStatus(OrderInfoStatusEnum.ORDER_OK))
                .delivered(orderInfoRepository.countByStatus(OrderInfoStatusEnum.DELIVERED))
                .cancelRequests(orderInfoRepository.countByStatus(OrderInfoStatusEnum.CANCEL_RQ))
                .refunds(orderInfoRepository.countByStatus(OrderInfoStatusEnum.REFUND_OK))
                .build();
    }
}