package com.create.chacha.domains.shared.member.serviceimpl;

import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.member.service.LegacyInfoService;
import com.create.chacha.domains.shared.repository.MemberAddressRepository;
import com.create.chacha.domains.shared.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LegacyInfoServiceImpl implements LegacyInfoService {
    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;

    @Override
    public MemberEntity getSellerInfo(Integer memberId){
        MemberEntity responseMember = new MemberEntity();
        memberRepository.findById(Long.valueOf(memberId)).ifPresent(member -> {
            responseMember.setName(member.getName());
            responseMember.setEmail(member.getEmail());
            responseMember.setPhone(member.getPhone());
        });
        return responseMember;
    }

    @Override
    public MemberAddressEntity getMemberAddress(Integer memberId){
        MemberAddressEntity responseMemberAddress = new MemberAddressEntity();
        memberAddressRepository.findFirstByMember_IdAndIsDefaultOrderByIdAsc(Long.valueOf(memberId), true).ifPresent(addr -> {
            responseMemberAddress.setPostNum(addr.getPostNum());
            responseMemberAddress.setAddressRoad(addr.getAddressRoad());
            responseMemberAddress.setAddressDetail(addr.getAddressDetail());
            responseMemberAddress.setAddressExtra(addr.getAddressExtra());
        });
        return responseMemberAddress;
    }
}
