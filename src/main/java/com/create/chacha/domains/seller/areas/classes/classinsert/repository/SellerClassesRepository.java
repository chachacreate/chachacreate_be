package com.create.chacha.domains.seller.areas.classes.classinsert.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import org.springframework.data.repository.CrudRepository;

public interface SellerClassesRepository extends CrudRepository<ClassInfoEntity, Long> {
	
}