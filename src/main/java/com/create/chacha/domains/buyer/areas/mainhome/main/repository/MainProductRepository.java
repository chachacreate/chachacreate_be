package com.create.chacha.domains.buyer.areas.mainhome.main.repository;

import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductListItemDTO;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainProductRepository extends JpaRepository<ProductEntity, Long> {

    // 상품 리스트 조회 (카테고리 필터링 포함)
    @Query("SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO(" +
            "p.id, p.downCategory.id, p.name, p.price, p.detail, p.stock, " +
            "p.createdAt, p.updatedAt, p.saleCount, p.viewCount, st.logo, " +
            "st.content, st.name, dc.name, uc.name, " +
            "(SELECT pi.url FROM ProductImageEntity pi WHERE pi.product = p AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL" +
            " AND pi.imageSequence = 1 AND pi.isDeleted = false ORDER BY pi.imageSequence ASC LIMIT 1), " +
            "st.url) " +
            "FROM ProductEntity p " +
            "LEFT JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "LEFT JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN StoreEntity st ON p.seller.id = st.seller.id " +
            "WHERE p.isDeleted = false " +
            "AND (:storeId IS NULL OR st.id = :storeId) " +
            "AND (:downCategories IS NULL OR p.downCategory.id IN :downCategories) " +
            "AND (:upCategories IS NULL OR uc.id IN :upCategories)")
    List<HomeProductDTO> findProductListWithFilters(
            @Param("storeId") Long storeId,
            @Param("downCategories") List<Long> downCategories,
            @Param("upCategories") List<Long> upCategories
    );

    // 상품명으로 검색
    @Query("SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO(" +
            "p.id, p.downCategory.id, p.name, p.price, p.detail, p.stock, " +
            "p.createdAt, p.updatedAt, p.saleCount, p.viewCount, st.logo, " +
            "st.content, st.name, dc.name, uc.name, " +
            "(SELECT pi.url FROM ProductImageEntity pi WHERE pi.product = p AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL" +
            " AND pi.imageSequence = 1 AND pi.isDeleted = false ORDER BY pi.imageSequence ASC LIMIT 1), " +
            "st.url) " +
            "FROM ProductEntity p " +
            "LEFT JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "LEFT JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN StoreEntity st ON p.seller.id = st.seller.id " +
            "WHERE p.isDeleted = false " +
            "AND p.name LIKE CONCAT('%', :keyword, '%')")
    List<HomeProductDTO> findByProductName(@Param("keyword") String keyword);

    // 인기상품 조회
    @Query("SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO(" +
            "p.id, p.downCategory.id, p.name, p.price, p.detail, p.stock, " +
            "p.createdAt, p.updatedAt, p.saleCount, p.viewCount, st.logo, " +
            "st.content, st.name, dc.name, uc.name, " +
            "(SELECT pi.url FROM ProductImageEntity pi WHERE pi.product = p AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL" +
            " AND pi.imageSequence = 1 AND pi.isDeleted = false ORDER BY pi.imageSequence ASC LIMIT 1), " +
            "st.url) " +
            "FROM ProductEntity p " +
            "LEFT JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "LEFT JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN StoreEntity st ON p.seller.id = st.seller.id " +
            "WHERE p.isDeleted = false " +
            "AND (:storeId IS NULL OR st.id = :storeId) " +
            "ORDER BY p.saleCount DESC")
    Page<HomeProductDTO> findBestProducts(@Param("storeId") Long storeId, Pageable pageable);

    // 대표상품 조회
    @Query("SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO(" +
            "p.id, p.downCategory.id, p.name, p.price, p.detail, p.stock, " +
            "p.createdAt, p.updatedAt, p.saleCount, p.viewCount, st.logo, " +
            "st.content, st.name, dc.name, uc.name, " +
            "(SELECT pi.url FROM ProductImageEntity pi WHERE pi.product = p AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL" +
            " AND pi.imageSequence = 1 AND pi.isDeleted = false ORDER BY pi.imageSequence ASC LIMIT 1), " +
            "st.url) " +
            "FROM ProductEntity p " +
            "LEFT JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "LEFT JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN StoreEntity st ON p.seller.id = st.seller.id " +
            "WHERE p.isDeleted = false " +
            "AND st.id = :storeId " +
            "AND p.isFlagship = true")
    List<HomeProductDTO> findStoreMainProducts(@Param("storeId") Long storeId, Pageable pageable);

    // 최신 상품 조회
    @Query("SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO(" +
            "p.id, p.downCategory.id, p.name, p.price, p.detail, p.stock, " +
            "p.createdAt, p.updatedAt, p.saleCount, p.viewCount, st.logo, " +
            "st.content, st.name, dc.name, uc.name, " +
            "(SELECT pi.url FROM ProductImageEntity pi WHERE pi.product = p AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL" +
            " AND pi.imageSequence = 1 AND pi.isDeleted = false ORDER BY pi.imageSequence ASC LIMIT 1), " +
            "st.url) " +
            "FROM ProductEntity p " +
            "LEFT JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "LEFT JOIN UpCategoryEntity uc ON dc.upCategory.id = uc.id " +
            "LEFT JOIN StoreEntity st ON p.seller.id = st.seller.id " +
            "WHERE p.isDeleted = false " +
            "ORDER BY p.createdAt DESC")
    List<HomeProductDTO> findNewProducts(Pageable pageable);

}
