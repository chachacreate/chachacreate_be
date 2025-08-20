package com.create.chacha.domains.shared.entity.store;


import com.create.chacha.domains.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 공지사항 엔티티
 * <p>
 * 각 스토어에서 작성하는 공지사항 정보를 저장.
 */
@Entity
@Table(name = "notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, exclude = {"store"})
public class NoticeEntity extends BaseEntity {

    /**
     * 공지사항 기본 키 (AUTO_INCREMENT)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 공지사항이 속한 스토어
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    /**
     * 공지사항 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 공지사항 내용
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 중요 여부 (일반 : false, 중요 : true)
     */
    @Column(name = "important_check", nullable = false)
    private Boolean importantCheck = false;
}

