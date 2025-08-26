package com.create.chacha.domains.buyer.areas.classes.classlist.controller;

import com.create.chacha.domains.buyer.areas.classes.classlist.dto.request.ClassListFilterDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.response.ClassListResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.service.ClassListService;
import com.create.chacha.domains.shared.classes.vo.ClassCardVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main") 
@Validated
public class ClassListController {

    private final ClassListService classListService;

    /**
     * 전체 클래스 목록 조회
     *  클래스 조건조회 API
     *  날짜기준 조회 API
     *  최신순, 마감임박순(end_date + end_time), 낮은 가격순, 높은 가격순, 클래스명 검색
     */
    @GetMapping("/classes")
    public ClassListResponseDTO getClasses(
            @Valid @ModelAttribute ClassListFilterDTO filter,         
            @RequestParam(name = "sort", required = false) String sort 
    ) {
    		if(filter.getSort() != null || (filter.getKeyword() != null && !filter.getKeyword().isBlank())) {
    			log.info("클래스 조건조회 API 호출 - sort={}, keyword={}, page={}, size={}", filter.getSort(), filter.getKeyword(), filter.getPage(), filter.getSize());
    		} else {
    			log.info("클래스 전체조회 API 호출 - page={}, size={}", filter.getPage(), filter.getSize());
    		}
        return classListService.getClassList(filter);
    }
    
    /**
     * 신규: 날짜 선택 시 예약 가능 클래스 조회 API
     * @param date 선택한 날짜 (yyyy-MM-dd)
     */
    @GetMapping("/classes/available")
    public List<ClassCardVO> getAvailableClassesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("날짜 선택 예약가능 클래스 조회 API 호출: date={}", date);
        return classListService.getAvailableClassesByDate(date);
    }

}
