package com.create.chacha.domains.buyer.areas.mypage.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.buyer.areas.mypage.dto.request.ChangeAddressRequestDTO;
import com.create.chacha.domains.buyer.areas.mypage.dto.request.ChangePasswordRequestDTO;
import com.create.chacha.domains.buyer.areas.mypage.dto.response.ChangeAddressResponseDTO;
import com.create.chacha.domains.buyer.areas.mypage.service.MemberUpdateService;
import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MemberUpdateController {

    @Autowired
    private final MemberUpdateService memberUpdateService;

    @PatchMapping("/changepwd")
    public ApiResponse<String> changeMyPassword(@AuthenticationPrincipal SecurityUser user,
                                                @RequestBody ChangePasswordRequestDTO request) {
        memberUpdateService.changePasswordFor(user.getMemberId(), request.getCurrentPassword(),
                                                request.getNewPassword(), request.getNewPasswordConfirm()
        );

        return new ApiResponse<>(ResponseCode.PASSWORD_CHANGE_OK, "비밀번호 변경이 완료되었습니다.");
    }

    @PatchMapping("/changeaddr")
    public ApiResponse<ChangeAddressResponseDTO> changeMyAddress(@AuthenticationPrincipal SecurityUser user,
                                                            @RequestBody ChangeAddressRequestDTO request) {

        memberUpdateService.changeAddressFor(user.getMemberId(), request);
        ChangeAddressResponseDTO response = memberUpdateService.getChangedAddress(user.getMemberId());

        return new ApiResponse<>(ResponseCode.ADDR_CHANGE_OK, response);
    }

}

