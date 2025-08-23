package com.create.chacha.domains.buyer.areas.store.mainproduct.repository;

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
}
