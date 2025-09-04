package com.create.chacha.domains.buyer.areas.classes.reservations.service.serviceimpl;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.repository.ClassReservationQueryRepository;
import com.create.chacha.domains.buyer.areas.classes.reservations.service.ClassReservationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 *  구매자 클래스 예약 조회
 * */
@Service
@RequiredArgsConstructor
public class ClassReservationQueryServiceImpl implements ClassReservationQueryService {

    private final ClassReservationQueryRepository repository;
    private final LegacyAPIUtil legacyAPIUtil;

    @Transactional(readOnly = true)
    @Override
    public List<ClassReservationSummaryResponseDTO> getReservationsByMember(Long memberId, String filterRaw, String q) {
    		final String normalizedFilter = normalizeFilter(filterRaw); // "ALL" | "UPCOMING" | "PAST" | "CANCELED"
        final String kw = normalizeKeyword(q);                      // null 또는 "%keyword%"
        final LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // DB에서 예약 목록 조회 (이때는 storeId만 포함)
        List<ClassReservationSummaryResponseDTO> reservations =
                repository.findSummariesByMemberAndFilterAndKeyword(memberId, now, normalizedFilter, kw);

        // storeId를 통해 storeName, storeUrl 채우기(Legacy API)
        reservations.forEach(r -> {
            LegacyStoreDTO legacyStore = legacyAPIUtil.getLegacyStoreDataById(r.getStoreId());
            r.setStoreName(legacyStore.getStoreName());
            r.setStoreUrl(legacyStore.getStoreUrl());
        });

        return reservations;
    }

    /**
     * 허용 입력:
     * - 한국어: 전체/예정/지난/취소
     * - 숫자: 1/2/3/4
     * - 영어: ALL/UPCOMING/PAST/CANCELED
     */
    private String normalizeFilter(String raw) {
        if (raw == null) return "ALL";
        String s = raw.trim();
        switch (s) { // 숫자
            case "1": return "ALL";
            case "2": return "UPCOMING";
            case "3": return "PAST";
            case "4": return "CANCELED";
        }
        switch (s) { // 한글
            case "전체": return "ALL";
            case "예정": return "UPCOMING";
            case "지난": return "PAST";
            case "취소": return "CANCELED";
        }
        s = s.toUpperCase(); // 영어
        switch (s) {
            case "ALL":
            case "UPCOMING":
            case "PAST":
            case "CANCELED":
                return s;
            default:
                return "ALL";
        }
    }

    private String normalizeKeyword(String q) {
        if (q == null) return null;
        String s = q.trim();
        if (s.isEmpty()) return null;
        return "%" + s.toLowerCase() + "%";
    }
}