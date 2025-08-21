package com.create.chacha.domains.shared.entity.classcore;

import com.create.chacha.config.app.constants.ImageStatusEnumConverter;
import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 클래스 이미지 엔티티
 * <p>
 * 각 클래스별 이미지 정보를 관리.
 * 이미지 URL, 등록 순서, 상태(DESCRIPTION / THUMBNAIL) 등을 포함하며,
 * BaseEntity를 상속받아 생성/삭제 시간과 삭제 여부를 자동 관리.
 */
@Entity
@Table(name = "class_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"classInfo"})
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class ClassImageEntity {

    /**
     * 이미지 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이미지가 속한 클래스
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_info_id", nullable = false)
    private ClassInfoEntity classInfo;

    /**
     * 이미지 파일 경로(URL)
     */
    @Convert(converter = AESConverter.class)
    private String url;

    /**
     * 이미지 등록 순서 (썸네일 표시 순서에 영향)
     */
    private Integer imageSequence;

    /**
     * 이미지 상태 (DESCRIPTION 또는 THUMBNAIL)
     */
    @Column(columnDefinition = "TINYINT")
    @Convert(converter = ImageStatusEnumConverter.class)
    private ImageStatusEnum status;

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
    private Boolean isDeleted;
}