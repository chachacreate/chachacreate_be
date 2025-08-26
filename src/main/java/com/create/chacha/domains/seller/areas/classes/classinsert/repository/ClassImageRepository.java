package com.create.chacha.domains.seller.areas.classes.classinsert.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.create.chacha.domains.shared.entity.classcore.ClassImageEntity;

public interface ClassImageRepository extends CrudRepository<ClassImageEntity, Long>{
	List<ClassImageEntity> findByClassInfo_Id(Long classId);
}
