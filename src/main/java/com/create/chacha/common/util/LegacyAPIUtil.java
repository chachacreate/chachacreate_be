package com.create.chacha.common.util;


import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.util.dto.LegacyResponse;
import com.create.chacha.common.util.dto.LegacySellerDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LegacyAPIUtil {
    private final RestTemplate restTemplate = new RestTemplate();

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
        String url = "http://localhost:9999/legacy/info/store/id/" + storeId; // ID 기반 API 주소
        ResponseEntity<LegacyResponse<LegacyStoreDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacyStoreDTO>>() {}
                );

        return response.getBody().getData();
    }

}
