package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);

    // 비밀번호 변경, 넣기 전에 꼭 PasswordConfig의 PasswordEncoder를 통과시킬 것
    @Modifying
    @Query("UPDATE MemberEntity m set m.password = :password where m.id=:memberId")
    void updatePassword(@Param("memberId") Long memberId, @Param("password") String password);

    @Modifying
    @Query("update MemberEntity m set m.isDeleted = true where m.id = :memberId")
    void deleteMember(@Param("memberId") Long memberId);

    @Modifying
    @Query("update MemberEntity m set m.password = :password, m.phone = :phone where m.id = :memberId")
    void updateMemberInfo();
}
