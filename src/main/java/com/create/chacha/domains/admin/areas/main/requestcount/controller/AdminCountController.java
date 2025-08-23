package com.create.chacha.domains.admin.areas.main.requestcount.controller;

import com.create.chacha.domains.admin.areas.main.requestcount.dto.response.StoreCountResponseDTO;
import com.create.chacha.domains.admin.areas.main.requestcount.service.AdminMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<StoreCountResponseDTO> getStoreCounts(@RequestParam(name = "metric") String metric) {
        log.info("스토어 개설 요청 건수 조회 API 호출, metric={}", metric);
        StoreCountResponseDTO response = adminMainService.getStoreCounts(metric);
        return ResponseEntity.ok(response);
    }

}
