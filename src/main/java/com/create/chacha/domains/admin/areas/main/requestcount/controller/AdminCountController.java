package com.create.chacha.domains.admin.areas.main.requestcount.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.admin.areas.main.requestcount.dto.response.StoreCountResponseDTO;
import com.create.chacha.domains.admin.areas.main.requestcount.service.AdminMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 메인 페이지 - 스토어 개설 요청 건수 조회 API
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCountController {

    private final AdminMainService adminMainService;

    /**
     * 스토어 개설 요청 건수 조회
     * @param metric new(신규), pending(미승인)
     * @return ResponseEntity<StoreCountResponseDTO>
     */
    @GetMapping("/stores")
    public ApiResponse<StoreCountResponseDTO> getStoreCounts(@RequestParam String metric) {
        StoreCountResponseDTO result = adminMainService.getStoreCounts(metric);
        if (result == null) {
            return new ApiResponse<>(ResponseCode.ADMIN_STORE_COUNT_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.ADMIN_STORE_COUNT_FOUND, result);
    }
    
    /**
     * 이력서 신청 건수 조회
     * @param metric new(신규), pending(미승인)
     * @return ResponseEntity<ResumeCountResponseDTO>
     */
    @GetMapping("/resumes")
    public ApiResponse<StoreCountResponseDTO> getResumeCounts(@RequestParam String metric) {
        StoreCountResponseDTO result = adminMainService.getResumeCounts(metric);
        if (result == null) {
            return new ApiResponse<>(ResponseCode.ADMIN_RESUME_COUNT_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.ADMIN_RESUME_COUNT_FOUND, result);
    }

}
