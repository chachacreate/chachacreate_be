package com.create.chacha.domains.seller.areas.classes.classcrud.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassDeletionToggleRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateFormResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.service.SellerClassService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/seller/{storeUrl}", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class SellerClassesController {

    private final SellerClassService sellerClassService;

    // =========================
    // 클래스 수정 (멀티파트)
    // =========================
    @PatchMapping(value = "/classes/{classId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ClassUpdateResponseDTO>> update(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("classId") Long classId,
            @RequestPart(value = "clazz", required = false) ClassUpdateRequestDTO.ClassCorePayload clazz,
            @RequestPart(value = "thumbnails", required = false) MultipartFile[] thumbnails,
            @RequestPart(value = "descriptions", required = false) MultipartFile[] descriptions,
            @RequestPart(value = "replaceDescriptionSeqs", required = false) Integer[] replaceDescriptionSeqs
    ) {
        ClassUpdateRequestDTO req = new ClassUpdateRequestDTO();
        req.setClazz(clazz);
        req.setThumbnails(thumbnails);
        req.setDescriptions(descriptions);
        req.setReplaceDescriptionSeqs(replaceDescriptionSeqs);

        ClassUpdateResponseDTO body = sellerClassService.updateClass(storeUrl, classId, req);

        // 서비스가 null 반환 시 404
        if (body == null) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_FORM_NOT_FOUND.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_FORM_NOT_FOUND, null));
        }

        return ResponseEntity
                .status(ResponseCode.SELLER_CLASS_UPDATE_OK.getStatus())
                .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_UPDATE_OK, body));
    }

    // =========================
    // 클래스 수정 폼 조회
    // =========================
    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassUpdateFormResponseDTO>> getClassForUpdate(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("classId") Long classId
    ) {
        ClassUpdateFormResponseDTO body = sellerClassService.getClassForUpdate(storeUrl, classId);

        if (body == null) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_FORM_NOT_FOUND.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_FORM_NOT_FOUND, null));
        }
        return ResponseEntity
                .status(ResponseCode.SELLER_CLASS_FORM_FOUND.getStatus())
                .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_FORM_FOUND, body));
    }

    // =========================
    // 삭제/복구 다중 토글 (0 ↔ 1)
    // =========================
    @PatchMapping("/classes/delete")
    public ResponseEntity<ApiResponse<ClassDeletionToggleResponseDTO>> toggleDeletion(
            @PathVariable("storeUrl") String storeUrl,
            @RequestBody ClassDeletionToggleRequestDTO request
    ) {
        // 요청 자체가 비었을 때 400
        if (request == null || request.getClassIds() == null || request.getClassIds().isEmpty()) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_TOGGLE_BAD_REQUEST.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_TOGGLE_BAD_REQUEST, null));
        }

        ClassDeletionToggleResponseDTO body =
                sellerClassService.toggleClassesDeletion(storeUrl, request.getClassIds());

        // 서비스에서 대상 없음 등으로 null 주면 404
        if (body == null) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_TOGGLE_NOT_FOUND.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_TOGGLE_NOT_FOUND, null));
        }

        // 필수 카운트가 null이면 요청 불량으로 간주(400)
        if (body.getRequestedCount() == null) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_TOGGLE_BAD_REQUEST.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_TOGGLE_BAD_REQUEST, body));
        }

        int requested = body.getRequestedCount();
        int toggledDeleted  = (body.getToggledToDeletedCount()  == null) ? 0 : body.getToggledToDeletedCount();
        int toggledRestored = (body.getToggledToRestoredCount() == null) ? 0 : body.getToggledToRestoredCount();
        int success = toggledDeleted + toggledRestored;

        if (success == 0) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_TOGGLE_NOT_FOUND.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_TOGGLE_NOT_FOUND, body));
        } else if (success < requested) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_TOGGLE_PARTIAL.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_TOGGLE_PARTIAL, body));
        } else {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_TOGGLE_OK.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_TOGGLE_OK, body));
        }
    }

    // =========================
    // 클래스 목록 조회
    // =========================
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassListItemResponseDTO>>> getClasses(
            @PathVariable("storeUrl") String storeUrl
    ) {
        List<ClassListItemResponseDTO> body = sellerClassService.getClassesByStoreUrl(storeUrl);

        if (body == null || body.isEmpty()) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASSES_NOT_FOUND.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASSES_NOT_FOUND, null));
        }
        return ResponseEntity
                .status(ResponseCode.SELLER_CLASSES_FOUND.getStatus())
                .body(new ApiResponse<>(ResponseCode.SELLER_CLASSES_FOUND, body));
    }

    // =========================
    // 클래스 등록 (멀티파트)
    // =========================
    @PostMapping(value = "/classes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<Long>>> create(
            @PathVariable("storeUrl") String storeUrl,
            @RequestPart("clazzes") String clazzesJson,
            @RequestParam(required = false) MultiValueMap<String, MultipartFile> fileMap
    ) throws Exception {

        ObjectMapper om = new ObjectMapper();

        // 배열/단일 객체 모두 허용
        List<ClassCreateRequestDTO.ClassCorePayload> cores;
        String trimmed = clazzesJson.trim();
        if (trimmed.startsWith("[")) {
            cores = om.readValue(trimmed, new TypeReference<List<ClassCreateRequestDTO.ClassCorePayload>>() {});
        } else {
            cores = List.of(om.readValue(trimmed, ClassCreateRequestDTO.ClassCorePayload.class));
        }

        if (cores.isEmpty()) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_CREATE_BAD_REQUEST.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_CREATE_BAD_REQUEST, null));
        }

        // 요청 DTO 조립 + 파일 매칭
        List<ClassCreateRequestDTO> reqs = new ArrayList<>();
        for (int i = 0; i < cores.size(); i++) {
            ClassCreateRequestDTO r = new ClassCreateRequestDTO();
            r.setClazz(cores.get(i));

            MultipartFile[] thumbs = getFiles(fileMap, "thumbnails_" + i);
            MultipartFile[] descs  = getFiles(fileMap, "descriptions_" + i);

            // 단일 등록 키도 허용
            if (thumbs == null && cores.size() == 1) thumbs = getFiles(fileMap, "thumbnails");
            if (descs  == null && cores.size() == 1)  descs = getFiles(fileMap, "descriptions");

            r.setThumbnails(thumbs);
            r.setDescriptions(descs);
            reqs.add(r);
        }

        List<Long> ids = sellerClassService.createClasses(storeUrl, reqs);

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_CREATE_BAD_REQUEST.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_CREATE_BAD_REQUEST, null));
        } else if (ids.size() < reqs.size()) {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_CREATE_PARTIAL.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_CREATE_PARTIAL, ids));
        } else {
            return ResponseEntity
                    .status(ResponseCode.SELLER_CLASS_CREATE_CREATED.getStatus())
                    .body(new ApiResponse<>(ResponseCode.SELLER_CLASS_CREATE_CREATED, ids));
        }
    }

    // =========================
    // 내부 유틸
    // =========================
    private MultipartFile[] getFiles(MultiValueMap<String, MultipartFile> map, String key) {
        if (map == null) return null;
        List<MultipartFile> list = map.get(key);
        return (list == null || list.isEmpty()) ? null : list.toArray(new MultipartFile[0]);
    }
}
