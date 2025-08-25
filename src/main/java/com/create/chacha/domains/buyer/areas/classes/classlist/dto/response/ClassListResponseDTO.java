package com.create.chacha.domains.buyer.areas.classes.classlist.dto.response;

import java.util.List;

import com.create.chacha.domains.shared.classes.vo.ClassCardVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 클래스 목록 조회 응답 DTO
 * - Controller → Service → Repository 흐름에서 최종 응답 객체로 사용됨
 * - 클라이언트(React 등)에 JSON 형태로 내려가며,
 *   리스트 데이터와 페이지네이션 정보를 포함한다.
 */
@Getter                
@AllArgsConstructor 
@Builder               
@ToString             
public class ClassListResponseDTO {

    /**
     * 현재 페이지의 클래스 목록 데이터
     * - ClassCardVO: title, thumbnailUrl, storeName, locationDetail, price 등
     * - Repository 에서 JPQL new projection 으로 조회한 결과가 들어옴
     */
    private final List<ClassCardVO> content;

    /**
     * 현재 페이지 번호 (0부터 시작)
     * - 클라이언트 요청값 그대로 반환
     * - 프론트의 페이지네이션 UI 표시 용도
     */
    private final int page;

    /**
     * 한 페이지에 포함된 데이터 개수
     * - 요청 시 전달된 size 값
     * - 서버에서 보정(clamp)된 값일 수 있음
     */
    private final int size;

    /**
     * 조건에 맞는 전체 클래스 데이터 개수
     * - Repository count 쿼리 결과
     * - "총 n건" 안내 문구, 페이지네이션 계산에 필요
     */
    private final long totalElements;

    /**
     * 전체 페이지 수
     * - 계산식: ceil(totalElements / size)
     * - 프론트에서 페이지 이동 버튼을 표시할 때 사용
     */
    private final int totalPages;

    /**
     * 현재 페이지가 마지막 페이지인지 여부
     * - true면 "다음 페이지 없음"
     * - 프론트에서 '더보기/다음' 버튼을 비활성화하는 조건으로 사용
     */
    private final boolean last;
}
