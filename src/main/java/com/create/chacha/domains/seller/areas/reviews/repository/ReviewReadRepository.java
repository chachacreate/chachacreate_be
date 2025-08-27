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
    }
    
    // 특정 상품 리뷰 조회
    @Query(value =
            "SELECT " +
            "  r.id AS reviewId, " +
            "  r.created_at AS reviewCreatedAt, " +
            "  r.updated_at AS reviewUpdatedAt, " +
            "  m.id AS authorId, " +
            "  m.name AS authorName, " +
            "  r.content AS content, " +
            "  p.name AS productName, " +
            "  p.created_at AS productCreatedAt, " +
            "  (SELECT pi.url FROM product_image pi " +
            "    WHERE pi.product_id = p.id " +
            "      AND pi.status = 1 " +
            "      AND pi.image_sequence = 1 " +
            "      AND pi.is_deleted = 0 " +
            "    LIMIT 1) AS productThumbnailUrl, " +
            "  (SELECT COUNT(*) FROM review_like rl " +
            "    WHERE rl.review_id = r.id AND rl.is_deleted = 0) AS likeCount, " +
            "  CONCAT(FORMAT(IFNULL((SELECT ROUND(AVG(r2.rating) * 2) / 2 " +
            "                         FROM review r2 " +
            "                         WHERE r2.product_id = p.id AND r2.is_deleted = 0), 0), 1), '/5.0') AS productRating " +
            "FROM review r " +
            "JOIN product p ON p.id = r.product_id " +
            "JOIN store   s ON s.id = p.seller_id " +
            "JOIN `member` m ON m.id = r.member_id " +
            "WHERE s.url = :storeUrl " +
            "  AND p.id = :productId " +
            "  AND r.is_deleted = 0 " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true)
        List<ReviewRow> findReviewsByStoreUrlAndProductId(@Param("storeUrl") String storeUrl,
                                                          @Param("productId") Long productId);
    
    // 전체 리뷰 조회
    @Query(value =
    	    "SELECT " +
    	    "  r.id AS reviewId, " +
    	    "  r.created_at AS reviewCreatedAt, " +
    	    "  r.updated_at AS reviewUpdatedAt, " +
    	    "  m.id AS authorId, " +
    	    "  m.name AS authorName, " +
    	    "  r.content AS content, " +
    	    "  p.name AS productName, " +
    	    "  p.created_at AS productCreatedAt, " +
    	    "  (SELECT pi.url FROM product_image pi " +
    	    "    WHERE pi.product_id = p.id " +
    	    "      AND pi.status = 1 " +              /* 썸네일 플래그 */
    	    "      AND pi.image_sequence = 1 " +      /* 대표 1번 */
    	    "      AND pi.is_deleted = 0 " +
    	    "    LIMIT 1) AS productThumbnailUrl, " +
    	    "  (SELECT COUNT(*) FROM review_like rl " +
    	    "    WHERE rl.review_id = r.id AND rl.is_deleted = 0) AS likeCount, " +
    	    "  CONCAT(FORMAT(IFNULL((SELECT ROUND(AVG(r2.rating) * 2) / 2 " +
    	    "                         FROM review r2 " +
    	    "                         WHERE r2.product_id = p.id AND r2.is_deleted = 0), 0), 1), '/5.0') AS productRating " +
    	    "FROM review r " +
    	    "JOIN product p ON p.id = r.product_id " +
    	    "JOIN store   s ON s.id = p.seller_id " +
    	    "JOIN `member` m ON m.id = r.member_id " +
    	    "WHERE s.url = :storeUrl " +
    	    "  AND r.is_deleted = 0 " +
    	    "ORDER BY p.id ASC, r.created_at DESC",    /* <-- 여기! 상품 오름차순, 리뷰 최신순 */
    	    nativeQuery = true)
    List<ReviewRow> findReviewsByStoreUrl(@Param("storeUrl") String storeUrl); // @Param 필수
}