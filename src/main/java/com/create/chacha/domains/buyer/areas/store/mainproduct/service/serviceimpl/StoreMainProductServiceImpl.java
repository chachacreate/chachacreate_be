package com.create.chacha.domains.buyer.areas.store.mainproduct.service.serviceimpl;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.repository.ProductQueryRepository;
import com.create.chacha.domains.buyer.areas.store.mainproduct.service.StoreMainProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 구매자 스토어 메인 - 인기상품 조회 Service 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StoreMainProductServiceImpl implements StoreMainProductService {

    private final ProductQueryRepository productQueryRepository;

    @Override
    public List<ProductResponseDTO> getBestProductsByStore(String storeUrl) {
        log.info("스토어 인기상품 조회 Service 실행, storeId={}", storeUrl);
        return productQueryRepository.findBestProductsByStore(storeUrl);
    }
}
