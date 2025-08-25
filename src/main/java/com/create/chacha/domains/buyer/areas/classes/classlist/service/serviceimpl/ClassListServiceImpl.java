package com.create.chacha.domains.buyer.areas.classes.classlist.service.serviceimpl;

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
 * - 파라미터 보정(검증 보완) + 정렬 파싱 + 페이지네이션 계산을 담당.
 * - Repository 로부터 VO 리스트와 총합을 받아 응답 DTO로 조립
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassListServiceImpl implements ClassListService {

    private final ClassInfoRepository classInfoRepository;

    @Override
    public ClassListResponseDTO getClassList(ClassListFilterDTO f, String sort) {
        // 1) 페이지 파라미터 보정 (방어 로직)
        // - size: 최소 1, 최대 100 (API 오남용 방지)
        // - page: 음수 방지
        int size = Math.min(Math.max(f.getSize(), 1), 100);
        int page = Math.max(f.getPage(), 0);

        // 2) 정렬 파싱
        // - 기본값: createdAt DESC
        // - 클라이언트에서 ?sort=price,asc 처럼 던지면 동적으로 반영
        Sort springSort = parseSort(sort);

        // 3) Pageable 구성: Repository 에 그대로 전달
        Pageable pageable = PageRequest.of(page, size, springSort);

        // 4) 검색어 정규화: 공백/빈문자 → null 로 변환
        String keyword = normalize(f.getKeyword());

        // 5) 목록 조회 (VO 프로젝션): 필요한 필드만 DB에서 꺼내온다
        List<ClassCardVO> rows = classInfoRepository.findClassCards(keyword, pageable);

        // 6) 총합 조회: 페이지 메타(totalElements/totalPages/last) 계산용
        long total = classInfoRepository.countClassCards(keyword);

        // 7) 페이지 메타 계산
        int totalPages = (int) Math.ceil((double) total / size);
        boolean last = (page + 1) >= Math.max(totalPages, 1);

        // 8) 응답 조립 (불변 DTO + 빌더)
        return ClassListResponseDTO.builder()
                .content(rows)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .last(last)
                .build();
    }

    /**
     * 정렬 문자열 파싱
     * - 형식: "필드명,방향" (예: "createdAt,desc" / "price,asc")
     * - 방향 미지정 시 ASC 로 간주
     * - 유효하지 않은 입력일 때는 기본 정렬(createdAt desc)로 처리
     */
    private Sort parseSort(String sort) {
        // 기본 정렬: 최신 생성순
        if (!StringUtils.hasText(sort)) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        String[] parts = sort.split(",");
        String prop = parts[0].trim();
        boolean asc = parts.length < 2 || !"desc".equalsIgnoreCase(parts[1].trim());
        return asc ? Sort.by(Sort.Order.asc(prop)) : Sort.by(Sort.Order.desc(prop));
    }

    /**
     * 문자열 정규화
     * - null/공백/빈문자 → null
     * - Repository 의 (:keyword IS NULL OR …) 조건 처리에 사용
     */
    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
