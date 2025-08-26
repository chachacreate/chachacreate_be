package com.create.chacha.domains.seller.areas.classes.reservations.service.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.seller.areas.classes.reservations.dto.response.SellerClassReservationStatsItemDTO;
import com.create.chacha.domains.seller.areas.classes.reservations.dto.response.SellerClassReservationStatsResponseDTO;
import com.create.chacha.domains.seller.areas.classes.reservations.service.SellerClassReservationStatsService;
import com.create.chacha.domains.shared.repository.ClassReservationStatsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 판매자 페이지 - 월별 시간/요일별 클래스 예약 건수 통계 서비스 구현체
 *
 * - 연도는 현재 KST 연도로 고정
 * - month 미지정 → 당월·어제까지, month 지정 → 해당 월 전체
 * - dimension: "hour"(0~23) | "weekday"(1=SUN~7=SAT)
 * - 상태: ORDER_OK만 집계
 * - 재사용: classInfoId=null(스토어 전체) | classInfoId=값(특정 클래스)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SellerClassReservationStatsServiceImpl implements SellerClassReservationStatsService {

    private final ClassReservationStatsRepository statsRepo;

    @Override
    public SellerClassReservationStatsResponseDTO getMonthlyStatsForStore(
            String storeUrl, Integer month, String dimension
    ) {
        return getMonthlyStatsInternal(storeUrl, month, dimension, null);
    }

    @Override
    public SellerClassReservationStatsResponseDTO getMonthlyStatsForClass(
            String storeUrl, Integer month, String dimension, Integer classInfoId
    ) {
        if (classInfoId == null || classInfoId <= 0) {
            throw new IllegalArgumentException("scope=class 인 경우 classId는 양수여야 합니다.");
        }
        return getMonthlyStatsInternal(storeUrl, month, dimension, classInfoId);
    }

    /** 공통 내부 구현: classInfoId 유무로 스토어/클래스 범위 분기 */
    private SellerClassReservationStatsResponseDTO getMonthlyStatsInternal(
            String storeUrl, Integer month, String dimension, Integer classInfoId
    ) {
        // 1) 필수값 검증
        if (storeUrl == null || storeUrl.isBlank()) {
            throw new IllegalArgumentException("storeUrl은 필수입니다.");
        }

        // 2) 기준 시각/연월/차원 정규화
        final ZoneId KST = ZoneId.of("Asia/Seoul");
        final ZonedDateTime now = ZonedDateTime.now(KST);

        final int y = now.getYear();  // 연도는 고정
        final int m = (month == null ? now.getMonthValue() : month);
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("month는 1~12 범위여야 합니다.");
        }

        final String dim = (dimension == null || dimension.isBlank())
                ? "hour"
                : dimension.toLowerCase(Locale.ROOT);

        // 3) 기간 계산: [start, end)
        final LocalDate firstDay = LocalDate.of(y, m, 1);
        final LocalDateTime start = firstDay.atStartOfDay();
        final LocalDateTime nextMonthStart = firstDay.plusMonths(1).atStartOfDay();
        final boolean monthSelected = (month != null);
        final LocalDateTime todayZeroKST = now.toLocalDate().atStartOfDay();
        final LocalDateTime end = monthSelected ? nextMonthStart : min(nextMonthStart, todayZeroKST);

        // 4) 집계 실행 (classInfoId: null=스토어 전체, 값=특정 클래스)
        var rows = dim.equals("weekday")
                ? statsRepo.countByWeekdayForStore(storeUrl, start, end, classInfoId)
                : statsRepo.countByHourForStore(storeUrl, start, end, classInfoId);

        // 5) 버킷 채우기 + 합계
        List<SellerClassReservationStatsItemDTO> items = new ArrayList<>();
        long total = 0;

        if ("weekday".equals(dim)) {
            long[] counts = new long[8]; // 1~7
            rows.forEach(r -> {
                Integer b = r.getBucket();
                if (b != null && b >= 1 && b <= 7) counts[b] = r.getCnt();
            });
            String[] labels = {"","SUN","MON","TUE","WED","THU","FRI","SAT"};
            for (int d = 1; d <= 7; d++) {
                long c = counts[d];
                items.add(new SellerClassReservationStatsItemDTO(d, labels[d], c));
                total += c;
            }
        } else {
            long[] counts = new long[24]; // 0~23
            rows.forEach(r -> {
                Integer b = r.getBucket();
                if (b != null && b >= 0 && b <= 23) counts[b] = r.getCnt();
            });
            for (int h = 0; h < 24; h++) {
                String label = String.format("%02d:00", h);
                long c = counts[h];
                items.add(new SellerClassReservationStatsItemDTO(h, label, c));
                total += c;
            }
        }

        // 6) 응답 조립 (class 범위일 때 classId 포함)
        return SellerClassReservationStatsResponseDTO.builder()
                .storeUrl(storeUrl)
                .classId(classInfoId)
                .year(y)
                .month(m)
                .dimension(dim)
                .rangeStart(start)
                .rangeEnd(end)
                .items(items)
                .total(total)
                .build();
    }

    /** 두 시각 중 더 이른 시각 */
    private static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) ? a : b;
    }
}
