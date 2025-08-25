package com.create.chacha.domains.buyer.areas.classes.classlist.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.buyer.areas.classes.classlist.dto.request.ClassListFilterDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.response.ClassListResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.service.ClassListService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
}
