package com.create.chacha.domains.seller.areas.classes.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SellerClassesRepository extends CrudRepository<ClassInfoEntity, Long> {
	
	// store.storeUrl = ? (삭제/미삭제 모두)
    List<ClassInfoEntity> findAllByStore_Url(String url);
}