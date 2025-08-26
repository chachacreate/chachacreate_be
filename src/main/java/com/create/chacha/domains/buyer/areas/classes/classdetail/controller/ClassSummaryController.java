package com.create.chacha.domains.buyer.areas.classes.classdetail.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassImagesResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassScheduleResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.service.ClassDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/main/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassSummaryController {

    private final ClassDetailService service;


    @GetMapping("/{classId}")
    public ResponseEntity<ClassSummaryResponseDTO> getSummary(@PathVariable("classId") Long classId) {
    	
    	ClassSummaryResponseDTO response = service.getSummary(classId);
        return ResponseEntity.ok(response);
    	
    }
    
    @GetMapping("/{classId}/images")
    public ResponseEntity<ClassImagesResponseDTO> getImages(@PathVariable("classId") Long classId) {
    	
    	ClassImagesResponseDTO response = service.getImages(classId);
        return ResponseEntity.ok(response);

    }
    
    @GetMapping("/{classId}/schedule")
    public ResponseEntity<List<ClassScheduleResponseDTO>> getSchedule(@PathVariable("classId") Long classId) {
        log.info("클래스 스케줄 조회 - classId: {}", classId);
        List<ClassScheduleResponseDTO> schedule = service.getSchedule(classId);
        return ResponseEntity.ok(schedule);
    }
    
    
}











