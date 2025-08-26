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
 * - 월 미지정 → 당월·어제까지, 월 지정 → 해당 월 전체
 * - 차원: hour(0~23), weekday(1=SUN~7=SAT)
 * - 상태: ORDER_OK만 집계
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
        // 1) 필수 파라미터 검증
        if (storeUrl == null || storeUrl.isBlank()) {
            throw new IllegalArgumentException("storeUrl은 필수입니다.");
        }

        // 2) 기준 시각: KST
        final ZoneId KST = ZoneId.of("Asia/Seoul");
        final ZonedDateTime now = ZonedDateTime.now(KST);

        // 3) 연도는 현재 연도로 고정, 월은 입력 없으면 현재 월
        final int y = now.getYear();
        final int m = (month == null ? now.getMonthValue() : month);
        if (m < 1 || m > 12) {
            throw new IllegalArgumentException("month는 1~12 범위여야 합니다.");
        }

        // 4) 차원 기본값/정규화
        final String dim = (dimension == null || dimension.isBlank())
                ? "hour"
                : dimension.toLowerCase(Locale.ROOT);

        // 5) 기간 계산: [start, end)
        //    - month 미지정(=당월) → 오늘 제외(어제 24:00 = 오늘 00:00 미만)
        //    - month 지정         → 해당 월 전체(다음 달 1일 00:00 미만)
        final LocalDate firstDay = LocalDate.of(y, m, 1);
        final LocalDateTime start = firstDay.atStartOfDay();
        final LocalDateTime nextMonthStart = firstDay.plusMonths(1).atStartOfDay();
        final boolean monthSelected = (month != null);
        final LocalDateTime todayZeroKST = now.toLocalDate().atStartOfDay();
        final LocalDateTime end = monthSelected ? nextMonthStart : min(nextMonthStart, todayZeroKST);

        // 6) 집계 실행 (스토어 전체: classInfoId=null)
        var rows = dim.equals("weekday")
                ? statsRepo.countByWeekdayForStore(storeUrl, start, end, null)
                : statsRepo.countByHourForStore(storeUrl, start, end, null);

        // 7) 버킷 채우기 + 합계
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

        // 8) 응답 
        return SellerClassReservationStatsResponseDTO.builder()
                .storeUrl(storeUrl)
                .year(y)           // 현재 연도 고정
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
