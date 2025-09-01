package com.create.chacha.domains.buyer.areas.mainhome.main.repository;

import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeDTO;
import com.create.chacha.domains.shared.entity.store.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MainStoreRepository extends JpaRepository<StoreEntity, Long> {

    // 인기스토어 조회 (down_category 기준)
    @Query("SELECT new com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeDTO(" +
            "s.id, s.name, s.url, s.logo, s.content, " +
            "dc.name, s.saleCount, 1) " +  // rnk를 1로 고정
            "FROM StoreEntity s " +
            "JOIN ProductEntity p ON s.seller.id = p.seller.id " +
            "JOIN DownCategoryEntity dc ON p.downCategory.id = dc.id " +
            "WHERE s.isDeleted = false " +
            "AND p.isDeleted = false " +
            "AND s.saleCount IS NOT NULL " +
            "GROUP BY s.id, s.name, s.url, s.logo, s.content, dc.name, s.saleCount " +
            "ORDER BY s.saleCount DESC")
    List<HomeDTO> findBestStores(Pageable pageable);
}
