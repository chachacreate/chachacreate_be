package com.create.chacha.domains.buyer.areas.classes.classdetail.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
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
    public ApiResponse<ClassSummaryResponseDTO> getSummary(@PathVariable("classId") Long classId) {
    	
    	ClassSummaryResponseDTO response = service.getSummary(classId);
    	return new ApiResponse<>(ResponseCode.CLASS_SAMMARY_OK, response);
    	
    }
    
    @GetMapping("/{classId}/images")
    public ApiResponse<ClassImagesResponseDTO> getImages(@PathVariable("classId") Long classId) {
    	
    	ClassImagesResponseDTO response = service.getImages(classId);
    	return new ApiResponse<>(ResponseCode.CLASS_IMAGES_OK, response);

    }
    
    @GetMapping("/{classId}/schedule")
    public ApiResponse<List<ClassScheduleResponseDTO>> getSchedule(@PathVariable("classId") Long classId) {
        log.info("클래스 스케줄 조회 - classId: {}", classId);
        List<ClassScheduleResponseDTO> response = service.getSchedule(classId);
        return new ApiResponse<>(ResponseCode.CLASS_SCHEDULE_OK, response);
    }
    
    
}











