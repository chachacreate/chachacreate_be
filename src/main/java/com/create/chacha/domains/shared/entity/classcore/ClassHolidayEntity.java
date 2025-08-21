package com.create.chacha.domains.shared.entity.classcore;

import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 클래스 휴일 정보 엔티티
 * <p>
 * 각 클래스별 휴일(운영하지 않는 날) 정보를 관리.
 * 휴일 이름, 날짜를 포함하며, BaseEntity를 상속받아 생성/수정/삭제 시간과 삭제 여부를 자동 관리.
 */
@Entity
@Table(name = "class_holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"classInfo"})
public class ClassHolidayEntity extends BaseEntity {

    /**
     * 휴일 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 휴일이 속한 클래스
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_info_id", nullable = false)
    private ClassInfoEntity classInfo;

    /**
     * 휴일 이름
     */
    private String name;

    /**
     * 휴일 날짜
     */
    private LocalDateTime restDate;
}
