package com.create.chacha.domains.buyer.areas.classes.classlist.service;

import com.create.chacha.domains.buyer.areas.classes.classlist.dto.request.ClassListFilterDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.response.ClassListResponseDTO;
import com.create.chacha.domains.shared.classes.vo.ClassCardVO;

import java.time.LocalDate;
import java.util.List;

public interface ClassListService {

    /**
     * 클래스 목록 조회 (메인홈/스토어 구분 + 검색/정렬/페이지네이션)
     */
    ClassListResponseDTO getClassList(ClassListFilterDTO f);

    /**
     * 예약 가능 클래스 조회
     * @param storeUrl "main" → 전체, 특정 값 → 해당 스토어
     * @param date 조회 기준 날짜
     */
    List<ClassCardVO> getAvailableClassesByDate(String storeUrl, LocalDate date);
}
