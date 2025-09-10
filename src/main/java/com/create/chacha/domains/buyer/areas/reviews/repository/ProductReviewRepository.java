package com.create.chacha.domains.buyer.areas.reviews.repository;

import com.create.chacha.domains.buyer.areas.reviews.dto.response.ProductReviewResponseDTO;
import com.create.chacha.domains.shared.entity.product.ReviewEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ReviewEntity, Long> {
//    List<ReviewEntity> findByProductIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long productId);
@Query("SELECT r " +
        "FROM ReviewEntity r " +
        "WHERE r.product.id = :productId " +
        "AND r.deletedAt IS NULL ")
List<ReviewEntity> findReviewsByProductId(@Param("productId") Long productId);

}
