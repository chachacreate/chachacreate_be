package com.create.chacha.domains.seller.areas.products.productcrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.create.chacha.domains.shared.entity.product.ProductImageEntity;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

}
