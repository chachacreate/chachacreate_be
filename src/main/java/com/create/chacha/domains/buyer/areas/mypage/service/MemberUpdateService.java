package com.create.chacha.domains.buyer.areas.mypage.service;

import com.create.chacha.domains.buyer.areas.mypage.dto.request.ChangeAddressRequestDTO;
import com.create.chacha.domains.buyer.areas.mypage.dto.response.ChangeAddressResponseDTO;
import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;

public interface MemberUpdateService {
    void changePasswordFor(Long memberId, String currentPwd, String newPwd, String newConfirmPwd);
    void changeAddressFor(Long memberId, ChangeAddressRequestDTO request);
    ChangeAddressResponseDTO getChangedAddress(Long memberId);
}
