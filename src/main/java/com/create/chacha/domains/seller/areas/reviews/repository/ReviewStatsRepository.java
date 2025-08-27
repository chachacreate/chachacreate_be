package com.create.chacha.domains.seller.areas.reviews.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.create.chacha.domains.shared.entity.product.ReviewEntity;

@Repository
public interface ReviewStatsRepository extends JpaRepository<ReviewEntity, Long> {

    interface StatsRow {
        Double getBucket(); // 0.0, 0.5, ..., 5.0
        Long getCnt();
    }

    @Query(value =
        "SELECT CAST(LEAST(5.0, GREATEST(0.0, ROUND(r.rating * 2) / 2)) AS DECIMAL(3,1)) AS bucket, " +
        "       COUNT(*) AS cnt " +
        "FROM review r " +
        "JOIN product p ON p.id = r.product_id " +
        "JOIN store   s ON s.id = p.seller_id " +
        "WHERE s.url = :storeUrl " +
        "  AND r.is_deleted = 0 " +
        "  AND r.rating IS NOT NULL " +
        "GROUP BY bucket " +
        "ORDER BY bucket ASC",
        nativeQuery = true)
    List<StatsRow> findStatsByStoreUrl(@Param("storeUrl") String storeUrl);

    @Query(value =
        "SELECT CAST(LEAST(5.0, GREATEST(0.0, ROUND(r.rating * 2) / 2)) AS DECIMAL(3,1)) AS bucket, " +
        "       COUNT(*) AS cnt " +
        "FROM review r " +
        "JOIN product p ON p.id = r.product_id " +
        "JOIN store   s ON s.id = p.seller_id " +
        "WHERE s.url = :storeUrl " +
        "  AND p.id = :productId " +
        "  AND r.is_deleted = 0 " +
        "  AND r.rating IS NOT NULL " +
        "GROUP BY bucket " +
        "ORDER BY bucket ASC",
        nativeQuery = true)
    List<StatsRow> findStatsByStoreUrlAndProductId(@Param("storeUrl") String storeUrl,
                                                   @Param("productId") Long productId);
}
