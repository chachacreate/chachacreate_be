package com.create.chacha.domains.buyer.areas.store.mainproduct.repository;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request.ProductFilterRequestDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import com.create.chacha.domains.shared.product.vo.ProductVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상품 조회 전용 Repository
 * - 인기상품 / 대표상품 / 전체상품 / 필터링 / 검색 조회 로직
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
    public List<ProductVO> findProductsByFilter(String storeUrl, ProductFilterRequestDTO filterDTO) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new com.create.chacha.domains.shared.product.vo.ProductVO(" +
                        "p.id, p.name, dc.name, p.price, pi.url, AVG(r.rating)) " +
                        "FROM ProductEntity p " +
                        "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
                        "JOIN ProductImageEntity pi ON p.id = pi.product.id " +
                        "JOIN SellerEntity s ON p.seller.id = s.id " +
                        "JOIN StoreEntity st ON s.id = st.seller.id " +
                        "LEFT JOIN ReviewEntity r ON r.product.id = p.id " +
                        "WHERE st.url = :storeUrl " +
                        "AND p.isDeleted = false " +
                        "AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL "
        );

        // 카테고리 필터
        if ("ucategory".equalsIgnoreCase(filterDTO.getCategoryType()) && filterDTO.getCategoryId() != null) {
            jpql.append("AND dc.upCategory.id = :categoryId ");
        } else if ("dcategory".equalsIgnoreCase(filterDTO.getCategoryType()) && filterDTO.getCategoryId() != null) {
            jpql.append("AND dc.id = :categoryId ");
        }

        // 검색어 (상품명 LIKE 검색)
        if (filterDTO.getKeyword() != null && !filterDTO.getKeyword().isEmpty()) {
            jpql.append("AND p.name LIKE CONCAT('%', :keyword, '%') ");
        }

        // 평점 정렬을 위한 GROUP BY
        boolean sortByRating = "rating".equalsIgnoreCase(filterDTO.getSort());
        if (sortByRating || true) {
            jpql.append("GROUP BY p.id, p.name, dc.name, p.price, pi.url ");
        }

        // 정렬 조건
        switch (filterDTO.getSort() != null ? filterDTO.getSort() : "latest") {
            case "order" -> jpql.append("ORDER BY p.saleCount DESC");
            case "view" -> jpql.append("ORDER BY p.viewCount DESC");
            case "rating" -> jpql.append("ORDER BY AVG(r.rating) DESC");
            case "price_low" -> jpql.append("ORDER BY p.price ASC");
            case "price_high" -> jpql.append("ORDER BY p.price DESC");
            default -> jpql.append("ORDER BY p.id DESC"); // 최신순
        }

        var query = em.createQuery(jpql.toString(), ProductVO.class)
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
