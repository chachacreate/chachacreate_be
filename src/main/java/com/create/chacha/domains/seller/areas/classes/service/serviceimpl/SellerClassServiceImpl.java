package com.create.chacha.domains.seller.areas.classes.service.serviceimpl;

import java.util.List;

import com.create.chacha.domains.seller.areas.classes.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.dto.response.ClassListItemResponseDTO;

public interface SellerClassServiceImpl {
	
	// 클래스 조회 (삭제/미삭제 모두 포함한 전체 목록 (스토어별))
    List<ClassListItemResponseDTO> getClassesByStoreUrl(String storeUrl);
    
	// 클래스 등록
	List<Long> createClasses(String storeUrl, List<ClassCreateRequestDTO> requests);
}
