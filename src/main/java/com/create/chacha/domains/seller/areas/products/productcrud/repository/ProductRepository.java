package com.create.chacha.domains.seller.areas.products.productcrud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.create.chacha.domains.shared.entity.product.ProductEntity;
import com.create.chacha.domains.shared.entity.product.ProductImageEntity;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductListItemDTO;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	
	// 상품 수정
	@Query("""
	           select (count(p) > 0)
	           from ProductEntity p
	             join p.seller s
	             join StoreEntity st on st.seller = s
	           where p.id = :productId and st.url = :storeUrl
	           """)
	 boolean existsByIdAndStoreUrl(@Param("productId") Long productId,
	                               @Param("storeUrl") String storeUrl);
	
	@Query("""
	           select p
	           from ProductEntity p
	           where p.id = :id
	           """)
	Optional<ProductEntity> findForUpdate(@Param("id") Long id);
	
	// 상품 수정 조회
	@Query("""
	   select p
	   from ProductEntity p
	     join fetch p.downCategory dc
	     join fetch dc.upCategory uc
	   where p.id = :productId
	     and p.seller.id = :sellerId
	""")
	Optional<ProductEntity> findForEdit(@Param("sellerId") Long sellerId,
	                                    @Param("productId") Long productId);
	
	// 대표 상품 설정/해제
	// 삭제되지 않은 대표상품 개수 (스토어 당 최대 3 제한용)
    @Query("""
           select count(p)
             from ProductEntity p
            where p.seller.id = :sellerId
              and p.isFlagship = true
              and p.isDeleted = false
           """)
    long countActiveFlagshipBySellerId(@Param("sellerId") Long sellerId);

    // 이 스토어(=sellerId)의 소유 상품 중 id IN (...)
    @Query("""
           select p
             from ProductEntity p
            where p.seller.id = :sellerId
              and p.id in :ids
           """)
    List<ProductEntity> findBySellerIdAndIdIn(@Param("sellerId") Long sellerId,
                                              @Param("ids") List<Long> ids);

    // 요청 목록 중 삭제된 게 하나라도 있는지(설정 불가 검사)
    @Query("""
        select (count(p) > 0) from ProductEntity p
        where p.seller.id = :sellerId
          and p.id in :ids
          and p.isDeleted = true
    """)
    boolean existsAnyDeletedIn(@Param("sellerId") Long sellerId,
                               @Param("ids") List<Long> ids);
	
	// 상품 조회
	@Query("""
		    select new com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductListItemDTO(
		        p.id,
		        (
		           select pi1.url from ProductImageEntity pi1
		           where pi1.product = p
		             and pi1.status = :thumb
		             and pi1.imageSequence = (
		                select min(pi2.imageSequence) from ProductImageEntity pi2
		                where pi2.product = p and pi2.status = :thumb
		             )
		        ),
		        p.name,
		        p.price,
		        p.stock,
		        uc.name,
		        dc.name,
		        p.createdAt,
		        p.updatedAt,
		        p.deletedAt,
		        p.isFlagship,
		        p.isDeleted
		    )
		    from ProductEntity p
		       join p.downCategory dc
		       join dc.upCategory uc
		    where p.seller.id = :sellerId
		    order by p.createdAt desc
		""")
		List<ProductListItemDTO> findListBySellerIdForStore(@Param("sellerId") Long sellerId,
		                                                    @Param("thumb") ImageStatusEnum thumb);
}

