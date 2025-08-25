package com.create.chacha.domains.seller.areas.classes.classinsert.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.seller.areas.classes.classinsert.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classinsert.dto.response.ClassCreateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classinsert.service.serviceimpl.SellerClassServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/seller/{storeUrl}")
@RequiredArgsConstructor
@Slf4j
public class SellerClassesController {
	
	private final SellerClassServiceImpl sellerClassService;

    @PostMapping("/classes")
    public ResponseEntity<ClassCreateResponseDTO> createClasses(
            @PathVariable("storeUrl") String storeUrl,      // <-- URL에서 받음
            @RequestBody List<ClassCreateRequestDTO> payload
    ) {
        var ids = sellerClassService.createClasses(storeUrl, payload);
        return ResponseEntity.ok(new ClassCreateResponseDTO(ids, ids.size()));
    }
}
