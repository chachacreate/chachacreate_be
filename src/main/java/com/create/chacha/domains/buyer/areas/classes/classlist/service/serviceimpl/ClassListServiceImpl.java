package com.create.chacha.domains.buyer.areas.classes.classlist.service.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.create.chacha.domains.buyer.areas.classes.classlist.dto.request.ClassListFilterDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.dto.response.ClassListResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classlist.repository.ClassInfoRepository;
import com.create.chacha.domains.buyer.areas.classes.classlist.service.ClassListService;
import com.create.chacha.domains.shared.classes.vo.ClassCardVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 클래스 목록 조회 비즈니스 로직 구현체
 *
 * - 조건조회(최신순, 마감임박순, 낮은가격순, 높은가격순, 키워드검색)
 * - storeUrl: "main" → 전체, 그 외에는 해당 스토어 조회
 * - Repository 호출 후 DTO 조립
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassListServiceImpl implements ClassListService {

    private final ClassInfoRepository classInfoRepository;

    @Override
    public ClassListResponseDTO getClassList(ClassListFilterDTO f) {
        // 1) 페이지 파라미터 보정
        int size = Math.min(Math.max(f.getSize(), 1), 100);
        int page = Math.max(f.getPage(), 0);

        // 2) 정렬 파싱 → Pageable 생성
        Sort sort = parseSort(f.getSort());
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3) 검색어 정규화
        String keyword = normalize(f.getKeyword());

        // 4) storeUrl 처리 ("main" → null)
        String effectiveStoreUrl =
                ("main".equalsIgnoreCase(f.getStoreUrl())) ? null : f.getStoreUrl();

        // 5) Repository 호출
        List<ClassCardVO> rows = classInfoRepository.findClassCards(effectiveStoreUrl, keyword, pageable);
        long total = classInfoRepository.countClassCards(effectiveStoreUrl, keyword);

        // 6) 페이지 메타 계산
        int totalPages = (int) Math.ceil((double) total / size);
        boolean last = (page + 1) >= Math.max(totalPages, 1);

        // 7) 로그
        log.info("클래스 목록 조회 실행 - storeUrl={}, sort={}, keyword={}, page={}, size={}, total={}",
                f.getStoreUrl(), f.getSort(), keyword, page, size, total);

        // 8) 응답 조립
        return ClassListResponseDTO.builder()
                .content(rows)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .last(last)
                .build();
    }

    @Override
    public List<ClassCardVO> getAvailableClassesByDate(String storeUrl, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = start.plusDays(1);

        // targetDate = 조회 기준일 자정
        LocalDateTime targetDate = start;

        String effectiveStoreUrl =
                ("main".equalsIgnoreCase(storeUrl)) ? null : storeUrl;

        return classInfoRepository.findAvailableClassesByDate(
                effectiveStoreUrl, targetDate, start, end
        );
    }

    /**
     * 조건조회 정렬 파싱
     * - latest     → 최신순 (id desc)
     * - end_date   → 마감임박순 (endDate asc, endTime asc)
     * - price_low  → 가격 낮은 순
     * - price_high → 가격 높은 순
     * - 기본값     → 최신순
     */
    private Sort parseSort(String sort) {
        if (!StringUtils.hasText(sort) || "latest".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Order.desc("id"));
        }
        return switch (sort.toLowerCase()) {
            case "end_date"   -> Sort.by(Sort.Order.asc("endDate"), Sort.Order.asc("endTime"));
            case "price_low"  -> Sort.by(Sort.Order.asc("price"));
            case "price_high" -> Sort.by(Sort.Order.desc("price"));
            default           -> Sort.by(Sort.Order.desc("id"));
        };
    }

    /**
     * 문자열 정규화
     */
    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
