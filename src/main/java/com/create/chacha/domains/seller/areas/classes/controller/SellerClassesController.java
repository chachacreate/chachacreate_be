package com.create.chacha.domains.seller.areas.classes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.seller.areas.classes.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.dto.request.ClassDeletionToggleRequestDTO;
import com.create.chacha.domains.seller.areas.classes.dto.response.ClassCreateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.service.serviceimpl.SellerClassServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/seller/{storeUrl}")
@RequiredArgsConstructor
@Slf4j
public class SellerClassesController {
	
	private final SellerClassServiceImpl sellerClassService;
	
	// 삭제/복구 다중 토글(0↔1)
	@PatchMapping("/classes/delete")
    public ResponseEntity<ClassDeletionToggleResponseDTO> toggleDeletion(
            @PathVariable("storeUrl") String storeUrl,
            @RequestBody ClassDeletionToggleRequestDTO request
    ) {
        ClassDeletionToggleResponseDTO result =
                sellerClassService.toggleClassesDeletion(storeUrl, request.getClassIds());
        return ResponseEntity.ok(result);
    }
	
	// 클래스 조회
	@GetMapping("/classes")
    public ResponseEntity<List<ClassListItemResponseDTO>> getClasses(@PathVariable("storeUrl") String storeUrl) {
        return ResponseEntity.ok(sellerClassService.getClassesByStoreUrl(storeUrl));
    }
	
	// 클래스 등록
    @PostMapping("/classes")
    public ResponseEntity<ClassCreateResponseDTO> createClasses(
            @PathVariable("storeUrl") String storeUrl,      // <-- URL에서 받음
            @RequestBody List<ClassCreateRequestDTO> payload
    ) {
        var ids = sellerClassService.createClasses(storeUrl, payload);
        return ResponseEntity.ok(new ClassCreateResponseDTO(ids, ids.size()));
    }
}
