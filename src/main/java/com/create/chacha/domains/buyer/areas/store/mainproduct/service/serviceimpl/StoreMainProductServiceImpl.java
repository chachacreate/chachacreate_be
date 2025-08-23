package com.create.chacha.domains.buyer.areas.store.mainproduct.service.serviceimpl;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request.ProductFilterRequestDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.repository.ProductQueryRepository;
import com.create.chacha.domains.buyer.areas.store.mainproduct.service.StoreMainProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 구매자 스토어 메인 - 상품 조회 Service 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StoreMainProductServiceImpl implements StoreMainProductService {

    private final ProductQueryRepository productQueryRepository;

    /**
     * 인기상품 조회 (판매량 상위 3개)
     */
    @Override
    public List<ProductResponseDTO> getBestProductsByStore(String storeUrl) {
        log.info("스토어 인기상품 조회 Service 실행, storeUrl={}", storeUrl);
        return productQueryRepository.findBestProductsByStore(storeUrl);
    }

    /**
     * 대표상품 조회 (isFlagship = true, 3개)
     */
    @Override
    public List<ProductResponseDTO> getFlagshipProductsByStore(String storeUrl) {
        log.info("스토어 대표상품 조회 Service 실행, storeUrl={}", storeUrl);
        return productQueryRepository.findFlagshipProductsByStore(storeUrl);
    }

    /**
     * 전체상품 조회 (대표상품 조건 제거)
     */
    @Override
    public List<ProductResponseDTO> getAllProductsByStore(String storeUrl) {
        log.info("스토어 전체상품 조회 Service 실행, storeUrl={}", storeUrl);
        return productQueryRepository.findAllProductsByStore(storeUrl);
    }


    @Override
    public List<ProductResponseDTO> getFilteredProductsByStore(String storeUrl, ProductFilterRequestDTO filterDTO) {
        log.info("스토어 전체상품 조회 Service 실행, storeUrl={}, filter={}", storeUrl, filterDTO);
        return productQueryRepository.findProductsByFilter(storeUrl, filterDTO);
    }
}
