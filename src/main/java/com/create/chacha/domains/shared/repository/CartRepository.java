package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.buyer.areas.cart.dto.response.CartDTO;
import com.create.chacha.domains.buyer.areas.cart.dto.response.CartViewDTO;
import com.create.chacha.domains.shared.entity.member.CartEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<CartEntity, Long> {
    // 회원별 장바구니 조회 (JPQL 방식)
    @Query("SELECT new com.create.chacha.domains.buyer.areas.cart.dto.response.CartDTO(" +
            "c.id, c.member.id, c.product.id, c.quantity, " +  // 엔티티 대신 ID 사용
            "p.name, p.detail, p.price, p.stock, " +
            "st.id, st.name, st.url, " +
            "(SELECT pi.url FROM ProductImageEntity pi " +
            " WHERE pi.product = p " +
            " AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
            " AND pi.imageSequence = 1 " +
            " AND pi.isDeleted = false " +
            " ORDER BY pi.imageSequence ASC LIMIT 1)) " +
            "FROM CartEntity c " +
            "JOIN c.product p " +
            "JOIN p.seller s " +
            "JOIN StoreEntity st ON st.seller.id = s.id " +
            "WHERE c.member.id = :memberId " +
            "AND p.isDeleted = false " +
            "AND st.isDeleted = false")
    List<CartDTO> findCartItemsByMemberId(@Param("memberId") Long memberId);

    // 상품별로 조회
    List<CartEntity> findByMember_IdAndProduct_Id(Long memberId, Long productId);

    // 상품별 조회 + view에 필요한 정보
    // CartRepository에 추가할 메소드
    @Query("SELECT new com.create.chacha.domains.buyer.areas.cart.dto.response.CartViewDTO(" +
            "c.id, c.member.id, c.product.id, c.quantity, " +
            "p.name, p.detail, p.price, p.stock, " +
            "st.id, st.name, st.url, " +
            "(SELECT pi.url FROM ProductImageEntity pi " +
            " WHERE pi.product = p " +
            " AND pi.status = com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL " +
            " AND pi.imageSequence = 1 " +
            " AND pi.isDeleted = false " +
            " ORDER BY pi.imageSequence ASC LIMIT 1)) " +
            "FROM CartEntity c " +
            "JOIN c.product p " +
            "JOIN p.seller s " +
            "JOIN StoreEntity st ON st.seller.id = s.id " +
            "WHERE c.member.id = :memberId " +
            "AND c.product.id = :productId " +
            "AND p.isDeleted = false " +
            "AND st.isDeleted = false")
    Optional<CartViewDTO> findCartViewItem(@Param("memberId") Long memberId, @Param("productId") Long productId);

    // CartRepository에 추가할 메소드
    @Modifying
    @Query("UPDATE CartEntity c SET c.quantity = :productCnt WHERE c.id = :cartId")
    int updateProductCount(@Param("cartId") Long cartId, @Param("productCnt") Integer productCnt);

    void deleteById(Long cartId);

    void deleteByMember_Id(Long memberId);
}