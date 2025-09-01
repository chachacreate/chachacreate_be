package com.create.chacha.domains.shared.entity.store;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class ResumeImageEntity{

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
    @Convert(converter = AESConverter.class)
    private String url;

    /**
     * 이미지 관련 설명
     */
    private String content;
    /*
     * 생성 시간
     */
    @CreatedDate
    @Column(updatable = false)
    LocalDateTime createdAt;
    /*
     * 삭제 시간
     */
    private LocalDateTime deletedAt;

    /*
     * 삭제 여부
     */
    @Column(nullable = false, name = "is_deleted", columnDefinition = "TINYINT")
    private Boolean isDeleted = false;
}

