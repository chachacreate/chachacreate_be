package com.create.chacha.domains.shared.entity.product;

import com.create.chacha.domains.shared.entity.BaseEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 리뷰 좋아요 엔티티
 * <p>
 * 회원이 특정 리뷰에 좋아요를 누른 내역을 관리하는 엔티티입니다.
 * 삭제 여부와 삭제 시간도 관리됩니다.
 * </p>
 *
 * <ul>
 *     <li>{@link #id} - 좋아요 ID (AUTO_INCREMENT)</li>
 *     <li>{@link #review} - 좋아요 대상 리뷰</li>
 *     <li>{@link #member} - 좋아요를 누른 회원</li>
 * </ul>
 */
@Entity
@Table(name = "review_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(value = AuditingEntityListener.class) // 변경이 일어나면 자동으로 넣어줌
public class ReviewLikeEntity{

    /** 좋아요 ID (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 좋아요 대상 리뷰 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    /** 좋아요를 누른 회원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;
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

