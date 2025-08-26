package com.create.chacha.domains.seller.areas.classes.classcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassImageEntity;

public interface ClassImageRepository extends CrudRepository<ClassImageEntity, Long>{
	
	List<ClassImageEntity> findByClassInfo_Id(Long classId);
	
	Optional<ClassImageEntity> findFirstByClassInfo_IdAndStatusAndImageSequenceAndIsDeletedFalseOrderByIdAsc(
            Long classInfoId, ImageStatusEnum status, Integer imageSequence
    );
}