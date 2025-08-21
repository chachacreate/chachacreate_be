package com.create.chacha.domains.shared.entity.store;

import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class StoreCustomEntity{

    /**
     * StoreEntity의 PK를 공유하는 기본 키 (FK & PK)
     */
    @Id
    private Long id;

    /**
     * 스토어 (PK + FK 매핑)
     */
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id") // store_custom.id = store.id
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
    private String fontColor = "#000000";

    /**
     * 헤더/푸터 색상 (default: #676F58)
     */
    private String headerFooterColor = "#676F58";

    /**
     * 공지사항 색상 (default: #FFF7DB)
     */
    private String noticeColor = "#FFF7DB";

    /**
     * 스토어 설명 영역 색상 (default: #FFF6EE)
     */
    private String descriptionColor = "#FFF6EE";

    /**
     * 인기상품/대표상품 색상 (default: #FFF7DB)
     */
    private String popularColor = "#FFF7DB";

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
