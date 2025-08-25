package com.create.chacha.domains.seller.areas.classes.classinsert.repository;

import com.create.chacha.domains.shared.entity.store.StoreEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
public interface StoreRepository extends CrudRepository<StoreEntity, Long>{
    Optional<StoreEntity> findByUrl(String url);
}
