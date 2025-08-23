package com.create.chacha.domains.buyer.areas.store.mainproduct.repository;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상품 조회 전용 Repository
 * - 인기상품/대표상품/신규상품 조회 로직 공용화
 */
@Repository
public class ProductQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public List<ProductResponseDTO> findBestProductsByStore(String storeUrl) {
		return em.createQuery(
				"SELECT new com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO("
						+ "p.id, p.name, dc.name, p.price, pi.url) " + "FROM ProductEntity p "
						+ "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id "
						+ "JOIN ProductImageEntity pi ON p.id = pi.product.id "
						+ "JOIN SellerEntity s ON p.seller.id = s.id " + "JOIN StoreEntity st ON s.id = st.seller.id "
						+ "WHERE st.url = :storeUrl " + "AND p.isDeleted = false "
						+ "AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " 
						+ "ORDER BY p.saleCount DESC",
				ProductResponseDTO.class).setParameter("storeUrl", storeUrl).setMaxResults(3).getResultList();
    }
}
