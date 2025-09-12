package com.create.chacha.domains.seller.areas.main.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.OrderStatusCountResponseDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.service.SellerMainStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/seller/{storeUrl}/main")
@RequiredArgsConstructor
@Slf4j
public class SellerMainStatusController {

    private final SellerMainStatusService sellerMainStatusService;

 // Controller
    @GetMapping("/status")
    public ApiResponse<OrderStatusCountResponseDTO> getOrderStatus(
            @PathVariable String storeUrl,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        String jsessionId = null;
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var c : cookies) {
                if ("JSESSIONID".equalsIgnoreCase(c.getName())) {
                    jsessionId = c.getValue();
                    break;
                }
            }
        }
        var dto = sellerMainStatusService.getOrderStatusCounts(storeUrl, jsessionId);
        return new ApiResponse<>(ResponseCode.SELLER_MAIN_STATUS_OK, dto);
    }

}
