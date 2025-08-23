package com.create.chacha.domains.seller.areas.main.dashboard.service;

import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.OrderStatusCountResponseDTO;

public interface SellerMainStatusService {
	
	OrderStatusCountResponseDTO getOrderStatusCounts(String storeUrl);
}
