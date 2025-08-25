package com.create.chacha.domains.buyer.areas.classes.classdetail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;

public interface ClassInfoRepository extends JpaRepository<ClassInfoEntity, Long> {
    // store/seller/member까지 필요하면 EntityGraph로 기본 fetch 최적화
	@EntityGraph(attributePaths = {"store"})
	@Query("select c from ClassInfoEntity c where c.id = :id")
	Optional<ClassInfoEntity> findByclassId(@Param("id") Long id);
}
