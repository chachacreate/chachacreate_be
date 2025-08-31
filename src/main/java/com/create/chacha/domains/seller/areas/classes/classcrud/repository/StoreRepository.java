package com.create.chacha.domains.seller.areas.classes.classcrud.repository;

import com.create.chacha.domains.shared.entity.seller.SellerEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findByUrl(String url);

    // ✅ Seller 엔티티를 로딩하지 않고 id만 조회
    @Query("select st.seller.id from StoreEntity st where st.url = :url")
    Optional<Long> findSellerIdByUrl(@Param("url") String url);
}
