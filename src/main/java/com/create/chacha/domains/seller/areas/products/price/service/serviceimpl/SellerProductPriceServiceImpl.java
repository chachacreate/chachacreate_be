package com.create.chacha.domains.seller.areas.products.price.service.serviceimpl;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.create.chacha.domains.seller.areas.products.price.dto.response.ProductPriceRecommendSimpleResponseDTO;
import com.create.chacha.domains.seller.areas.products.price.service.SellerProductPriceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerProductPriceServiceImpl implements SellerProductPriceService {

    @Override
    @Transactional
    public ProductPriceRecommendSimpleResponseDTO previewPriceByFiles(String storeUrl, List<MultipartFile> images) {
        try {
            int h = hashFiles(storeUrl, images);
            int recommended = computeMockPrice(h);
            return new ProductPriceRecommendSimpleResponseDTO(recommended);
        } catch (Exception e) {
            log.error("가격 추천 처리 중 오류: {}", e.getMessage(), e);
            return null; // 컨트롤러에서 500으로 매핑
        }
    }

    // ===== 모의 가격 산출 (결정적) =====
    private int computeMockPrice(int h) {
        // base: 50,000 ~ 200,000 (1,000원 단위)
        int base = 50_000 + Math.floorMod(h, 151) * 1_000;   // 50,000..200,000
        int stepPercent = Math.floorMod(h >>> 1, 11) - 5;    // -5..+5
        double delta = stepPercent / 100.0;
        return roundToHundreds((int) Math.max(1_000, Math.round(base * (1 + delta))));
    }

    private int roundToHundreds(int v) {
        int q = Math.round(v / 100f);
        return q * 100;
    }

    private int hashFiles(String storeUrl, List<MultipartFile> images) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // storeUrl 포함 → 스토어마다 결과 달라짐
        md.update(storeUrl.getBytes(StandardCharsets.UTF_8));
        for (MultipartFile f : images) {
            if (f.getOriginalFilename() != null) {
                md.update(f.getOriginalFilename().getBytes(StandardCharsets.UTF_8));
            }
            md.update(f.getBytes()); // 파일 내용 포함
        }
        byte[] d = md.digest();
        return ByteBuffer.wrap(d, 0, 4).getInt(); // 상위 4바이트 → int
    }
}
