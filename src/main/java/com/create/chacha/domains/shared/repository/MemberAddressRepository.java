package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddressEntity, Long> {
    // 기본 주소 1개 조회
    Optional<MemberAddressEntity> findFirstByMember_IdAndIsDefaultOrderByIdAsc(Long memberId, Boolean isDefault);
    // 주소 변경
    @Modifying
    @Query("UPDATE MemberAddressEntity m SET " +
            "m.postNum = :postNum, " +
            "m.addressRoad = :addressRoad, " +
            "m.addressDetail = :addressDetail, " +
            "m.addressExtra = :addressExtra " +
            "WHERE m.member.id = :memberId AND m.isDefault = true")
    int updateDefaultAddress(@Param("memberId") Long memberId,
                             @Param("postNum") String postNum,
                             @Param("addressRoad") String addressRoad,
                             @Param("addressDetail") String addressDetail,
                             @Param("addressExtra") String addressExtra);
    // 주소 삭제
    @Modifying
    @Query("update MemberAddressEntity m set m.isDeleted = false WHERE m.id = :addressId")
    int deleteByAddressId(@Param("addressId") Long addressId);
}
