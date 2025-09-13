package com.create.chacha.common.util;

import com.create.chacha.common.util.dto.LegacyProductDTO;
import com.create.chacha.common.util.dto.LegacyResponse;
import com.create.chacha.common.util.dto.LegacySellerDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.LegacyOrderStatusResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 레거시 API 호출 유틸
 * - 기존 메서드는 유지
 * - /legacy/{storeUrl}/seller/main 호출은 HTML이 섞여올 수 있어 안전 메서드 추가
 */
@Component
public class LegacyAPIUtil {

    @Value("${spring.legacy.api.url}")
    private String legacyApiUrl; // 예) http://localhost:9999/legacy

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 재사용

    /* ========================= 기존 메서드 (유지) ========================= */

    public LegacyStoreDTO getLegacyStoreData(String storeUrl) {
        String url = legacyApiUrl + "/info/store/" + storeUrl; // /legacy/info/store/{storeUrl}
        ResponseEntity<LegacyResponse<LegacyStoreDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacyStoreDTO>>() {}
                );
        return response.getBody().getData();
    }

    public LegacySellerDTO getLegacySellerData(String storeUrl) {
        String url = legacyApiUrl + "/info/seller/" + storeUrl; // /legacy/info/seller/{storeUrl}
        ResponseEntity<LegacyResponse<LegacySellerDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacySellerDTO>>() {}
                );
        return response.getBody().getData();
    }

    public LegacyStoreDTO getLegacyStoreDataById(Long storeId) {
        String url = legacyApiUrl + "/info/store/id/" + storeId; // /legacy/info/store/id/{storeId}
        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        String body = response.getBody();
        if (body == null || body.isBlank()) return null;

        try {
            LegacyResponse<LegacyStoreDTO> wrapped =
                objectMapper.readValue(body, new TypeReference<LegacyResponse<LegacyStoreDTO>>() {});
            if (wrapped != null && wrapped.getData() != null) return wrapped.getData();
        } catch (Exception ignore) {}

        try {
            return objectMapper.readValue(body, LegacyStoreDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ⚠️ 기존 버전: JSON 컨텐츠타입일 때만 정상 동작.
     * HTML 로그인 페이지가 떨어지면 UnknownContentTypeException 발생 가능.
     */
    public List<LegacyOrderStatusResponseDTO> getLegacyStatusList(String storeUrl) {
        // 주의: base + "/" + storeUrl + "/seller/main" 형태가 안전
        String url = legacyApiUrl + "/" + storeUrl + "/seller/main";
        ResponseEntity<LegacyResponse<Map<String, Object>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<Map<String, Object>>>() {}
                );

        Map<String, Object> data = response.getBody().getData();
        @SuppressWarnings("unchecked")
        List<LegacyOrderStatusResponseDTO> statusList =
                objectMapper.convertValue(
                        data.get("statusList"),
                        new TypeReference<List<LegacyOrderStatusResponseDTO>>() {}
                );
        return statusList;
    }

    public LegacyProductDTO getLegacyProductData(Long productId) {
        String url = legacyApiUrl + "/info/product/" + productId;
        ResponseEntity<LegacyResponse<LegacyProductDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacyProductDTO>>() {}
                );
        return response.getBody().getData();
    }

    // 상품 주문 취소/환불 시 legacy로 orderDetail 테이블 orderStatus 업데이트 요청
    public void updateLegacyOrderStatus(Long orderDetailId, String status) {
        String url = legacyApiUrl + "/order/status/update";
        Map<String, Object> body = Map.of(
                "orderDetailId", orderDetailId,
                "orderStatus", status
        );

        // POST 요청의 body 형식이 JSON임을 알림
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<LegacyResponse<Void>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<LegacyResponse<Void>>() {}
        );
    }

    /* ========================= 신규 메서드 (안전 파서 + 쿠키 지원) ========================= */

    /**
     * 레거시 판매자 메인 데이터 조회 (statusList, reviewList, orderSumList)
     * - String으로 받아 수동 파싱 → 레거시가 text/html을 내려도 방어 가능
     * - jsessionId가 있으면 Cookie 헤더로 전달(비로그인 HTML 응답 방지)
     */
    public LegacyMainPayload fetchLegacyMain(String storeUrl, @Nullable String jsessionId) {
        final String url = UriComponentsBuilder.fromHttpUrl(legacyApiUrl)
                .pathSegment(storeUrl, "seller", "main") // /legacy/{storeUrl}/seller/main
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        if (jsessionId != null && !jsessionId.isBlank()) {
            headers.add(HttpHeaders.COOKIE, "JSESSIONID=" + jsessionId);
        }

        ResponseEntity<String> res = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class
        );

        MediaType ct = res.getHeaders().getContentType();
        String body = res.getBody();

        // 로그인 안 된 경우 HTML이 내려오는 케이스 탐지
        if ((ct != null && ct.isCompatibleWith(MediaType.TEXT_HTML)) ||
            (body != null && body.trim().startsWith("<!DOCTYPE html"))) {
            throw new IllegalStateException("Legacy returned HTML (likely unauthenticated). status=" +
                    res.getStatusCodeValue() + ", snippet=" + snippet(body));
        }

        try {
            // {"status":200,"message":"...","data":{ statusList, reviewList, orderSumList }}
            LegacyResponse<LegacyMainPayload> wrapper =
                    objectMapper.readValue(body, new TypeReference<LegacyResponse<LegacyMainPayload>>() {});
            return wrapper.getData();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse legacy main payload. bodySnippet=" + snippet(body), e);
        }
    }

    /**
     * 판매자 메인 statusList만 필요할 때 사용 (쿠키 전달 포함)
     */
    public List<LegacyOrderStatusResponseDTO> getLegacyStatusListSafe(String storeUrl, @Nullable String jsessionId) {
        LegacyMainPayload payload = fetchLegacyMain(storeUrl, jsessionId);
        return payload != null && payload.getStatusList() != null
                ? payload.getStatusList()
                : List.of();
    }

    private String snippet(String s) {
        if (s == null) return "null";
        String t = s.replaceAll("\\s+", " ");
        return t.substring(0, Math.min(200, t.length()));
    }

    /* ---------------- 파싱용 DTO ---------------- */
    @Getter @Setter @ToString
    public static class LegacyMainPayload {
        private List<LegacyOrderStatusResponseDTO> statusList;
        private List<Map<String, Object>> reviewList;
        private List<Map<String, Object>> orderSumList;
    }
}
