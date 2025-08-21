package com.create.chacha.domains.shared.entity.store;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.shared.constants.AcceptStatusEnum;
import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.seller.SellerEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 스토어 정보 엔티티
 * <p>
 * BaseEntity를 상속받아 생성/수정/삭제 시간을 자동 관리
 */
@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class StoreEntity extends BaseEntity {

    /**
     * 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 판매자 (SellerEntity와 1:1 관계)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private SellerEntity seller;

    /**
     * 스토어 로고 이미지 경로
     */
    @Convert(converter = AESConverter.class)
    private String logo;

    /**
     * 스토어명
     */
    private String name;

    /**
     * 스토어 설명
     */
    private String content;

    /**
     * 스토어 URL (중복 불가, 변경 불가)
     */
    private String url;

    /**
     * 스토어 상품들의 총 판매 수 합계
     */
    private Integer saleCount = 0;

    /**
     * 스토어 상품들의 총 조회 수 합계
     */
    private Integer viewCount = 0;

    /**
     * 스토어 상태 (예: ACTIVE, INACTIVE, SUSPENDED 등)
     */
    private AcceptStatusEnum status;

    /**
     * StoreCustomEntity와 1:1 매핑 (store.id = store_custom.id)
     */
    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private StoreCustomEntity storeCustomEntity;
}
