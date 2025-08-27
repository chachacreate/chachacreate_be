package com.create.chacha.domains.seller.areas.products.price.service.serviceimpl;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.create.chacha.domains.seller.areas.products.price.dto.response.ProductPriceRecommendSimpleResponseDTO;
import com.create.chacha.domains.seller.areas.products.price.service.SellerProductPriceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerProductPriceServiceImpl implements SellerProductPriceService {

	@Override
    @Transactional
    public ProductPriceRecommendSimpleResponseDTO previewPriceByFiles(String storeUrl, List<MultipartFile> images) {
        // 1) 입력 검증
        if (images == null || images.size() != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일은 정확히 3개여야 합니다.");
        }
        for (MultipartFile f : images) {
            if (f == null || f.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비어있는 이미지 파일이 있습니다.");
            }
            String ct = f.getContentType();
            if (ct == null || !ct.toLowerCase().startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.");
            }
        }

        // 2) 모의 추천가 산출(결정적) — 나중에 여기만 Python API 호출로 교체
        int h = hashFiles(storeUrl, images);
        int recommended = computeMockPrice(h);

        return new ProductPriceRecommendSimpleResponseDTO(recommended);
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

    private int hashFiles(String storeUrl, List<MultipartFile> images) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // storeUrl을 포함해 스토어마다 결과가 달라지도록
            md.update(storeUrl.getBytes(StandardCharsets.UTF_8));
            for (MultipartFile f : images) {
                if (f.getOriginalFilename() != null) {
                    md.update(f.getOriginalFilename().getBytes(StandardCharsets.UTF_8));
                }
                md.update(f.getBytes()); // 파일 내용 포함
            }
            byte[] d = md.digest();
            return ByteBuffer.wrap(d, 0, 4).getInt(); // 상위 4바이트를 int로
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 해시 처리 중 오류", e);
        }
    }
}