package com.create.chacha.common.util;


import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.create.chacha.common.util.dto.LegacyResponse;
import com.create.chacha.common.util.dto.LegacySellerDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import com.create.chacha.domains.seller.areas.main.dashboard.dto.response.LegacyOrderStatusResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class LegacyAPIUtil {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 재사용

    public LegacyStoreDTO getLegacyStoreData(String storeUrl) {
        String url = "http://localhost:9999/legacy/info/store/" + storeUrl; // legacy API 주소
        ResponseEntity<LegacyResponse<LegacyStoreDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacyStoreDTO>>() {}
                );

        return response.getBody().getData();
    }

    public LegacySellerDTO getLegacySellerData(String storeUrl) {
        String url = "http://localhost:9999/legacy/info/seller/" + storeUrl;

        ResponseEntity<LegacyResponse<LegacySellerDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacySellerDTO>>() {}
                );

        return response.getBody().getData();
    }

    public LegacyStoreDTO getLegacyStoreDataById(Long storeId) {
        String url = "http://localhost:9999/legacy/info/store/id/" + storeId;

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
    
    public List<LegacyOrderStatusResponseDTO> getLegacyStatusList(String storeUrl) {
        String url = "http://localhost:9999/legacy/" + storeUrl + "/seller/main";

        ResponseEntity<LegacyResponse<Map<String, Object>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<Map<String, Object>>>() {}
                );

        Map<String, Object> data = response.getBody().getData();

        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        List<LegacyOrderStatusResponseDTO> statusList =
                mapper.convertValue(data.get("statusList"), new TypeReference<List<LegacyOrderStatusResponseDTO>>() {});

        return statusList;
    }
    

}
