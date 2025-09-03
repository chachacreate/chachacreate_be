package com.create.chacha.domains.shared.member.service;

import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;

public interface LegacyInfoService {
    public MemberEntity getSellerInfo(Integer memberId);
    public MemberAddressEntity getMemberAddress(Integer memberId);
    public MemberAddressEntity getMemberAddressByAddressId(Integer addressId);
}
