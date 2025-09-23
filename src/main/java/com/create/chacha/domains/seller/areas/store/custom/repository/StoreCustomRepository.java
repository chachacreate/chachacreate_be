package com.create.chacha.domains.seller.areas.store.custom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.create.chacha.domains.shared.entity.store.StoreCustomEntity;

public interface StoreCustomRepository extends JpaRepository<StoreCustomEntity, Long> {
	
    boolean existsByStoreId(Long storeId);
}
