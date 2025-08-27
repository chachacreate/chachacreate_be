package com.create.chacha.domains.seller.areas.products.price.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.create.chacha.domains.seller.areas.products.price.dto.response.ProductPriceRecommendSimpleResponseDTO;

public interface SellerProductPriceService {
	// 이미지 파일 3장을 받아 추천가 산출
    ProductPriceRecommendSimpleResponseDTO previewPriceByFiles(String storeUrl, List<MultipartFile> images);
}
