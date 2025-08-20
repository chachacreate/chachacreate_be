package com.create.chacha.domains.shared.entity.product;

import com.create.chacha.config.app.constants.ImageStatusEnumConverter;
import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 상품 이미지 엔티티
 * <p>
 * 상품에 등록되는 이미지 정보를 관리하는 엔티티입니다.
 * 각 이미지는 상품과 연관되며, 썸네일 또는 상세 이미지로 구분됩니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 기본 키 (AUTO_INCREMENT)</li>
 *     <li>{@link #product} - 연결된 상품</li>
 *     <li>{@link #url} - 이미지 파일 경로</li>
 *     <li>{@link #sequence} - 등록 순서 (썸네일 우선순위 결정)</li>
 *     <li>{@link #status} - 이미지 상태 (DESCRIPTION or THUMBNAIL)</li>
 * </ul>
 */
@Entity
@Table(name = "product_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageEntity extends BaseEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연결된 상품
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /**
     * 이미지 파일 경로
     */
    @Column(nullable = false, length = 500)
    private String url;

    /**
     * 이미지 등록 순서 (썸네일 우선순위 결정)
     */
    @Column(nullable = false)
    private Integer sequence;

    /**
     * 이미지 상태 (DESCRIPTION, THUMBNAIL)
     */
    @Column(nullable = false, length = 20)
    @Convert(converter = ImageStatusEnumConverter.class)
    private ImageStatusEnum status;
}
