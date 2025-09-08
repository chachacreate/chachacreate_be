package com.create.chacha.common.util.legacy.service;

import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.repository.MemberAddressRepository;
import com.create.chacha.domains.shared.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LegacyInfoServiceImpl implements LegacyInfoService {
    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;

    @Override
    public MemberEntity getMemberById(Integer memberId) {
        return memberRepository.findById(memberId.longValue()).orElse(null);
    }

    @Override
    public MemberEntity getSellerInfo(Integer memberId) {
        MemberEntity responseMember = new MemberEntity();
        memberRepository.findById(Long.valueOf(memberId)).ifPresent(member -> {
            responseMember.setName(member.getName());
            responseMember.setEmail(member.getEmail());
            responseMember.setPhone(member.getPhone());
        });
        return responseMember;
    }

    @Override
    public MemberAddressEntity getMemberAddress(Integer memberId) {
        MemberAddressEntity responseMemberAddress = new MemberAddressEntity();
        memberAddressRepository.findFirstByMember_IdAndIsDefaultOrderByIdAsc(Long.valueOf(memberId), true).ifPresent(addr -> {
            responseMemberAddress.setId(addr.getId());
            responseMemberAddress.setPostNum(addr.getPostNum());
            responseMemberAddress.setAddressRoad(addr.getAddressRoad());
            responseMemberAddress.setAddressDetail(addr.getAddressDetail());
            responseMemberAddress.setAddressExtra(addr.getAddressExtra());
        });
        return responseMemberAddress;
    }

    @Override
    public MemberAddressEntity setMemberAddress(Integer memberId, MemberAddressEntity addr) {
        MemberEntity member = memberRepository.findById(memberId.longValue())
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));
        addr.setMember(member);

        // NOT NULL 필드 기본값 보장
        if (addr.getIsDeleted() == null) {
            addr.setIsDeleted(false);
        }
        if (addr.getIsDefault() == null) {
            addr.setIsDefault(false);
        }

        MemberAddressEntity savedAddress = memberAddressRepository.save(addr);

        MemberAddressEntity response = new MemberAddressEntity();
        response.setId(savedAddress.getId());
        response.setPostNum(savedAddress.getPostNum());
        response.setAddressRoad(savedAddress.getAddressRoad());
        response.setAddressDetail(savedAddress.getAddressDetail());
        response.setAddressExtra(savedAddress.getAddressExtra());
        response.setIsDefault(savedAddress.getIsDefault());

        return response;
    }


    @Override
    public MemberAddressEntity getMemberAddressByAddressId(Integer addressId) {
        MemberAddressEntity responseMemberAddress = new MemberAddressEntity();
        memberAddressRepository.findById(Long.valueOf(addressId)).ifPresent(addr -> {
            responseMemberAddress.setId(addr.getId());
            responseMemberAddress.setPostNum(addr.getPostNum());
            responseMemberAddress.setAddressRoad(addr.getAddressRoad());
            responseMemberAddress.setAddressDetail(addr.getAddressDetail());
            responseMemberAddress.setAddressExtra(addr.getAddressExtra());
        });
        return responseMemberAddress;
    }

    @Override
    public MemberEntity getMemberByEmail(String memberEmail) {
        MemberEntity responseMember = new MemberEntity();
        memberRepository.findByEmail(memberEmail).ifPresent(member -> {
            responseMember.setName(member.getName());
            responseMember.setEmail(member.getEmail());
            responseMember.setPhone(member.getPhone());
            responseMember.setId(member.getId());
            responseMember.setMemberRole(member.getMemberRole());
            responseMember.setRegistrationNumber(member.getRegistrationNumber());
            responseMember.setCreatedAt(member.getCreatedAt());
            responseMember.setUpdatedAt(member.getUpdatedAt());
            responseMember.setDeletedAt(member.getDeletedAt());
            responseMember.setIsDeleted(member.getIsDeleted());
        });
        return responseMember;
    }
}
