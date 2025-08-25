package com.create.chacha.domains.buyer.areas.classes.reservations.service.serviceimpl;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.repository.ClassReservationQueryRepository;
import com.create.chacha.domains.buyer.areas.classes.reservations.service.ClassReservationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 예약 조회 비즈니스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassReservationQueryServiceImpl implements ClassReservationQueryService {

    private final ClassReservationQueryRepository repository;

    @Override
    public List<ClassReservationSummaryResponseDTO> getReservationsByMember(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("memberId는 양수여야 합니다.");
        }

        List<ClassReservationSummaryResponseDTO> result = repository.findSummariesByMemberId(memberId);
        log.info("[buyer] memberId={} 예약 조회 결과 {}건", memberId, result.size());
        return result;
    }
}
