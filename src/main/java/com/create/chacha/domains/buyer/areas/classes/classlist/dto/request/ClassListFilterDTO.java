package com.create.chacha.domains.buyer.areas.classes.classlist.dto.request;

import org.springframework.lang.Nullable;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * 클래스 목록 조회용 요청 DTO
 * - 클라이언트가 전달하는 검색/페이지네이션 조건을 담는다.
 * - 컨트롤러에서 @ModelAttribute 로 바인딩되어 Service → Repository 로 전달됨.
 * - 현재 단계는 "전체 조회 + 키워드 검색 + 기본 페이지네이션"만 활성화.
 *   (추가 필터들은 TODO 주석만 남기고 실제 코드는 추후 확장)
 */
@Getter
@Setter
public class ClassListFilterDTO {

    /**
     * 키워드 검색 (옵션)
     * - 제목(title)에 대한 부분 일치 검색용 문자열
     * - null/빈문자열이면 전체 조회
     * - Repository에서는 JPQL LIKE 조건으로 사용
     *   예) AND (:keyword IS NULL OR ci.title LIKE %:keyword%)
     */
    @Nullable
    private String keyword;   // 제목 like 검색 값

    /**
     * 페이지 번호 (0-base)
     * - 0부터 시작하는 인덱스 방식
     * - 음수 방지를 위해 @Min(0) 적용 → 잘못된 요청 시 Bean Validation 400 응답
     * - Service 계층에서 보정(clamp)되어 Pageable 생성에 사용됨
     */
    @Min(0)
    private int page = 0;     // 기본값 0 페이지

    /**
     * 페이지 크기 (한 페이지 당 항목 수)
     * - 최소 1 이상이어야 하므로 @Min(1)
     * - 과도한 값 방지를 위해 Service 계층에서 상한(예: 100)으로 clamp
     * - Pageable 생성 시 size 로 사용
     */
    @Min(1)
    private int size = 20;    // 기본값 20, Service에서 상한 보정

    // ─────────────────────────────────────────────────────────────────────────────
    // ▼▼▼ 추후 확장 예정 필터들(요구사항 확정 뒤에 활성화) ▼▼▼
    // 최신순, 마감임박순, 낮은 가격순, 높은 가격순
    // ※ 지금 단계에서는 스펙만 남겨두고 실제 필드/로직은 추가하지 않음.
    // ─────────────────────────────────────────────────────────────────────────────
}
