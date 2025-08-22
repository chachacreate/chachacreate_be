package com.create.chacha.domains.admin.areas.main.requestcount.service.serviceimpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.admin.areas.main.requestcount.dto.response.StoreCountResponseDTO;
import com.create.chacha.domains.admin.areas.main.requestcount.repository.RequestCountRepository;
import com.create.chacha.domains.admin.areas.main.requestcount.service.AdminMainService;
import com.create.chacha.domains.shared.constants.AcceptStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 관리자 메인 페이지 - 스토어 개설 요청 건수 Service 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMainServiceImpl implements AdminMainService {

    private final RequestCountRepository requestCountRepository;

    @Override
    public StoreCountResponseDTO getStoreCounts(String metric) {
        long count;

        switch (metric) {
            case "new" -> {
                Instant start = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
                Instant end = LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
                count = requestCountRepository.countNewStoreRequestsToday(start, end);
            }
            case "pending" -> {
                count = requestCountRepository.countByStatus(AcceptStatusEnum.PENDING);
            }
            default -> throw new IllegalArgumentException("잘못된 metric 값입니다. [new | pending] 중 선택하세요.");
        }

        return StoreCountResponseDTO.builder()
                .count(count)
                .build();
    }
}


