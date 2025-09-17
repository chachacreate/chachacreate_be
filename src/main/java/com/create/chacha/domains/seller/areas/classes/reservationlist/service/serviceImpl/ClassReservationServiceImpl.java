package com.create.chacha.domains.seller.areas.classes.reservationlist.service.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response.ClassReservationMonthlyResponseDTO;
import com.create.chacha.domains.seller.areas.classes.reservationlist.dto.response.ClassReservationRowDTO;
import com.create.chacha.domains.seller.areas.classes.reservationlist.service.ClassReservationService;
import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.repository.ClassReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassReservationServiceImpl implements ClassReservationService {

    private final ClassReservationRepository reservationRepository;
    private final LegacyAPIUtil legacyAPIUtil;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public ClassReservationMonthlyResponseDTO getReservations(String storeUrl, String yearMonthOrNull) {
        LocalDateTime start = null, end = null;
        String normalizedYm = null;

        if (yearMonthOrNull != null && !yearMonthOrNull.isBlank()) {
            YearMonth ym = YearMonth.parse(yearMonthOrNull.trim());
            normalizedYm = ym.toString();
            start = ym.atDay(1).atStartOfDay();
            end   = start.plusMonths(1);
        }

        // 🔎 legacy에서 storeId만 가져오고, DB 조인은 하지 않습니다.
        var legacyStore = legacyAPIUtil.getLegacyStoreData(storeUrl);
        Long storeId = (legacyStore != null && legacyStore.getStoreId() != null)
                ? legacyStore.getStoreId().longValue() : null;

        if (storeId == null) {
            log.warn("Legacy storeId not found for storeUrl={}", storeUrl);
            return ClassReservationMonthlyResponseDTO.builder()
                    .storeUrl(storeUrl).yearMonth(normalizedYm).items(List.of()).build();
        }

        // ✅ JPQL로 조회 → MemberEntity의 @Convert가 적용되어 name/phone이 평문으로 반환됩니다.
        var rows = reservationRepository.findRowsByStoreIdWithOptionalMonth(storeId, start, end);

        List<ClassReservationRowDTO> items = rows.stream().map(p -> {
            String reservedDate = DATE_FMT.format(p.getReservedTime().toLocalDate());
            String reservedTime = TIME_FMT.format(p.getReservedTime().toLocalTime());
            Instant updatedAt   = p.getUpdatedTime().atZone(ZoneId.systemDefault()).toInstant();
            OrderAndReservationStatusEnum status = p.getStatus();

            return ClassReservationRowDTO.builder()
                    .reservedDate(reservedDate)
                    .className(p.getClassTitle())
                    .reservedTime(reservedTime)
                    .reserverName(p.getMemberName())    // 🔐 컨버터로 복호화된 평문
                    .reserverPhone(p.getMemberPhone())  // 🔐 컨버터로 복호화된 평문
                    .paymentAmount(p.getPrice())
                    .status(status)
                    .updatedAt(updatedAt)
                    .build();
        }).toList();

        return ClassReservationMonthlyResponseDTO.builder()
                .storeUrl(storeUrl).yearMonth(normalizedYm).items(items).build();
    }
}
