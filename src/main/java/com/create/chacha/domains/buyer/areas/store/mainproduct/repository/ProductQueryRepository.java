package com.create.chacha.domains.buyer.areas.store.mainproduct.repository;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request.ProductFilterRequestDTO;
import com.create.chacha.domains.shared.product.vo.ProductVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상품 조회 전용 Repository
 * - 인기상품 / 대표상품 / 전체상품 / 필터링 / 검색 조회 로직
 * - 반환은 모두 VO(ProductVO)로 통일 (Service에서 DTO로 감싸기)
 */
@Repository
public class ProductQueryRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 특정 스토어 인기상품 조회 (판매량 기준 상위 3개)
     * - 중복 썸네일 제거: THUMBNAIL + imageSequence=1 만 조인
     * - 평균 평점 집계: LEFT JOIN + AVG(r.rating)
     * - 정렬: saleCount DESC, tie-breaker로 id DESC
     */
    public List<ProductVO> findBestProductsByStore(String storeUrl) {
        return em.createQuery(
            "SELECT new com.create.chacha.domains.shared.product.vo.ProductVO(" +
            "  p.id, p.name, uc.name, dc.name, p.price, pi.url, COALESCE(AVG(r.rating), 0)) " +
            "FROM ProductEntity p " +
            "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            // 대표 썸네일 1장만
            "LEFT JOIN ProductImageEntity pi ON pi.product.id = p.id " +
            "  AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
            "  AND pi.imageSequence = 1 " +
            "JOIN SellerEntity s ON p.seller.id = s.id " +
            "JOIN StoreEntity st ON s.id = st.seller.id " +
            "LEFT JOIN ReviewEntity r ON r.product.id = p.id " +
            "WHERE st.url = :storeUrl " +
            "  AND p.isDeleted = false " +
            "GROUP BY p.id, p.name, uc.name, dc.name, p.price, pi.url " +
            "ORDER BY p.saleCount DESC, p.id DESC",
            ProductVO.class
        )
        .setParameter("storeUrl", storeUrl)
        .setMaxResults(3)
        .getResultList();
    }

    /**
     * 특정 스토어 대표상품 조회 (isFlagship = true, 상위 3개)
     * - 썸네일 1장만
     * - 평균 평점 포함
     * - 정렬: 최신 등록 우선(id DESC), 필요시 saleCount 보조 정렬 추가 가능
     */
    public List<ProductVO> findFlagshipProductsByStore(String storeUrl) {
        return em.createQuery(
            "SELECT new com.create.chacha.domains.shared.product.vo.ProductVO(" +
            "  p.id, p.name, uc.name, dc.name, p.price, pi.url, COALESCE(AVG(r.rating), 0)) " +
            "FROM ProductEntity p " +
            "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN ProductImageEntity pi ON pi.product.id = p.id " +
            "  AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
            "  AND pi.imageSequence = 1 " +
            "JOIN SellerEntity s ON p.seller.id = s.id " +
            "JOIN StoreEntity st ON s.id = st.seller.id " +
            "LEFT JOIN ReviewEntity r ON r.product.id = p.id " +
            "WHERE st.url = :storeUrl " +
            "  AND p.isDeleted = false " +
            "  AND p.isFlagship = true " +
            "GROUP BY p.id, p.name, uc.name, dc.name, p.price, pi.url " +
            "ORDER BY p.id DESC",
            ProductVO.class
        )
        .setParameter("storeUrl", storeUrl)
        .setMaxResults(3)
        .getResultList();
    }

    /**
     * 특정 스토어 전체상품 조회 (조건 없음, 최신순)
     * - 썸네일 1장만
     * - 평균 평점 포함
     */
    public List<ProductVO> findAllProductsByStore(String storeUrl) {
        return em.createQuery(
            "SELECT new com.create.chacha.domains.shared.product.vo.ProductVO(" +
            "  p.id, p.name, uc.name, dc.name, p.price, pi.url, COALESCE(AVG(r.rating), 0)) " +
            "FROM ProductEntity p " +
            "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN ProductImageEntity pi ON pi.product.id = p.id " +
            "  AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
            "  AND pi.imageSequence = 1 " +
            "JOIN SellerEntity s ON p.seller.id = s.id " +
            "JOIN StoreEntity st ON s.id = st.seller.id " +
            "LEFT JOIN ReviewEntity r ON r.product.id = p.id " +
            "WHERE st.url = :storeUrl " +
            "  AND p.isDeleted = false " +
            "GROUP BY p.id, p.name, uc.name, dc.name, p.price, pi.url " +
            "ORDER BY p.id DESC",
            ProductVO.class
        )
        .setParameter("storeUrl", storeUrl)
        .getResultList();
    }

    /**
     * 필터링/검색 조건에 맞는 상품 조회
     * - 카테고리 필터(상/하) & 키워드 검색
     * - 썸네일 1장만
     * - 평균 평점 포함
     * - 정렬: latest/order/view/rating/price_low/price_high
     */
    public List<ProductVO> findProductsByFilter(String storeUrl, ProductFilterRequestDTO filterDTO) {
        StringBuilder jpql = new StringBuilder(
            "SELECT new com.create.chacha.domains.shared.product.vo.ProductVO(" +
            "  p.id, p.name, uc.name, dc.name, p.price, pi.url, COALESCE(AVG(r.rating), 0)) " +
            "FROM ProductEntity p " +
            "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN ProductImageEntity pi ON pi.product.id = p.id " +
            "  AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
            "  AND pi.imageSequence = 1 " +
            "JOIN SellerEntity s ON p.seller.id = s.id " +
            "JOIN StoreEntity st ON s.id = st.seller.id " +
            "LEFT JOIN ReviewEntity r ON r.product.id = p.id " +
            "WHERE st.url = :storeUrl " +
            "  AND p.isDeleted = false "
        );

        // 카테고리 필터
        if ("ucategory".equalsIgnoreCase(filterDTO.getCategoryType()) && filterDTO.getCategoryId() != null) {
            jpql.append("AND uc.id = :categoryId ");
        } else if ("dcategory".equalsIgnoreCase(filterDTO.getCategoryType()) && filterDTO.getCategoryId() != null) {
            jpql.append("AND dc.id = :categoryId ");
        }

        // 검색어 (상품명 LIKE 검색)
        if (filterDTO.getKeyword() != null && !filterDTO.getKeyword().isEmpty()) {
            jpql.append("AND p.name LIKE CONCAT('%', :keyword, '%') ");
        }

        // 평균 평점 사용 → GROUP BY 필수
        jpql.append("GROUP BY p.id, p.name, uc.name, dc.name, p.price, pi.url ");

        // 정렬 조건
        String sort = (filterDTO.getSort() != null) ? filterDTO.getSort() : "latest";
        switch (sort) {
            case "order"      -> jpql.append("ORDER BY p.saleCount DESC, p.id DESC");
            case "view"       -> jpql.append("ORDER BY p.viewCount DESC, p.id DESC");
            case "rating"     -> jpql.append("ORDER BY AVG(r.rating) DESC, p.id DESC");
            case "price_low"  -> jpql.append("ORDER BY p.price ASC, p.id DESC");
            case "price_high" -> jpql.append("ORDER BY p.price DESC, p.id DESC");
            default           -> jpql.append("ORDER BY p.id DESC"); // 최신순
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
