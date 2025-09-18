package com.create.chacha.domains.seller.areas.reviews.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // 꼭 이거!
import org.springframework.stereotype.Repository;

import com.create.chacha.domains.shared.entity.product.ReviewEntity;

@Repository
public interface ReviewReadRepository extends JpaRepository<ReviewEntity, Long> {

    // 프로젝션(인터페이스) 그대로 쓰는 경우
    interface ReviewRow {
        Long getReviewId();
        LocalDateTime getReviewCreatedAt();
        LocalDateTime getReviewUpdatedAt();
        Long getAuthorId();
        String getAuthorName();
        String getContent();
        String getProductName();
        LocalDateTime getProductCreatedAt();
        String getProductThumbnailUrl();
        Integer getLikeCount();
        String getProductRating(); // "X.X/5.0"
        Long getProductId();
    }
    
    // 전체 리뷰 조회
    @Query(value =
            "SELECT " +
            "  r.id AS reviewId, " +
            "  r.created_at AS reviewCreatedAt, " +
            "  r.updated_at AS reviewUpdatedAt, " +
            "  m.id AS authorId, " +
            "  m.name AS authorName, " +
            "  r.content AS content, " +
            "  r.product_id AS productId, " +
            "  (SELECT COUNT(*) FROM review_like rl " +
            "    WHERE rl.review_id = r.id AND rl.is_deleted = 0) AS likeCount, " +
            "  CONCAT(FORMAT(r.rating, 1), '/5.0') AS productRating " +
            "FROM review r " +
            "JOIN `member` m ON m.id = r.member_id " +
            "WHERE r.is_deleted = 0 " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true)
    List<ReviewRow> findReviewsOnly();

    @Query("SELECT r.member.name FROM ReviewEntity r WHERE r.id = :reviewId AND r.isDeleted = false")
    String findAuthorNameByReviewId(@Param("reviewId") Long reviewId);
}