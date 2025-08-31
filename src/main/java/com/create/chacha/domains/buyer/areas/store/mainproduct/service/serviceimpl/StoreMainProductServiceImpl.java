package com.create.chacha.domains.buyer.areas.store.mainproduct.service.serviceimpl;

import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.request.ProductFilterRequestDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.dto.response.ProductResponseDTO;
import com.create.chacha.domains.buyer.areas.store.mainproduct.repository.ProductQueryRepository;
import com.create.chacha.domains.buyer.areas.store.mainproduct.service.StoreMainProductService;
import com.create.chacha.domains.shared.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 구매자 스토어 메인 - 상품 조회 Service 구현체
 * - Repository는 VO로만 반환, Service는 VO→DTO 변환 책임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreMainProductServiceImpl implements StoreMainProductService {

    private final ProductQueryRepository productQueryRepository;

    /**
     * 인기상품 조회 (판매량 상위 3개)
     */
    @Override
    public List<ProductResponseDTO> getBestProductsByStore(String storeUrl) {
        log.info("스토어 인기상품 조회 Service 실행, storeUrl={}", storeUrl);
        List<ProductVO> vos = productQueryRepository.findBestProductsByStore(storeUrl);
        return vos.stream().map(ProductResponseDTO::from).toList();
    }

    /**
     * 대표상품 조회 (isFlagship = true, 3개)
     */
    @Override
    public List<ProductResponseDTO> getFlagshipProductsByStore(String storeUrl) {
        log.info("스토어 대표상품 조회 Service 실행, storeUrl={}", storeUrl);
        List<ProductVO> vos = productQueryRepository.findFlagshipProductsByStore(storeUrl);
        return vos.stream().map(ProductResponseDTO::from).toList();
    }

    /**
     * 전체상품 조회 (최신순)
     */
    @Override
    public List<ProductResponseDTO> getAllProductsByStore(String storeUrl) {
        log.info("스토어 전체상품 조회 Service 실행, storeUrl={}", storeUrl);
        List<ProductVO> vos = productQueryRepository.findAllProductsByStore(storeUrl);
        return vos.stream().map(ProductResponseDTO::from).toList();
    }

    /**
     * 필터/검색 조회
     */
    @Override
    public List<ProductResponseDTO> getFilteredProductsByStore(String storeUrl, ProductFilterRequestDTO filterDTO) {
        log.info("스토어 필터 상품 조회 Service 실행, storeUrl={}, filter={}", storeUrl, filterDTO);
        List<ProductVO> vos = productQueryRepository.findProductsByFilter(storeUrl, filterDTO);
        return vos.stream().map(ProductResponseDTO::from).toList();
    }
}
