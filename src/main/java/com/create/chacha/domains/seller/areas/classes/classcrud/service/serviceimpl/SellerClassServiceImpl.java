package com.create.chacha.domains.seller.areas.classes.classcrud.service.serviceimpl;

import java.util.List;

import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateResponseDTO;

public interface SellerClassServiceImpl {
	
	// 클래스 수정 기능
	ClassUpdateResponseDTO updateClass(String storeUrl, Long classId, ClassCreateRequestDTO request);
	
	// 클래스 수정을 위한 데이터 조회
	ClassCreateRequestDTO getClassForUpdate(String storeUrl, Long classId);
	
	// 클래스 논리적 삭제 update
	 ClassDeletionToggleResponseDTO toggleClassesDeletion(String storeUrl, List<Long> classIds);
	
	// 클래스 조회 (삭제/미삭제 모두 포함한 전체 목록 (스토어별))
    List<ClassListItemResponseDTO> getClassesByStoreUrl(String storeUrl);
    
	// 클래스 등록
	List<Long> createClasses(String storeUrl, List<ClassCreateRequestDTO> requests);
}