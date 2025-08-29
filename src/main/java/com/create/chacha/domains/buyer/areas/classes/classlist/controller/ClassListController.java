package com.create.chacha.domains.buyer.areas.classes.classlist.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
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
    public ApiResponse<ClassListResponseDTO> getClasses(
            @Valid @ModelAttribute ClassListFilterDTO filter,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        ClassListResponseDTO dto = classListService.getClassList(filter);

        if (dto == null || dto.getContent() == null || dto.getContent().isEmpty()) {
            return new ApiResponse<>(ResponseCode.CLASSES_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.CLASSES_FOUND, dto);
    }

    
    /**
     * 신규: 날짜 선택 시 예약 가능 클래스 조회 API
     * @param date 선택한 날짜 (yyyy-MM-dd)
     */
    @GetMapping("/classes/available")
    public ApiResponse<List<ClassCardVO>> getAvailableClassesByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ClassCardVO> result = classListService.getAvailableClassesByDate(date);

        if (result.isEmpty()) {
            return new ApiResponse<>(ResponseCode.CLASSES_AVAILABLE_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.CLASSES_AVAILABLE_FOUND, result);
    }

}
