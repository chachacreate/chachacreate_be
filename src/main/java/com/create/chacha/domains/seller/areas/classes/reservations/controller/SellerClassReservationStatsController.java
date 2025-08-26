package com.create.chacha.domains.seller.areas.classes.reservations.controller;

import com.create.chacha.domains.seller.areas.classes.reservations.dto.response.SellerClassReservationStatsResponseDTO;
import com.create.chacha.domains.seller.areas.classes.reservations.service.SellerClassReservationStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * 판매자 - 월별 예약 통계 API 
 *
 * - 연도: 현재 연도(KST) 고정
 * - 월(month): 미지정 시 당월·어제까지, 지정 시 해당 월 전체
 * - 그룹: time(=hour) | weekday
 *
 * 파라미터
 * - scope: store(기본) | class
 * - classId: scope=class 일 때 필수 (class_info.id)
 * - month: 1~12 (null이면 당월)
 * - groupBy: time | weekday  
 * - dimension: hour|weekday → groupBy 없을 때만 사용
 */
@Slf4j
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerClassReservationStatsController {

    private final SellerClassReservationStatsService statsService;

    @GetMapping(
    				  value = {"/{storeUrl}/classreservations", "/{storeUrl}/classreservations/stats"},
    				  produces = "application/json"
    				)

    public SellerClassReservationStatsResponseDTO getMonthlyStats(
            @PathVariable("storeUrl") String storeUrl,

            // 통계 범위: store(기본) | class
            @RequestParam(value = "scope", required = false, defaultValue = "store") String scope,

            // scope=class일 때 대상 클래스 ID (class_info.id)
            @RequestParam(value = "classId", required = false) Integer classId,

            // 조회 월(1~12). null이면 당월·어제까지 / 지정 시 해당 월 전체
            @RequestParam(value = "month", required = false) Integer month,

            // 신규 파라미터: time|weekday
            @RequestParam(value = "groupBy", required = false) String groupBy,

            // 레거시 파라미터: hour|weekday (groupBy 없을 때만 사용)
            @RequestParam(value = "dimension", required = false, defaultValue = "hour") String dimension
    ) {
        // groupBy 우선 → 없으면 dimension 사용
        String dim = normalizeGrouping(groupBy, dimension);

        if ("class".equalsIgnoreCase(scope)) {
            if (classId == null) {
                throw new IllegalArgumentException("scope=class 인 경우 classId는 필수입니다.");
            }
            return statsService.getMonthlyStatsForClass(storeUrl, month, dim, classId);
        }
        // 기본: 스토어 전체
        return statsService.getMonthlyStatsForStore(storeUrl, month, dim);
    }

    /** groupBy(신규) 우선, 없으면 dimension(레거시) 사용. 결과는 "hour" | "weekday" 로 표준화 */
    private String normalizeGrouping(String groupBy, String dimension) {
        if (groupBy != null && !groupBy.isBlank()) {
            String g = groupBy.toLowerCase(Locale.ROOT);
            if (g.equals("time") || g.equals("hour")) return "hour";
            if (g.equals("weekday") || g.equals("dayofweek") || g.equals("dow")) return "weekday";
            throw new IllegalArgumentException("groupBy는 time|weekday 만 허용합니다.");
        }
        // 레거시 dimension 처리
        String d = (dimension == null || dimension.isBlank())
                ? "hour"
                : dimension.toLowerCase(Locale.ROOT);
        if (d.equals("hour") || d.equals("time")) return "hour";
        if (d.equals("weekday")) return "weekday";
        throw new IllegalArgumentException("dimension은 hour|weekday 만 허용합니다.");
    }
}
