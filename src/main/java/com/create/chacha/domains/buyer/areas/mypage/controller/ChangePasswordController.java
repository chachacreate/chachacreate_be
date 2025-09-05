package com.create.chacha.domains.buyer.areas.mypage.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.buyer.areas.mypage.dto.request.ChangePasswordRequestDTO;
import com.create.chacha.domains.buyer.areas.mypage.service.ChangePasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class ChangePasswordController {

    @Autowired
    private final ChangePasswordService changePasswordService;

    @PatchMapping("/changepwd")
    public ApiResponse<String> changeMyPassword(@AuthenticationPrincipal SecurityUser user,
                                                @RequestBody ChangePasswordRequestDTO request) {
        changePasswordService.changePasswordFor(user.getMemberId(), request.getCurrentPassword(),
                                                request.getNewPassword(), request.getNewPasswordConfirm()
        );

        return new ApiResponse<>(ResponseCode.OK, "비밀 번호 변경이 완료되었습니다.");
    }

}

