package com.create.chacha.domains.buyer.areas.classes.classdetail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.service.ClassDetailQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/main/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassSummaryController {

    private final ClassDetailQueryService service;

    @GetMapping("/{classId}")
    public ResponseEntity<ClassSummaryResponseDTO> getSummary(@PathVariable("classId") Long classId) {
    	
    	ClassSummaryResponseDTO response = service.getSummary(classId);
        return ResponseEntity.ok(response);
    	
    }
}
