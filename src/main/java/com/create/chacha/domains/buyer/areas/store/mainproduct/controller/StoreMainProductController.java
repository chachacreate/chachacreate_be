package com.create.chacha.domains.buyer.areas.store.mainproduct.controller;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.service.StoreMainProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ProductResponseDTO>> getProductsByStore(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam(name = "type") String type
    ) {
        log.info("스토어 상품 조회 API 호출, storeUrl={}, type={}", storeUrl, type);

        List<ProductResponseDTO> products;
        switch (type.toLowerCase()) {
            case "popular" -> products = storeMainProductService.getBestProductsByStore(storeUrl);
            case "flagship" -> products = storeMainProductService.getFlagshipProductsByStore(storeUrl);
            default -> throw new IllegalArgumentException("지원하지 않는 type 값입니다. [popular | flagship]만 허용됩니다.");
        }

        return ResponseEntity.ok(products);
    }

    /**
     * 특정 스토어의 전체상품 조회
     * @param storeUrl 스토어 URL
     * @return 전체 상품 리스트
     */
    @GetMapping("/{storeUrl}/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProductsByStore(
            @PathVariable("storeUrl") String storeUrl
    ) {
        log.info("스토어 전체상품 조회 API 호출, storeUrl={}", storeUrl);
        List<ProductResponseDTO> products = storeMainProductService.getAllProductsByStore(storeUrl);
        return ResponseEntity.ok(products);
    }
}
