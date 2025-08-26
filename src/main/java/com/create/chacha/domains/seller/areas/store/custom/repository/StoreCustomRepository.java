package com.create.chacha.domains.seller.areas.store.custom.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.create.chacha.domains.shared.entity.store.StoreCustomEntity;

public interface StoreCustomRepository extends CrudRepository<StoreCustomEntity, Long> {
	
	// store.url 로 조인해서 한 건 조회 (스토어당 1개)
    Optional<StoreCustomEntity> findByStore_Url(String url);
}
