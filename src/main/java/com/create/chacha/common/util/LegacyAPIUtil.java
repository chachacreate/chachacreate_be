package com.create.chacha.common.util;


import com.create.chacha.common.util.dto.LegacyResponse;
import com.create.chacha.common.util.dto.LegacySellerDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class LegacyAPIUtil {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.legacy.api.url}")
    private String legacyApiUrl;

    public LegacyStoreDTO getLegacyStoreData(String storeUrl) {
        String url = legacyApiUrl + "/info/store/" + storeUrl; // legacy API 주소
        ResponseEntity<LegacyResponse<LegacyStoreDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacyStoreDTO>>() {}
                );
        log.debug(response.getBody().getData().toString());
        return response.getBody().getData();
    }

    public LegacySellerDTO getLegacySellerData(String storeUrl) {
        String url = legacyApiUrl + "/info/seller/" + storeUrl;

        ResponseEntity<LegacyResponse<LegacySellerDTO>> response =
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<LegacyResponse<LegacySellerDTO>>() {}
                );
        log.debug(response.getBody().getData().toString());
        return response.getBody().getData();
    }
}
