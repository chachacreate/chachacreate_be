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
@RequestMapping("/api/seller/{storeUrl}")
@RequiredArgsConstructor
@Slf4j
public class SellerClassesController {

    private final SellerClassService sellerClassService;

    /** 클래스 수정 (멀티파트 + S3 업로드만) */
    @PatchMapping(value = "/classes/{classId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ClassUpdateResponseDTO>> update(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("classId") Long classId,
            @RequestPart(value = "clazz", required = false)
            ClassUpdateRequestDTO.ClassCorePayload clazz,

            @RequestPart(value = "thumbnails", required = false)
            MultipartFile[] thumbnails,
            @RequestPart(value = "thumbnailSeqs", required = false)
            Integer[] thumbnailSeqs,

            @RequestPart(value = "descriptions", required = false)
            MultipartFile[] descriptions,
            @RequestPart(value = "replaceDescriptionSeqs", required = false)
            Integer[] replaceDescriptionSeqs
	    ) {
	        ClassUpdateRequestDTO req = new ClassUpdateRequestDTO();
	        req.setClazz(clazz);
	        req.setThumbnails(thumbnails);
	        req.setThumbnailSeqs(thumbnailSeqs);
	        req.setDescriptions(descriptions);
	        req.setReplaceDescriptionSeqs(replaceDescriptionSeqs);
	
	        ClassUpdateResponseDTO body = sellerClassService.updateClass(storeUrl, classId, req);
	        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
	    }

    // 클래스 수정 페이지 조회
    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassUpdateFormResponseDTO>> getClassForUpdate(
            @PathVariable("storeUrl") String storeUrl,
            @PathVariable("classId") Long classId
    ) {
        var body = sellerClassService.getClassForUpdate(storeUrl, classId);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }
    // 삭제/복구 다중 토글(0↔1)
    @PatchMapping("/classes/delete")
    public ResponseEntity<ApiResponse<ClassDeletionToggleResponseDTO>> toggleDeletion(
            @PathVariable("storeUrl") String storeUrl,
            @RequestBody ClassDeletionToggleRequestDTO request
    ) {
        ClassDeletionToggleResponseDTO body =
                sellerClassService.toggleClassesDeletion(storeUrl, request.getClassIds());
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    // 클래스 조회
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassListItemResponseDTO>>> getClasses(
            @PathVariable("storeUrl") String storeUrl
    ) {
        List<ClassListItemResponseDTO> body = sellerClassService.getClassesByStoreUrl(storeUrl);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    /** 클래스 등록 (멀티파트 + S3 업로드만) */
    @PostMapping(value = "/classes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<Long>>> create(
            @PathVariable("storeUrl") String storeUrl,
            @RequestPart("clazzes") String clazzesJson,                 // 단일/다중 모두 여기로
            @RequestParam(required = false) MultiValueMap<String, MultipartFile> fileMap
    ) throws Exception {

        ObjectMapper om = new ObjectMapper();

        // clazzesJson이 배열이든, 실수로 객체든 모두 처리
        List<ClassCreateRequestDTO.ClassCorePayload> cores;
        String trimmed = clazzesJson.trim();
        if (trimmed.startsWith("[")) {
            cores = om.readValue(trimmed, new TypeReference<List<ClassCreateRequestDTO.ClassCorePayload>>() {});
        } else {
            // 단일 객체가 들어오면 1개짜리 리스트로 변환
            cores = List.of(om.readValue(trimmed, ClassCreateRequestDTO.ClassCorePayload.class));
        }

        List<ClassCreateRequestDTO> reqs = new ArrayList<>();
        for (int i = 0; i < cores.size(); i++) {
            ClassCreateRequestDTO r = new ClassCreateRequestDTO();
            r.setClazz(cores.get(i));

            // 파일은 인덱스로 매칭
            MultipartFile[] thumbs = getFiles(fileMap, "thumbnails_" + i);
            MultipartFile[] descs  = getFiles(fileMap, "descriptions_" + i);

            // (편의) 단일 등록일 때 thumbnails/descriptions 키도 허용
            if (thumbs == null && cores.size() == 1) thumbs = getFiles(fileMap, "thumbnails");
            if (descs  == null && cores.size() == 1) descs  = getFiles(fileMap, "descriptions");

            r.setThumbnails(thumbs);
            r.setDescriptions(descs);
            reqs.add(r);
        }

        List<Long> ids = sellerClassService.createClasses(storeUrl, reqs);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.CREATED, ids));
    }

    private MultipartFile[] getFiles(MultiValueMap<String, MultipartFile> map, String key) {
        if (map == null) return null;
        List<MultipartFile> list = map.get(key);
        return (list == null || list.isEmpty()) ? null : list.toArray(new MultipartFile[0]);
    }

}
