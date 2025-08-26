package com.create.chacha.domains.seller.areas.classes.reservationlist.service.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response.ClassReservationMonthlyResponseDTO;
import com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response.ClassReservationRowDTO;
import com.create.chacha.domains.seller.areas.classes.reservationlist.service.ClassReservationService;
import com.create.chacha.domains.shared.repository.ClassReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 클래스 예약 조회 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassReservationServiceImpl implements ClassReservationService {

    private final ClassReservationRepository reservationRepository;

    /** 프론트 요구 포맷: 날짜(yyyy-MM-dd) */
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** 프론트 요구 포맷: 시간(HH:mm) */
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 특정 스토어의 클래스 예약 목록을 조회한다.
     *
     * @param storeUrl        스토어 URL (고유 식별자)
     * @param yearMonthOrNull yyyy-MM 또는 null(전체 조회)
     * @return 월간/전체 조회 응답 DTO
     */
    @Override
    public ClassReservationMonthlyResponseDTO getReservations(String storeUrl, String yearMonthOrNull) {
        // 1) yearMonth 파싱(yyyy-MM) — 없으면 전체 조회
        LocalDateTime start = null;
        LocalDateTime end = null;
        String normalizedYm = null;

        if (yearMonthOrNull != null && !yearMonthOrNull.isBlank()) {
            final String ymStr = yearMonthOrNull.trim();
            try {
                YearMonth ym = YearMonth.parse(ymStr);   // ISO yyyy-MM
                normalizedYm = ym.toString();            // 항상 yyyy-MM 형태로 보정
                start = ym.atDay(1).atStartOfDay();      // yyyy-MM-01 00:00:00
                end = start.plusMonths(1);               // 다음 달 1일 00:00:00
            } catch (Exception e) {
                throw new IllegalArgumentException("yearMonth는 yyyy-MM 형식이어야 합니다. 예) 2025-08");
            }
        }

        // 2) 조회 (항상 reservedTime DESC)
        List<ClassReservationRepository.RowProjection> rows =
                reservationRepository.findRowsByStoreUrlWithOptionalMonth(storeUrl, start, end);

        // 3) DTO 변환
        List<ClassReservationRowDTO> items = rows.stream().map(p -> {
            // 날짜/시간 문자열 분리 (DB 종속 함수 사용 X)
            String reservedDate = DATE_FMT.format(p.getReservedTime().toLocalDate());
            String reservedTime = TIME_FMT.format(p.getReservedTime().toLocalTime());

            // 최근 수정일 → UTC Instant (서버 TZ가 UTC라면 직접 toInstant(ZoneOffset.UTC) 사용 가능)
            Instant updatedAt = p.getUpdatedTime()
                                 .atZone(ZoneId.systemDefault())
                                 .toInstant();

            return ClassReservationRowDTO.builder()
                    .reservedDate(reservedDate)
                    .className(p.getClassTitle())
                    .reservedTime(reservedTime)
                    .reserverName(p.getMemberName())
                    .reserverPhone(p.getMemberPhone())
                    .paymentAmount(p.getPrice())
                    .status(p.getStatus()) // Enum 그대로 직렬화됨(문자열)
                    .updatedAt(updatedAt)
                    .build();
        }).toList();

        // 4) 컨테이너 응답 조립
        return ClassReservationMonthlyResponseDTO.builder()
                .storeUrl(storeUrl)
                .yearMonth(normalizedYm) // null = 전체 조회 의미
                .items(items)
                .build();
    }
}
