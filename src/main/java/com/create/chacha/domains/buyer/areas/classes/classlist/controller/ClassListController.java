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
     * 전체 클래스 목록 조회 (t1~t5만 응답: 제목, 썸네일, 스토어명, 상세위치, 가격)
     */
    @GetMapping("/classes")
    public ClassListResponseDTO getClasses(
            @Valid @ModelAttribute ClassListFilterDTO filter,         
            @RequestParam(name = "sort", required = false) String sort 
    ) {
        return classListService.getClassList(filter, sort);
    }
}
