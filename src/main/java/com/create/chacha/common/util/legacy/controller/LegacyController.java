package com.create.chacha.common.util.legacy.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.common.util.legacy.service.LegacyInfoService;
import com.create.chacha.domains.shared.member.exception.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class LegacyController {
    @Autowired
    LegacyInfoService legacyInfoService;

    @GetMapping("/info/seller/{memberId}")
    public ApiResponse<MemberEntity> sendMemberEntityBySellerId(@PathVariable Integer memberId){
        MemberEntity memberEntity = legacyInfoService.getSellerInfo(memberId);
        log.info(memberEntity.toString());
        return new ApiResponse<>(ResponseCode.OK, memberEntity);
    }
    @GetMapping("/info/memberAddress/{memberId}")
    public ApiResponse<MemberAddressEntity> sendMemberAddressEntityByMemberId(@PathVariable Integer memberId){
        MemberAddressEntity address = legacyInfoService.getMemberAddress(memberId);
        log.info(address.toString());
        return new ApiResponse<>(ResponseCode.OK, address);
    }

    @GetMapping("/info/memberAddressByAddressId/{addressId}")
    public ApiResponse<MemberAddressEntity> sendMemberAddressEntityByMemberAddressId(@PathVariable Integer addressId){
        MemberAddressEntity address = legacyInfoService.getMemberAddressByAddressId(addressId);
        log.info(address.toString());
        return new ApiResponse<>(ResponseCode.OK, address);
    }

    @GetMapping("/info/member/{memberId}")
    public ApiResponse<MemberEntity> sendMemberEntityByMemberId(@PathVariable Integer memberId){
        try {
            MemberEntity memberEntity = legacyInfoService.getMemberById(memberId);
            return new ApiResponse<>(ResponseCode.OK, memberEntity);
        } catch (MemberNotFoundException e) {
            return new ApiResponse<>(ResponseCode.NOT_FOUND, null);
        }
    }

}
