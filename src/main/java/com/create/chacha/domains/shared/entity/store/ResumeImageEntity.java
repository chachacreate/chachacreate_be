package com.create.chacha.domains.shared.entity.store;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 이력서 이미지 엔티티
 * <p>
 * ResumeEntity에 속한 이미지 파일 정보를 관리.
 * Soft Delete 개념을 적용하여 삭제 여부와 삭제 시간을 함께 관리.
 */
@Entity
@Table(name = "resume_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"resume"})
public class ResumeImageEntity extends BaseEntity {

    /**
     * 이력서 이미지 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연결된 이력서
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private StoreResumeEntity resume;

    /**
     * 이미지 파일 경로
     */
    @Column(nullable = false, length = 1000)
    @Convert(converter = AESConverter.class)
    private String url;

    /**
     * 이미지 관련 설명
     */
    @Column
    private String content;

    /**
     * 삭제 여부 (0 = 사용 중, 1 = 삭제됨)
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * 삭제 시각 (Soft Delete 용)
     */
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
}

