package com.create.chacha.domains.seller.areas.classes.classcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassImageEntity;

public interface ClassImageRepository extends CrudRepository<ClassImageEntity, Long>{
	
	@Modifying
	@Query("""
	update ClassImageEntity i
	   set i.isDeleted = true, i.deletedAt = CURRENT_TIMESTAMP
	 where i.classInfo.id = :classId
	   and i.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL
	   and (i.imageSequence is null or i.imageSequence <> :keepSeq)
	   and i.isDeleted = false
	""")
	int markThumbnailOthersDeleted(@Param("classId") Long classId, @Param("keepSeq") int keepSeq);

	
	/** 클래스 수정 위한 코드 **/
	// 특정 seq의 썸네일 1건 (삭제여부 무관)
    Optional<ClassImageEntity> findByClassInfo_IdAndStatusAndImageSequence(
            Long classInfoId, ImageStatusEnum status, Integer imageSequence);

    // 활성(미삭제) DESCRIPTION 전체
    List<ClassImageEntity> findAllByClassInfo_IdAndStatusAndIsDeletedFalse(
            Long classInfoId, ImageStatusEnum status);

    // DESCRIPTION 시퀀스 최대값(삭제여부 무관)
    Optional<ClassImageEntity> findTopByClassInfo_IdAndStatusOrderByImageSequenceDesc(
            Long classInfoId, ImageStatusEnum status);
	
	// 판매자에서 수정 데이터 조회할 때 사용 (class id로 조회, 미삭제만 수정 조회 가능)
	List<ClassImageEntity> findAllByClassInfo_IdAndIsDeletedFalse(Long classInfoId);
	
	// 구매자에서 클래스 상세 보기 때 사용
	List<ClassImageEntity> findByClassInfo_Id(Long classId);
	
	// 판매자에서 클래스 리스트 조회 때 사용
	Optional<ClassImageEntity> findFirstByClassInfo_IdAndStatusAndImageSequenceAndIsDeletedFalseOrderByIdAsc(
            Long classInfoId, ImageStatusEnum status, Integer imageSequence
    );
	
	@Query("""
	        select img
	        from ClassImageEntity img
	        where img.classInfo.id = :classId
	          and (
	                (img.status = :thumb and img.imageSequence = 1)
	             or (img.status = :detail)
	          )
	        order by
	          case when (img.status = :thumb and img.imageSequence = 1) then 0 else 1 end,
	          img.imageSequence asc,
	          img.id asc
	    """)
	    List<ClassImageEntity> findImagesForDetail(
	            @Param("classId") Long classId,
	            @Param("thumb") com.create.chacha.domains.shared.constants.ImageStatusEnum thumb,
	            @Param("detail") com.create.chacha.domains.shared.constants.ImageStatusEnum detail
	    );
}