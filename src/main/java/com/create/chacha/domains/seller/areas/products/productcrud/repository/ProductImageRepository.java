package com.create.chacha.domains.seller.areas.products.productcrud.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.product.ProductImageEntity;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
	List<ProductImageEntity> findByProduct_IdAndStatusOrderByImageSequenceAsc(
	        Long productId, ImageStatusEnum status);

	Optional<ProductImageEntity> findByProductIdAndStatusAndImageSequence(
            Long productId, ImageStatusEnum status, Integer imageSequence);

    List<ProductImageEntity> findByProductIdAndStatusAndIsDeletedFalseOrderByImageSequenceAsc(
            Long productId, ImageStatusEnum status);

    List<ProductImageEntity> findByProductIdAndStatusAndImageSequenceIn(
            Long productId, ImageStatusEnum status, Collection<Integer> seqs);

    @Query("""
           select coalesce(max(pi.imageSequence), 0)
           from ProductImageEntity pi
           where pi.product.id = :productId and pi.status = :status
           """)
    Integer findMaxSeq(@Param("productId") Long productId,
                       @Param("status") ImageStatusEnum status);
}
