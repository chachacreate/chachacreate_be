package com.create.chacha.domains.admin.areas.main.requestcount.service;

import com.create.chacha.domains.admin.areas.main.requestcount.dto.response.StoreCountResponseDTO;

/**
 * 관리자 메인 페이지 - 스토어 개설 요청 건수 Service
 */
public interface AdminMainService {

    /**
     * 스토어 개설 요청 건수 조회
     * @param metric new(신규), pending(미승인)
     * @return StoreCountResponseDTO
     */
    StoreCountResponseDTO getStoreCounts(String metric);
    
    /**
     * metric(new, pending)에 따른 이력서 건수 조회
     */
    StoreCountResponseDTO getResumeCounts(String metric);
}
