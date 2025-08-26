package com.create.chacha.domains.buyer.areas.classes.classlist.service;

import java.time.LocalDate;
import java.util.List;

import com.create.chacha.domains.buyer.areas.classes.classlist.dto.request.ClassListFilterDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.response.ClassListResponseDTO;
import com.create.chacha.domains.shared.classes.vo.ClassCardVO;

/**
 * 클래스 목록 조회 유스케이스 정의 (비즈니스 경계)
 */
public interface ClassListService {

    /**
     * 전체 클래스 목록 조회
     * 조건조회 결과 반환
     *
     * @param filter  페이지/검색 조건 (page, size, keyword 등)
     * @param sort    정렬 키 (예: "createdAt,desc")
     * @return        페이지 메타 + 목록 카드 VO 리스트
     */
    ClassListResponseDTO getClassList(ClassListFilterDTO filter);

    /**
     * 날짜 기준 예약 가능 클래스 조회
     */
	List<ClassCardVO> getAvailableClassesByDate(LocalDate date);
    
}
