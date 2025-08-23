package com.create.chacha.domains.admin.areas.main.requestcount.service.serviceimpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.admin.areas.main.requestcount.dto.response.StoreCountResponseDTO;
import com.create.chacha.domains.admin.areas.main.requestcount.repository.RequestCountRepository;
import com.create.chacha.domains.admin.areas.main.requestcount.service.AdminMainService;
import com.create.chacha.domains.shared.constants.AcceptStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 관리자 메인 페이지 - 스토어 개설 요청 건수 Service 구현체
 * 관리자 메인 페이지 - 건수 조회 Service 구현체
 * <p>
 * - 스토어 개설 요청 건수 조회
 * - 이력서 신청 건수 조회
 * 
 * metric 파라미터에 따라 new / pending 조건으로 구분하여 카운트를 반환한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMainServiceImpl implements AdminMainService {

    private final RequestCountRepository requestCountRepository;


    /**
     * 스토어 개설 요청 건수 조회
     *
     * @param metric new → 오늘 등록된 건수
     *               pending → 상태가 PENDING인 건수
     * @return StoreCountResponseDTO (count 값 포함)
     */

    @Override
    public StoreCountResponseDTO getStoreCounts(String metric) {
        long count;

        switch (metric) {
            case "new" -> {
                Instant start = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
                Instant end = LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
                count = requestCountRepository.countNewStoreRequestsToday(start, end);
            }
                // 오늘 00:00 ~ 내일 00:00 사이 생성된 스토어 개수 조회
            }
            case "pending" -> {
                // 상태가 PENDING인 스토어 개수 조회
                count = requestCountRepository.countByStatus(AcceptStatusEnum.PENDING);
            }
            default -> throw new IllegalArgumentException("잘못된 metric 값입니다. [new | pending] 중 선택하세요.");
        }

        return StoreCountResponseDTO.builder()
                .count(count)
                .build();
    }

    /**
     * 이력서 신청 건수 조회
     *
     * @param metric new → 오늘 등록된 이력서 건수
     *               pending → 상태가 PENDING인 이력서 건수
     * @return StoreCountResponseDTO (count 값 포함)
     */
    @Override
    public StoreCountResponseDTO getResumeCounts(String metric) {
        long count;

        switch (metric) {
            case "new" -> {
                // 오늘 00:00 ~ 내일 00:00 사이 생성된 이력서 개수 조회
	            	LocalDateTime start = LocalDate.now().atStartOfDay();
	            	LocalDateTime end = start.plusDays(1);
	            	count = requestCountRepository.countNewResumeRequestsToday(start, end);

            }
            case "pending" -> {
                // 상태가 PENDING인 이력서 개수 조회
                count = requestCountRepository.countByStatus(AcceptStatusEnum.PENDING);
            }
            default -> throw new IllegalArgumentException("잘못된 metric 값입니다. [new | pending] 중 선택하세요.");
        }

        return StoreCountResponseDTO.builder()
                .count(count)
                .build();
    }
}
