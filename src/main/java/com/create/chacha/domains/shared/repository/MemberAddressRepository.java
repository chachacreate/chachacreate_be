package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.shared.entity.member.MemberAddressEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAddressRepository extends JpaRepository<MemberAddressEntity, Long> {
}
