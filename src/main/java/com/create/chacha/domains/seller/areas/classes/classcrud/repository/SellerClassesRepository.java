package com.create.chacha.domains.seller.areas.classes.classcrud.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SellerClassesRepository extends CrudRepository<ClassInfoEntity, Long> {
	
	// 클래스 수정 데이터 조회
	Optional<ClassInfoEntity> findByIdAndStore_Url(Long id, String url);
	
	// 클래스 조회 store.storeUrl = ? (삭제/미삭제 모두)
    List<ClassInfoEntity> findAllByStore_Url(String url);
    
    // 클래스 논리적 삭제 구현: 스토어 URL과 ID 리스트로 소속된 클래스들 조회 (삭제/미삭제 모두 포함)
    List<ClassInfoEntity> findAllByIdInAndStore_Url(Collection<Long> ids, String url);
}