package com.create.chacha.domains.buyer.areas.store.mainproduct.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request.ProductFilterRequestDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.service.StoreMainProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 구매자 스토어 메인페이지 - 상품 조회 API Controller
 * 
 * 지원 기능:
 *  - 인기상품 조회 (type=popular)
 *  - 대표상품 조회 (type=flagship)
 *  - 전체상품 조회 (/products)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StoreMainProductController {

    private final StoreMainProductService storeMainProductService;

    /**
     * 특정 스토어의 인기/대표 상품 조회
     * @param storeUrl 스토어 URL
     * @param type     조회 타입 (popular | flagship)
     * @return 상품 리스트 (최대 3개)
     */
    @GetMapping("/{storeUrl}")
    public ApiResponse<List<ProductResponseDTO>> getProductsByStore(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam(name = "type") String type
    ) {
        log.info("스토어 상품 조회 API 호출, storeUrl={}, type={}", storeUrl, type);

        List<ProductResponseDTO> products;
        switch (type.toLowerCase()) {
            case "popular" -> {
                products = storeMainProductService.getBestProductsByStore(storeUrl);
                if (products == null || products.isEmpty()) {
                    return new ApiResponse<>(ResponseCode.STORE_POPULAR_PRODUCTS_NOT_FOUND, null);
                }
                return new ApiResponse<>(ResponseCode.STORE_POPULAR_PRODUCTS_FOUND, products);
            }
            case "flagship" -> {
                products = storeMainProductService.getFlagshipProductsByStore(storeUrl);
                if (products == null || products.isEmpty()) {
                    return new ApiResponse<>(ResponseCode.STORE_FLAGSHIP_PRODUCTS_NOT_FOUND, null);
                }
                return new ApiResponse<>(ResponseCode.STORE_FLAGSHIP_PRODUCTS_FOUND, products);
            }
            default -> {
                return new ApiResponse<>(ResponseCode.PRODUCT_TYPE_INVALID, null);
            }
        }
    }

    /**
     * 특정 스토어의 전체상품 조회 (필터링/검색 포함)
     * @param storeUrl 스토어 URL
     * @param filterDTO 필터링/검색 조건
     * @return 상품 리스트
     */
    @GetMapping("/{storeUrl}/products")
    public ApiResponse<List<ProductResponseDTO>> getAllProductsByStore(
            @PathVariable("storeUrl") String storeUrl,
            @ModelAttribute ProductFilterRequestDTO filterDTO
    ) {
        log.info("스토어 전체상품 조회 API 호출, storeUrl={}, filter={}", storeUrl, filterDTO);

        List<ProductResponseDTO> products = storeMainProductService.getFilteredProductsByStore(storeUrl, filterDTO);

        if (products == null || products.isEmpty()) {
            return new ApiResponse<>(ResponseCode.STORE_ALL_PRODUCTS_NOT_FOUND, null);
        }
        return new ApiResponse<>(ResponseCode.STORE_ALL_PRODUCTS_FOUND, products);
    }

}
