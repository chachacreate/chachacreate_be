package com.create.chacha.domains.seller.areas.classes.classcrud.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SellerClassesRepository extends CrudRepository<ClassInfoEntity, Long> {
	
	// 클래스 수정 데이터 조회
	@Query(value = """
	        select *
	          from class_info
	         where id = :classId
	           and store_id = :storeId
	        """, nativeQuery = true)
	    Optional<ClassInfoEntity> findByIdAndStore_Url(@Param("classId") Long classId,
	                                                   @Param("storeId") Long storeId);
	
	// 클래스 조회 store.storeUrl = ? (삭제/미삭제 모두, 최신순)
	@Query(value = """
	        select *
	          from class_info
	         where store_id = :storeId
	         order by updated_at desc
	        """, nativeQuery = true)
	List<ClassInfoEntity> findAllByStore_Url(@Param("storeId") Long storeId);
    
    // 클래스 논리적 삭제 구현: 스토어 URL과 ID 리스트로 소속된 클래스들 조회 (삭제/미삭제 모두 포함)
	@Query(value = """
	        select *
	          from class_info
	         where store_id = :storeId
	           and id in (:ids)
	        """, nativeQuery = true)
	    List<ClassInfoEntity> findAllByIdInAndStore_Url(@Param("ids") Collection<Long> ids,
	                                                    @Param("storeId") Long storeId);
}