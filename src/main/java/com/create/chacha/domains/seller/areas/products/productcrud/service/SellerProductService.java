package com.create.chacha.domains.seller.areas.products.productcrud.service;

import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductCreateRequestDTO;

import java.util.List;

public interface SellerProductService {
    /** storeUrl 기준으로 seller 연관을 주입해서 다중 상품 생성 */
    List<Long> createProducts(String storeUrl, List<ProductCreateRequestDTO> reqs);
}
