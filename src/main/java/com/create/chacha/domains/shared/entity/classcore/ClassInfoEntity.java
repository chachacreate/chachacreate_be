package com.create.chacha.domains.shared.entity.classcore;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 클래스 정보 엔티티
 * <p>
 * 각 스토어에서 운영하는 클래스 정보를 관리.
 * 클래스명, 설명, 가격, 예약 안내, 최대 참여 인원, 주소, 시작/종료 시간 등을 포함.
 * BaseEntity를 상속받아 생성/수정/삭제 시간과 삭제 여부를 자동 관리.
 */
@Entity
@Table(name = "class_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"store"})
public class ClassInfoEntity extends BaseEntity {

    /**
     * 클래스 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 클래스가 속한 스토어
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;
    
 // 🔸 추가: 읽기 전용 스칼라 컬럼 매핑 (조인 없이 필터 용도)
    @Column(name = "store_id", insertable = false, updatable = false)
    private Long storeId;

    /**
     * 클래스명
     */
    private String title;

    /**
     * 클래스 설명
     */
    private String detail;

    /**
     * 회당 가격
     */
    private Integer price;

    /**
     * 예약 관련 주의 사항
     */
    private String guideline;

    /**
     * 최대 참여 인원
     */
    private Integer participant;

    /**
     * 주소 우편번호
     */
    private String postNum;

    /**
     * 도로명 주소
     */
    private String addressRoad;

    /**
     * 상세 주소
     */
    private String addressDetail;

    /**
     * 추가 주소 정보
     */
    private String addressExtra;

    /**
     * 클래스 시작 날짜
     */
    private LocalDateTime startDate;

    /**
     * 클래스 종료 날짜
     */
    private LocalDateTime endDate;

    /**
     * 클래스 시작 시간
     */
    private LocalTime startTime;

    /**
     * 클래스 종료 시간
     */
    private LocalTime endTime;

    /**
     * 클래스 시간 간격 (분 단위)
     */
    private Integer timeInterval;
}