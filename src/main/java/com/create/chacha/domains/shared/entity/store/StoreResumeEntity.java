package com.create.chacha.domains.shared.entity.store;

import com.create.chacha.domains.shared.constants.AcceptStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 이력서 엔티티
 * <p>
 * 지원자가 스토어에 제출한 이력서 정보를 관리.
 * 검증 상태(PENDING, APPROVED, REJECTED)를 Enum으로 구분.
 * BaseEntity를 상속받아 생성/수정 시간을 자동 관리.
 */
@Entity
@Table(name = "store_resume")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"store"})
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class StoreResumeEntity{

    /**
     * 이력서 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 지원한 스토어
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    /**
     * 이력서 검증 상태
     */
    @Enumerated(EnumType.STRING)
    private AcceptStatusEnum status = AcceptStatusEnum.PENDING;
    /*
     * 생성 시간
     */
    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdAt;
    /*
     * 수정 시간
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}