package com.create.chacha.domains.shared.entity.store;

import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 스토어 커스터마이징(디자인) 정보 엔티티
 * <p>
 * BaseEntity를 상속받아 생성/수정/삭제 시간을 자동 관리
 */
@Entity
@Table(name = "store_custom")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class StoreCustomEntity extends BaseEntity {

    /**
     * StoreEntity의 PK를 공유하는 기본 키 (FK & PK)
     */
    @Id
    private Integer id;

    /**
     * 스토어 (PK + FK 매핑)
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id") // store_custom.id = store.id
    private StoreEntity store;

    /**
     * 폰트 메타데이터 (StoreFontEntity 1:1 관계)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "font_id")
    private StoreFontEntity font;

    /**
     * 아이콘 메타데이터 (StoreIconEntity 1:1 관계)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_id")
    private StoreIconEntity icon;

    /**
     * 기본 글자 색상 (default: #000000)
     */
    @Column(name = "font_color", nullable = false, length = 20)
    private String fontColor = "#000000";

    /**
     * 헤더/푸터 색상 (default: #676F58)
     */
    @Column(name = "header_footer_color", nullable = false, length = 20)
    private String headerFooterColor = "#676F58";

    /**
     * 공지사항 색상 (default: #FFF7DB)
     */
    @Column(name = "notice_color", nullable = false, length = 20)
    private String noticeColor = "#FFF7DB";

    /**
     * 스토어 설명 영역 색상 (default: #FFF6EE)
     */
    @Column(name = "description_color", nullable = false, length = 20)
    private String descriptionColor = "#FFF6EE";

    /**
     * 인기상품/대표상품 색상 (default: #FFF7DB)
     */
    @Column(name = "popular_color", nullable = false, length = 20)
    private String popularColor = "#FFF7DB";
}
