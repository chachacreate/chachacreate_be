package com.create.chacha.domains.buyer.areas.mypage.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.buyer.areas.mypage.dto.request.CancelRefundRequestDTO;
import com.create.chacha.domains.buyer.areas.mypage.service.CancelRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/order")
public class CancelRefundController {

    @Autowired
    CancelRefundService cancelRefundService;

    @PostMapping("/cancel")
    public ApiResponse<String> requestCancel(@RequestBody CancelRefundRequestDTO request) {
        cancelRefundService.requestCancel(request.getOrderDetailId(), request.getAmount(), request.getContent());
        return new ApiResponse<>(ResponseCode.ORDER_CANCEL_OK, "취소 요청 성공!");
    }

    @PostMapping("/refund")
    public ApiResponse<String> requestRefund(@RequestBody CancelRefundRequestDTO request) {
        cancelRefundService.requestRefund(request.getOrderDetailId(), request.getAmount(), request.getContent());
        return new ApiResponse<>(ResponseCode.ORDER_REFUND_OK, "환불 요청 성공!");
    }
}
