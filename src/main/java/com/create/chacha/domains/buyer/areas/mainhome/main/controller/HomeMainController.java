package com.create.chacha.domains.buyer.areas.mainhome.main.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.service.MainPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class HomeMainController {
    private final MainPageService mainPageService;

    // 메인 홈 메인페이지에서 인기스토어,인기상품,최신상품조회 (기존 URL 유지)
    @GetMapping("/main")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMainPageData() {
        Map<String, Object> result = mainPageService.getHomeMainProductMap();
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, result));
    }

    // 메인홈 전체상품 조회 (기존 URL과 파라미터 유지, List 응답)
    @GetMapping("/main/products")
    public ResponseEntity<ApiResponse<List<HomeProductDTO>>> getProductList(
            @RequestParam(required = false) List<String> d,     // down_category IDs
            @RequestParam(required = false) List<String> u,     // up_category IDs
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort) {

        List<HomeProductDTO> result = mainPageService.getProductList(
                null, d, u, keyword, sort);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, result));
    }
}
