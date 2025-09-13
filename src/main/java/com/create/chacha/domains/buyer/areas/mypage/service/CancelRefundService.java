package com.create.chacha.domains.buyer.areas.mypage.service;

public interface CancelRefundService {
    public boolean requestCancel(Long orderDetailId, Integer amount, String content);
    public boolean requestRefund(Long orderDetailId, Integer amount, String content);
}
