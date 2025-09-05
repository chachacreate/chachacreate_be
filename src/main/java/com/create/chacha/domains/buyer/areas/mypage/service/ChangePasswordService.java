package com.create.chacha.domains.buyer.areas.mypage.service;

public interface ChangePasswordService {
    void changePasswordFor(Long memberId, String currentPwd, String newPwd, String newConfirmPwd);
}
