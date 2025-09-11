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
        Long getProductId();
    }

    @Query(value =
            "SELECT CAST(LEAST(5.0, GREATEST(0.0, ROUND(r.rating * 2) / 2)) AS DECIMAL(3,1)) AS bucket, r.product_id AS productId, " +
                    "       COUNT(*) AS cnt " +
                    "FROM review r " +
                    "WHERE r.is_deleted = 0 " +
                    "  AND r.rating IS NOT NULL " +
                    "GROUP BY bucket, productId " +
                    "ORDER BY bucket ASC",
            nativeQuery = true)
    List<StatsRow> findStatsOnly();
}
