package com.create.chacha.domains.seller.areas.products.productcrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.create.chacha.domains.shared.entity.product.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long>{

}
