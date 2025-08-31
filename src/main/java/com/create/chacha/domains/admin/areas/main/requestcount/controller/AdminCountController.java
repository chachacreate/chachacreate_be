package com.create.chacha.domains.admin.areas.main.requestcount.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.admin.areas.main.requestcount.dto.response.StoreCountResponseDTO;
import com.create.chacha.domains.admin.areas.main.requestcount.service.AdminMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 메인 페이지 - 요청 건수 조회 API
 * - 팀 규칙: 컨트롤러는 @PathVariable 위주 사용
 * - 예시:
 *   - GET /api/admin/stores/new 			신규 스토어 개설요청 건수
 *   - GET /api/admin/stores/pending		미처리된 스토어 개설요청 건수
 *   - GET /api/admin/resumes/new			신규 이력서 접수 건수
 *   - GET /api/admin/resumes/pending	미처리된 이력서 접수 건수
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCountController {

    private final AdminMainService adminMainService;

    /**
     * 스토어 개설 요청 건수 조회
     * GET /api/admin/stores/{metric}
     */
    @GetMapping("/stores/{metric}")
    public ApiResponse<StoreCountResponseDTO> getStoreCounts(@PathVariable("metric") String metric) {
        log.info("[GET] /api/admin/stores/{}", metric);

        StoreCountResponseDTO result = adminMainService.getStoreCounts(metric);
        if (result == null) {
            return new ApiResponse<>(ResponseCode.ADMIN_STORE_COUNT_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.ADMIN_STORE_COUNT_FOUND, result);
    }

    /**
     * 이력서 신청 건수 조회
     * GET /api/admin/resumes/{metric}
     */
    @GetMapping("/resumes/{metric}")
    public ApiResponse<StoreCountResponseDTO> getResumeCounts(@PathVariable("metric") String metric) {
        log.info("[GET] /api/admin/resumes/{}", metric);

        StoreCountResponseDTO result = adminMainService.getResumeCounts(metric);
        if (result == null) {
            return new ApiResponse<>(ResponseCode.ADMIN_RESUME_COUNT_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.ADMIN_RESUME_COUNT_FOUND, result);
    }
}
