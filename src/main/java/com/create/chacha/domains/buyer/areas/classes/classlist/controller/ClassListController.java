package com.create.chacha.domains.buyer.areas.classes.classlist.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.request.ClassListFilterDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.response.ClassListResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.service.ClassListService;
import com.create.chacha.domains.shared.classes.vo.ClassCardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{storeUrl}/classes")
public class ClassListController {

    private final ClassListService service;

    /**
     * 클래스 목록 조회 API
     * - 메인홈: /api/main/classes?page=0&size=20&sort=latest&keyword=자수
     * - 스토어: /api/{storeUrl}/classes?page=0&size=20&sort=latest&keyword=자수
     */
    @GetMapping
    public ApiResponse<ClassListResponseDTO> getClasses(
            @PathVariable("storeUrl") String storeUrl,
            @ModelAttribute ClassListFilterDTO filter
    ) {
        filter.setStoreUrl(storeUrl);
        log.info("클래스 목록 조회 API 호출 - storeUrl={}, filter={}", storeUrl, filter);
        ClassListResponseDTO response = service.getClassList(filter);
        if(response == null || response.getContent().isEmpty()) {
        	return new ApiResponse<>(ResponseCode.CLASSES_NOT_FOUND, response);
        }
        return new ApiResponse<>(ResponseCode.CLASSES_FOUND, response);
    }

    /**
     * 예약 가능 클래스 조회 API
     * - 메인홈: /api/main/classes/available?date=2025-09-10
     * - 스토어: /api/{storeUrl}/classes/available?date=2025-09-10
     */
    @GetMapping("/available")
    public ApiResponse<List<ClassCardVO>> getAvailableClasses(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("예약 가능 클래스 조회 API 호출 - storeUrl={}, date={}", storeUrl, date);
        List<ClassCardVO> available = service.getAvailableClassesByDate(storeUrl, date);
        if(available == null || available.isEmpty()) {
        	 	return new ApiResponse<>(ResponseCode.CLASSES_AVAILABLE_NOT_FOUND, available);
        }
        return new ApiResponse<>(ResponseCode.CLASSES_AVAILABLE_FOUND, available);
    }
}
