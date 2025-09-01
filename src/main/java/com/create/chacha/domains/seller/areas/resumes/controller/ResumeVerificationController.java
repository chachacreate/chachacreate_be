package com.create.chacha.domains.seller.areas.resumes.controller;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.domains.seller.areas.resumes.dto.request.ResumeRequestDTO;
import com.create.chacha.domains.seller.areas.resumes.dto.response.ResumeResponseDTO;
import com.create.chacha.domains.seller.areas.resumes.service.ResumeVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/{storeUrl}/resumes")
public class ResumeVerificationController {

    private final ResumeVerificationService resumeVerificationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ResumeResponseDTO> createResume(
            @PathVariable String storeUrl,
            @RequestBody ResumeRequestDTO request
    ) {
        ResumeResponseDTO response = resumeVerificationService.createResume(storeUrl, request);
        return new ApiResponse<>(ResponseCode.RESUME_UPLOAD_SUCCESS, response);
    }
}

