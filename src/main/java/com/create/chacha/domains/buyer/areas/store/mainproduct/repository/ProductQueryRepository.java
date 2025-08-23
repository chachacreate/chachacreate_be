package com.create.chacha.domains.buyer.areas.store.mainproduct.repository;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request.ProductFilterRequestDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상품 조회 전용 Repository
 * 
 * 공통 조회 로직:
 *  - 상품명, 카테고리명, 가격, 썸네일 URL
 * 
 * 세부 조회 조건:
 *  - 인기상품 → 판매량 순서
 *  - 대표상품 → isFlagship = true
 *  - 전체상품 → 조건 없음
 */
@Repository
public class ProductQueryRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 특정 스토어 인기상품 조회 (판매량 기준 상위 3개)
     */
    public List<ProductResponseDTO> findBestProductsByStore(String storeUrl) {
        return em.createQuery(
                "SELECT new com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO(" +
                        "p.id, p.name, dc.name, p.price, pi.url) " +
                "FROM ProductEntity p " +
                "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
                "JOIN ProductImageEntity pi ON p.id = pi.product.id " +
                "JOIN SellerEntity s ON p.seller.id = s.id " +
                "JOIN StoreEntity st ON s.id = st.seller.id " +
                "WHERE st.url = :storeUrl " +
                "AND p.isDeleted = false " +
                "AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
                "ORDER BY p.saleCount DESC", ProductResponseDTO.class)
                .setParameter("storeUrl", storeUrl)
                .setMaxResults(3)
                .getResultList();
    }

    /**
     * 특정 스토어 대표상품 조회 (isFlagship = true, 3개)
     */
    public List<ProductResponseDTO> findFlagshipProductsByStore(String storeUrl) {
        return em.createQuery(
                "SELECT new com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO(" +
                        "p.id, p.name, dc.name, p.price, pi.url) " +
                "FROM ProductEntity p " +
                "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
                "JOIN ProductImageEntity pi ON p.id = pi.product.id " +
                "JOIN SellerEntity s ON p.seller.id = s.id " +
                "JOIN StoreEntity st ON s.id = st.seller.id " +
                "WHERE st.url = :storeUrl " +
                "AND p.isDeleted = false " +
                "AND p.isFlagship = true " +
                "AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
                "ORDER BY p.id DESC", ProductResponseDTO.class)
                .setParameter("storeUrl", storeUrl)
                .setMaxResults(3)
                .getResultList();
    }

    /**
     * 특정 스토어 전체상품 조회 (조건 없음)
     */
    public List<ProductResponseDTO> findAllProductsByStore(String storeUrl) {
        return em.createQuery(
                "SELECT new com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO(" +
                        "p.id, p.name, dc.name, p.price, pi.url) " +
                "FROM ProductEntity p " +
                "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
                "JOIN ProductImageEntity pi ON p.id = pi.product.id " +
                "JOIN SellerEntity s ON p.seller.id = s.id " +
                "JOIN StoreEntity st ON s.id = st.seller.id " +
                "WHERE st.url = :storeUrl " +
                "AND p.isDeleted = false " +
                "AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
                "ORDER BY p.id DESC", ProductResponseDTO.class)
                .setParameter("storeUrl", storeUrl)
                .getResultList();
    }
    
    /**
     * 필터링/검색 조건에 맞는 상품 조회
     */
	public List<ProductResponseDTO> findProductsByFilter(String storeUrl, ProductFilterRequestDTO filterDTO) {
		StringBuilder jpql = new StringBuilder(
				"SELECT new com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO("
						+ "p.id, p.name, dc.name, p.price, pi.url) " + "FROM ProductEntity p "
						+ "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id "
						+ "JOIN ProductImageEntity pi ON p.id = pi.product.id "
						+ "JOIN SellerEntity s ON p.seller.id = s.id " + "JOIN StoreEntity st ON s.id = st.seller.id "
						+ "LEFT JOIN ReviewEntity r ON r.product.id = p.id " 
						+ "WHERE st.url = :storeUrl " + "AND p.isDeleted = false "
						+ "AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL "
						);


        // 카테고리 필터링
        if ("ucategory".equalsIgnoreCase(filterDTO.getCategoryType()) && filterDTO.getCategoryId() != null) {
            jpql.append("AND dc.upCategory.id = :categoryId ");
        } else if ("dcategory".equalsIgnoreCase(filterDTO.getCategoryType()) && filterDTO.getCategoryId() != null) {
            jpql.append("AND dc.id = :categoryId ");
        }

        // 검색어 조회
        if (filterDTO.getKeyword() != null && !filterDTO.getKeyword().isEmpty()) {
            jpql.append("AND p.name LIKE CONCAT('%', :keyword, '%') ");
        }

		// 정렬 조건
		// filterDTO.getSort() 값이 null이면 기본값 "latest" 로 처리
		switch (filterDTO.getSort() != null ? filterDTO.getSort() : "latest") {
			// 주문 많은 순 → saleCount 내림차순 정렬
			case "order" -> jpql.append("ORDER BY p.saleCount DESC");
			// 조회순 → viewCount 내림차순 정렬
			case "view" -> jpql.append("ORDER BY p.viewCount DESC");
			// 평점순 → rating 내림차순 정렬
			case "rating" -> {
		        // 리뷰 테이블 조인 후 평균 평점 기준 정렬
		        jpql.append("GROUP BY p.id, p.name, dc.name, p.price, pi.url ");
		        jpql.append("ORDER BY AVG(r.rating) DESC");
		    }
			// 낮은 가격순 → price 오름차순 정렬
			case "price_low" -> jpql.append("ORDER BY p.price ASC");
			// 높은 가격순 → price 내림차순 정렬
			case "price_high" -> jpql.append("ORDER BY p.price DESC");
			// 기본값 (latest) → id 내림차순 정렬 = 최신 상품 순
		default -> jpql.append("ORDER BY p.id DESC");
		}


        var query = em.createQuery(jpql.toString(), ProductResponseDTO.class)
                .setParameter("storeUrl", storeUrl);

        if (filterDTO.getCategoryId() != null) {
            query.setParameter("categoryId", filterDTO.getCategoryId());
        }
        if (filterDTO.getKeyword() != null && !filterDTO.getKeyword().isEmpty()) {
            query.setParameter("keyword", filterDTO.getKeyword());
        }

        return query.getResultList();
    }
}
