package com.create.chacha.domains.seller.areas.classes.classcrud.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassDeletionToggleRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassCreateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.service.SellerClassService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/seller/{storeUrl}")
@RequiredArgsConstructor
@Slf4j
public class SellerClassesController {

    private final SellerClassService sellerClassService;

    // 클래스 수정 기능
    @PatchMapping("/classes/update")
    public ResponseEntity<ApiResponse<ClassUpdateResponseDTO>> updateClass(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam("classId") Long classId,
            @RequestBody ClassCreateRequestDTO request
    ) {
        ClassUpdateResponseDTO body = sellerClassService.updateClass(storeUrl, classId, request);
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.OK, body));
    }

    // 클래스 수정 페이지 조회
    @GetMapping("/classes/update")
    public ResponseEntity<ApiResponse<ClassCreateRequestDTO>> getClassForUpdate(
            @PathVariable("storeUrl") String storeUrl,
            @RequestParam("classId") Long classId
    ) {
        ClassCreateRequestDTO body = sellerClassService.getClassForUpdate(storeUrl, classId);
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

    // 클래스 등록
    @PostMapping("/classes")
    public ResponseEntity<ApiResponse<ClassCreateResponseDTO>> createClasses(
            @PathVariable("storeUrl") String storeUrl,
            @RequestBody List<ClassCreateRequestDTO> payload
    ) {
        var ids = sellerClassService.createClasses(storeUrl, payload);
        ClassCreateResponseDTO body = new ClassCreateResponseDTO(ids, ids.size());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(ResponseCode.CREATED, body));
    }
}
