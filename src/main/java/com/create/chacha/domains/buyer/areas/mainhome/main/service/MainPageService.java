package com.create.chacha.domains.buyer.areas.mainhome.main.service;

import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO;
import com.create.chacha.domains.shared.constants.DownCategoryEnum;
import com.create.chacha.domains.shared.constants.UpCategoryEnum;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.MainProductRepository;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.MainStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MainPageService {

    private final MainProductRepository productRepository;
    private final MainStoreRepository storeRepository;

    // 상품 리스트 전체 조회 (필터링 및 정렬 포함)
public List<HomeProductDTO> getProductList(Long storeId,
                                           List<String> d,
                                           List<String> u,
                                           String keyword,
                                           String sort) {

        List<HomeProductDTO> products = productRepository.findProductListWithFilters(
                storeId, getDownCategoryId(d), getUpCategoryId(u));

        // 정렬 적용
        if (sort != null) {
            switch (sort) {
                case "latest":
                    products.sort((p1, p2) -> p2.getProductDate().compareTo(p1.getProductDate()));
                    break;
                case "popular":
                    products.sort((p1, p2) -> Integer.compare(p2.getSaleCnt(), p1.getSaleCnt()));
                    break;
                case "lowprice":
                    products.sort(Comparator.comparing(HomeProductDTO::getPrice));
                    break;
                case "highprice":
                    products.sort((p1, p2) -> Integer.compare(p2.getPrice(), p1.getPrice()));
                    break;
                default:
                    products.sort((p1, p2) -> p2.getProductDate().compareTo(p1.getProductDate()));
                    break;
            }
        }

        // 키워드가 존재하면 전용 쿼리 실행
        if (keyword != null && !keyword.isEmpty()) {
            log.info("🔍 상품명 검색 요청: {}", keyword);
            return searchByProductName(keyword);
        }

        return products;
    }

    /** 🏠 메인 홈 - 인기 스토어 + 인기 상품 + 신상품 */
    public Map<String, Object> getHomeMainProductMap() {
        return Map.of(
                "bestStore", getBestStores(),
                "bestProduct", getBestProducts(null),
                "newProduct", getNewProducts()
        );
    }

    /** 🛍️ 스토어 메인 페이지 - 인기 + 대표 상품 묶음 */
    public Map<String, List<HomeProductDTO>> getStoreMainProductMap(Long storeId) {
        return Map.of(
                "bestProduct", getBestProducts(storeId),
                "mainProduct", getStoreMainProducts(storeId)
        );
    }

    // 상품명으로 검색
    public List<HomeProductDTO> searchByProductName(String keyword) {
        return productRepository.findByProductName(keyword);
    }

    // 인기상품 조회
    public List<HomeProductDTO> getBestProducts(Long storeId) {
        int limit = (storeId != null) ? 3 : 10;
        Pageable pageable = PageRequest.of(0, limit);
        Page<HomeProductDTO> page = productRepository.findBestProducts(storeId, pageable);
        return page.getContent();
    }

    // 스토어 대표상품 조회
    public List<HomeProductDTO> getStoreMainProducts(Long storeId) {
        Pageable pageable = PageRequest.of(0, 3);
        return productRepository.findStoreMainProducts(storeId, pageable);
    }

    // 인기스토어 조회
    public List<HomeDTO> getBestStores() {
        Pageable pageable = PageRequest.of(0, 10);
        return storeRepository.findBestStores(pageable);
    }

    // 최신상품 조회
    public List<HomeProductDTO> getNewProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        return productRepository.findNewProducts(pageable);
    }
    // downCategory 이름으로 id조회
    public List<Long> getDownCategoryId(List<String> d){
        List<Long> list = new ArrayList<>();
        d.stream().forEach(dc -> {
            list.add(DownCategoryEnum.valueOf(dc).getId());
        });
        return list;
    }
    // upCategory 이름으로 id조회
    public List<Long> getUpCategoryId(List<String> u){
        List<Long> list = new ArrayList<>();
        u.stream().forEach(uc -> {
            list.add(UpCategoryEnum.valueOf(uc).getId());
        });
        return list;
    }
}
