package com.create.chacha.domains.seller.areas.products.productcrud.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductCreateRequestDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.service.SellerProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/seller/{storeUrl}")
@RequiredArgsConstructor
@Slf4j
public class SellerProductController {

    private final SellerProductService productService;

    /**
     * 단일/다중 등록 통합 엔드포인트
     *
     * 허용하는 멀티파트 키 (싱글 기준):
     * - products : JSON(Object or Array)
     * - thumbnails  또는 thumbnails_0  또는 thumbnails[]  또는 thumbnails[0]  (3 files 필수)
     * - descriptions 또는 descriptions_0 또는 descriptions[] 또는 descriptions[0] (0..N files)
     *
     * 다중 등록일 때(i번째 항목):
     * - thumbnails_i / thumbnails[i]
     * - descriptions_i / descriptions[i]
     * 없으면 위 싱글 키로 폴백
     */
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<Long>>> create(
            @PathVariable("storeUrl") String storeUrl,
            @RequestPart("products") String productsJson,
            MultipartHttpServletRequest mreq
    ) {
        // 1) JSON → payload 리스트
        ObjectMapper om = new ObjectMapper();
        List<ProductCreateRequestDTO.ProductCorePayload> payloads = new ArrayList<>();
        try {
            JsonNode root = om.readTree(productsJson);
            if (root.isArray()) {
                for (JsonNode n : root) {
                    payloads.add(om.treeToValue(n, ProductCreateRequestDTO.ProductCorePayload.class));
                }
            } else if (root.isObject()) {
                payloads.add(om.treeToValue(root, ProductCreateRequestDTO.ProductCorePayload.class));
            } else {
                throw new IllegalArgumentException("`products` 는 JSON 객체 또는 배열이어야 합니다.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("products JSON 파싱 실패: " + e.getMessage(), e);
        }

        boolean multi = payloads.size() > 1;

        // 2) 파일 매핑
        List<ProductCreateRequestDTO> reqs = new ArrayList<>();
        for (int i = 0; i < payloads.size(); i++) {
            ProductCreateRequestDTO dto = new ProductCreateRequestDTO();
            dto.setProduct(payloads.get(i)); // sellerId는 서비스에서 storeUrl로 주입

            dto.setThumbnails(filesOrNull(mreq, multi, "thumbnails", i));
            dto.setDescriptions(filesOrNull(mreq, multi, "descriptions", i));
            reqs.add(dto);
        }

        if (log.isDebugEnabled()) {
            for (int i = 0; i < reqs.size(); i++) {
                var r = reqs.get(i);
                log.debug("[product#{}] thumbs={}, descs={}",
                        i,
                        r.getThumbnails() == null ? 0 : r.getThumbnails().length,
                        r.getDescriptions() == null ? 0 : r.getDescriptions().length
                );
            }
        }

        // 3) 서비스 호출 (서비스가 storeUrl → seller 프록시 주입)
        List<Long> ids = productService.createProducts(storeUrl, reqs);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.CREATED, ids));
    }

    /** 단일이면 thumbnails / thumbnails_0 / thumbnails[] / thumbnails[0] 등 폭넓게 허용
     *  다중이면 thumbnails_{i} / thumbnails[i] 우선, 없으면 thumbnails / thumbnails_0 로 폴백
     */
    private MultipartFile[] filesOrNull(
            MultipartHttpServletRequest mreq, boolean multi, String baseKey, int idx) {

        // 들어온 멀티파트 키/개수 로그 (INFO)
        Iterator<String> it = mreq.getFileNames();
        while (it.hasNext()) {
            String n = it.next();
            List<MultipartFile> v = mreq.getFiles(n);
            log.info("[multipart] part '{}' -> {} file(s)", n, (v == null ? 0 : v.size()));
        }

        // 우선순위 후보 키
        String[] candidates = multi
                ? new String[]{ baseKey + "_" + idx, baseKey + "[" + idx + "]", baseKey, baseKey + "_0", baseKey + "[]" }
                : new String[]{ baseKey, baseKey + "_0", baseKey + "[]", baseKey + "[" + idx + "]" };

        for (String k : candidates) {
            List<MultipartFile> lst = mreq.getFiles(k);
            if (lst != null && !lst.isEmpty() && lst.stream().anyMatch(f -> f != null && !f.isEmpty())) {
                log.info("[multipart] picked key '{}' ({} files)", k, lst.size());
                return lst.toArray(MultipartFile[]::new);
            }
        }

        // 최후의 보험: baseKey로 시작하는 아무 키 (예: thumbnails_foo, thumbnails[3] 등)
        it = mreq.getFileNames();
        while (it.hasNext()) {
            String k = it.next();
            if (k.equals(baseKey) || k.startsWith(baseKey + "_") || k.startsWith(baseKey + "[")) {
                List<MultipartFile> lst = mreq.getFiles(k);
                if (lst != null && !lst.isEmpty()) {
                    log.info("[multipart] fallback picked key '{}' ({} files)", k, lst.size());
                    return lst.toArray(MultipartFile[]::new);
                }
            }
        }

        log.info("[multipart] no files found for baseKey='{}'", baseKey);
        return null;
    }
}
